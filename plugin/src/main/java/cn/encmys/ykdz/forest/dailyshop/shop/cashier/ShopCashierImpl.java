package cn.encmys.ykdz.forest.dailyshop.shop.cashier;

import cn.encmys.ykdz.forest.dailyshop.api.DailyShop;
import cn.encmys.ykdz.forest.dailyshop.api.event.shop.ShopPreSettleEvent;
import cn.encmys.ykdz.forest.dailyshop.api.event.shop.ShopSettleEvent;
import cn.encmys.ykdz.forest.dailyshop.api.price.enums.PriceMode;
import cn.encmys.ykdz.forest.dailyshop.api.product.Product;
import cn.encmys.ykdz.forest.dailyshop.api.product.stock.ProductStock;
import cn.encmys.ykdz.forest.dailyshop.api.shop.Shop;
import cn.encmys.ykdz.forest.dailyshop.api.shop.cashier.ShopCashier;
import cn.encmys.ykdz.forest.dailyshop.api.shop.cashier.log.SettlementLog;
import cn.encmys.ykdz.forest.dailyshop.api.shop.cashier.log.enums.SettlementLogType;
import cn.encmys.ykdz.forest.dailyshop.api.shop.cashier.record.MerchantRecord;
import cn.encmys.ykdz.forest.dailyshop.api.shop.order.ShopOrder;
import cn.encmys.ykdz.forest.dailyshop.api.shop.order.enums.OrderType;
import cn.encmys.ykdz.forest.dailyshop.api.shop.order.enums.SettlementResult;
import cn.encmys.ykdz.forest.dailyshop.api.utils.BalanceUtils;
import cn.encmys.ykdz.forest.dailyshop.api.utils.LogUtils;
import cn.encmys.ykdz.forest.dailyshop.shop.cashier.log.SettlementLogImpl;
import org.bukkit.Bukkit;
import org.jetbrains.annotations.NotNull;

import java.util.*;

public class ShopCashierImpl implements ShopCashier {
    private final Shop shop;
    private final double initBalance;
    private final boolean supply;
    private final boolean overflow;
    private final boolean inherit;
    private double balance;

    public ShopCashierImpl(@NotNull Shop shop, @NotNull MerchantRecord merchant) {
        this.shop = shop;
        this.initBalance = merchant.initBalance();
        this.balance = initBalance;
        this.supply = merchant.supply();
        this.overflow = merchant.overflow();
        this.inherit = merchant.inherit();
    }

    @Override
    public void billOrder(@NotNull ShopOrder order) {
        if (order.isBilled()) {
            return;
        }

        Map<String, Double> bill = new HashMap<>();
        for (Map.Entry<String, Integer> entry : order.getOrderedProducts().entrySet()) {
            String productId = entry.getKey();
            int stack = entry.getValue();
            double price;

            if (order.getOrderType() == OrderType.SELL_TO) {
                price = shop.getShopPricer().getBuyPrice(productId) * stack;
            } else {
                price = shop.getShopPricer().getSellPrice(productId) * stack;
            }

            bill.put(productId, price);
        }
        order.setBill(bill);
        order.setBilled(true);
    }

    @Override
    public SettlementResult settle(@NotNull ShopOrder order) {
        // Event
        ShopPreSettleEvent shopPreSettleEvent = new ShopPreSettleEvent(shop, order);
        Bukkit.getPluginManager().callEvent(shopPreSettleEvent);
        if (shopPreSettleEvent.isCancelled()) {
            return SettlementResult.CANCELLED;
        }
        // Event

        if (order.isSettled()) {
            LogUtils.warn("Try to settle an order twice.");
            return SettlementResult.DUPLICATED;
        }
        if (order.getOrderType() != OrderType.BUY_ALL_FROM) {
            billOrder(order);
        }

        // Event
        ShopSettleEvent shopSettleEvent = new ShopSettleEvent(shop, order);
        Bukkit.getPluginManager().callEvent(shopSettleEvent);
        // Event

        return switch (order.getOrderType()) {
            case BUY_FROM -> buyFrom(order);
            case BUY_ALL_FROM -> buyAllFrom(order);
            case SELL_TO -> sellTo(order);
        };
    }

    private SettlementResult sellTo(@NotNull ShopOrder order) {
        SettlementResult result = canSellTo(order);
        if (result == SettlementResult.SUCCESS) {
            for (Map.Entry<String, Integer> entry : order.getOrderedProducts().entrySet()) {
                Product product = DailyShop.PRODUCT_FACTORY.getProduct(entry.getKey());
                int stack = entry.getValue();

                if (product == null) continue;

                order.setSettled(true);

                // 处理商人模式
                if (isMerchant()) {
                    modifyBalance(order.getTotalPrice());
                }

                // 处理库存
                ProductStock stock = product.getProductStock();
                if (stock.isGlobalStock()) stock.modifyGlobal(order);
                if (stock.isPlayerStock()) stock.modifyPlayer(order);

                // 处理余额
                BalanceUtils.removeBalance(order.getCustomer(), order.getBilledPrice(product));

                // 给予商品
                product.give(shop, order.getCustomer(), stack);
            }
            logSettlement(order);
        }
        return result;
    }

    private SettlementResult buyFrom(@NotNull ShopOrder order) {
        SettlementResult result = canBuyFrom(order);
        if (result == SettlementResult.SUCCESS) {
            for (Map.Entry<String, Integer> entry : order.getOrderedProducts().entrySet()) {
                Product product = DailyShop.PRODUCT_FACTORY.getProduct(entry.getKey());
                int stack = entry.getValue();

                if (product == null) continue;

                order.setSettled(true);

                // 处理商人模式
                if (isMerchant()) {
                    modifyBalance(-1 * order.getTotalPrice());
                }

                // 处理库存
                ProductStock stock = product.getProductStock();
                if (stock.isGlobalStock()) stock.modifyGlobal(order);
                if (stock.isPlayerStock()) stock.modifyPlayer(order);

                // 处理余额
                BalanceUtils.addBalance(order.getCustomer(), order.getBilledPrice(product));

                // 收取商品
                product.take(shop, order.getCustomer(), stack);
            }
        }
        logSettlement(order);
        return result;
    }

    private SettlementResult buyAllFrom(@NotNull ShopOrder order) {
        // 计算订单中每种商品的玩家拥有量
        // 并储存到订单中
        for (String productId : order.getOrderedProducts().keySet()) {
            Product product = DailyShop.PRODUCT_FACTORY.getProduct(productId);
            if (product == null) continue;
            order.setStack(product, product.has(shop, order.getCustomer(), 1));
        }
        billOrder(order);
        return buyFrom(order);
    }

    @Override
    public SettlementResult canSellTo(@NotNull ShopOrder order) {
        for (Map.Entry<String, Integer> entry : order.getOrderedProducts().entrySet()) {
            Product product = DailyShop.PRODUCT_FACTORY.getProduct(entry.getKey());

            if (product == null) continue;

            // 当前未上架（购物车暂存）
            if (shop.getShopStocker().isListedProduct(product.getId())) {
                return SettlementResult.NOT_LISTED;
            }
            // 商品未开放购买
            else if (product.getBuyPrice().getPriceMode() == PriceMode.DISABLE || order.getBill(product) == -1d) {
                return SettlementResult.TRANSITION_DISABLED;
            }
            // 客户余额不足
            else if (BalanceUtils.checkBalance(order.getCustomer()) < order.getBilledPrice(product)) {
                return SettlementResult.NOT_ENOUGH_MONEY;
            }
            // 商品个人库存不足
            else if (product.getProductStock().isPlayerStock() && product.getProductStock().ifReachPlayerLimit(order.getCustomer().getUniqueId())) {
                return SettlementResult.NOT_ENOUGH_PLAYER_STOCK;
            }
            // 商品总库存不足
            else if (product.getProductStock().isGlobalStock() && product.getProductStock().ifReachGlobalLimit()) {
                return SettlementResult.NOT_ENOUGH_GLOBAL_STOCK;
            }
            // 客户背包空间不足
            else if (!canHold(order)) {
                return SettlementResult.NOT_ENOUGH_INVENTORY_SPACE;
            }
        }
        return SettlementResult.SUCCESS;
    }

    @Override
    public SettlementResult canBuyFrom(@NotNull ShopOrder order) {
        for (Map.Entry<String, Integer> entry : order.getOrderedProducts().entrySet()) {
            Product product = DailyShop.PRODUCT_FACTORY.getProduct(entry.getKey());
            int stack = entry.getValue();

            if (product == null) continue;

            // 当前未上架（购物车暂存）
            if (shop.getShopStocker().isListedProduct(product.getId())) {
                return SettlementResult.NOT_LISTED;
            }
            // 商人模式余额不足
            else if (isMerchant() && balance < order.getTotalPrice()) {
                return SettlementResult.NOT_ENOUGH_MERCHANT_BALANCE;
            }
            // 商品未开放收购
            else if (product.getSellPrice().getPriceMode() == PriceMode.DISABLE || order.getBill(product) == -1d) {
                return SettlementResult.TRANSITION_DISABLED;
            }
            // 客户没有足够的商品
            else if (product.has(shop, order.getCustomer(), stack) == 0) {
                return SettlementResult.NOT_ENOUGH_PRODUCT;
            }
        }
        return SettlementResult.SUCCESS;
    }

    @Override
    public boolean canHold(@NotNull ShopOrder order) {
        for (Map.Entry<String, Integer> entry : order.getOrderedProducts().entrySet()) {
            Product product = DailyShop.PRODUCT_FACTORY.getProduct(entry.getKey());
            int stack = entry.getValue();

            if (product == null) continue;

            if (!product.canHold(shop, order.getCustomer(), stack)) {
                return false;
            }
        }
        return true;
    }

    @Override
    public void logSettlement(@NotNull ShopOrder order) {
        List<String> orderedProductIds = new ArrayList<>();
        List<String> orderedProductNames = new ArrayList<>();
        List<Integer> orderedProductStacks = new ArrayList<>();
        for (Map.Entry<String, Integer> entry : order.getOrderedProducts().entrySet()) {
            Product product = DailyShop.PRODUCT_FACTORY.getProduct(entry.getKey());
            int stack = entry.getValue();
            if (product == null) continue;
            orderedProductIds.add(product.getId());
            orderedProductNames.add(product.getIconDecorator().getName());
            orderedProductStacks.add(stack);
        }

        SettlementLog log;
        UUID customerUUID = order.getCustomer().getUniqueId();

        switch (order.getOrderType()) {
            case BUY_ALL_FROM -> log = SettlementLogImpl.buyAllFromLog(customerUUID);
            case SELL_TO -> log = SettlementLogImpl.sellToLog(customerUUID);
            default -> log = SettlementLogImpl.buyFromLog(customerUUID);
        }

        DailyShop.DATABASE.insertSettlementLog(shop.getId(), log
                .setPrice(order.getTotalPrice())
                .setType(SettlementLogType.getFromOrderType(order.getOrderType()))
                .setOrderedProductIds(orderedProductIds)
                .setOrderedProductNames(orderedProductNames)
                .setOrderedProductStacks(orderedProductStacks)
                .setOrderedProductIds(orderedProductIds));
    }

    @Override
    public void modifyBalance(double value) {
        if (!isMerchant()) {
            return;
        }
        if (!supply && value > 0) {
            return;
        }
        double newValue = balance + value;
        boolean isOverflow = newValue > initBalance;
        if (isOverflow && !overflow) {
            return;
        }
        balance = newValue;
    }

    @Override
    public double getInitBalance() {
        return initBalance;
    }

    @Override
    public boolean isSupply() {
        return supply;
    }

    @Override
    public boolean isOverflow() {
        return overflow;
    }

    @Override
    public boolean isInherit() {
        return inherit;
    }

    @Override
    public double getBalance() {
        return balance;
    }

    @Override
    public void setBalance(double balance) {
        this.balance = balance;
    }

    @Override
    public boolean isMerchant() {
        return initBalance != -1d;
    }

    @Override
    public void restockMerchant() {
        if (isMerchant() && !inherit) {
            balance = initBalance;
        }
    }
}
