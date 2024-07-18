package cn.encmys.ykdz.forest.dailyshop.shop.cashier;

import cn.encmys.ykdz.forest.dailyshop.api.DailyShop;
import cn.encmys.ykdz.forest.dailyshop.api.event.ProductTradeEvent;
import cn.encmys.ykdz.forest.dailyshop.api.price.enums.PriceMode;
import cn.encmys.ykdz.forest.dailyshop.api.product.Product;
import cn.encmys.ykdz.forest.dailyshop.api.product.stock.ProductStock;
import cn.encmys.ykdz.forest.dailyshop.api.shop.Shop;
import cn.encmys.ykdz.forest.dailyshop.api.shop.cashier.ShopCashier;
import cn.encmys.ykdz.forest.dailyshop.api.shop.cashier.log.SettlementLog;
import cn.encmys.ykdz.forest.dailyshop.api.shop.cashier.log.enums.SettlementLogType;
import cn.encmys.ykdz.forest.dailyshop.api.shop.order.ShopOrder;
import cn.encmys.ykdz.forest.dailyshop.api.shop.order.enums.OrderType;
import cn.encmys.ykdz.forest.dailyshop.api.shop.order.enums.SettlementResult;
import cn.encmys.ykdz.forest.dailyshop.api.utils.BalanceUtils;
import cn.encmys.ykdz.forest.dailyshop.api.utils.LogUtils;
import cn.encmys.ykdz.forest.dailyshop.shop.cashier.log.SettlementLogImpl;
import org.bukkit.Bukkit;
import org.jetbrains.annotations.NotNull;

import java.util.*;
import java.util.stream.IntStream;

public class ShopCashierImpl implements ShopCashier {
    private final Shop shop;
    // TODO 商人功能
    private double balance = -1d;
    private final boolean supply = false;
    private final boolean overflow = false;
    private boolean inherit = false;

    public ShopCashierImpl(@NotNull Shop shop) {
        this.shop = shop;
    }

    @Override
    public void billOrder(@NotNull ShopOrder order) {
        if (order.isBilled()) {
            return;
        }
        order.setBilled(true);

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
    }

    @Override
    public SettlementResult settle(@NotNull ShopOrder order) {
        // Event
        ProductTradeEvent event = new ProductTradeEvent(order.getCustomer(), shop, order);
        Bukkit.getPluginManager().callEvent(event);
        if (event.isCancelled()) {
            return SettlementResult.CANCELLED;
        }
        // Event

        if (order.isSettled()) {
            LogUtils.warn("Try to settle an order twice.");
            return SettlementResult.DUPLICATED;
        }
        order.setSettled(true);
        billOrder(order);
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
            IntStream.range(0, order.getTotalStack()).forEach((i) -> {
                for (Map.Entry<String, Integer> entry : order.getOrderedProducts().entrySet()) {
                    Product product = DailyShop.PRODUCT_FACTORY.getProduct(entry.getKey());
                    int stack = entry.getValue();

                    // 处理库存
                    ProductStock stock = product.getProductStock();
                    if (stock.isGlobalStock()) stock.modifyGlobal(order);
                    if (stock.isPlayerStock()) stock.modifyPlayer(order);

                    // 处理余额
                    BalanceUtils.addBalance(order.getCustomer(), order.getBilledPrice(product));

                    // 收取商品
                    product.take(shop, order.getCustomer(), stack);
                }
            });
            logSettlement(order);
        }
        return result;
    }

    private SettlementResult buyAllFrom(@NotNull ShopOrder order) {
        order.setTotalStack(hasStackInTotal(order));
        return buyFrom(order);
    }

    @Override
    public SettlementResult canSellTo(@NotNull ShopOrder order) {
        for (Map.Entry<String, Integer> entry : order.getOrderedProducts().entrySet()) {
            Product product = DailyShop.PRODUCT_FACTORY.getProduct(entry.getKey());

            // 商品未开放购买
            if (product.getBuyPrice().getPriceMode() == PriceMode.DISABLE || order.getBill(product) == -1d) {
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

            // 商品未开放收购
            if (product.getSellPrice().getPriceMode() == PriceMode.DISABLE || order.getBill(product) == -1d) {
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

            if (!product.canHold(shop, order.getCustomer(), stack)) {
                return false;
            }
        }
        return true;
    }

    @Override
    public int hasStackInTotal(@NotNull ShopOrder order) {
        int stackInTotal = Integer.MAX_VALUE;
        for (Map.Entry<String, Integer> entry : order.getOrderedProducts().entrySet()) {
            Product product = DailyShop.PRODUCT_FACTORY.getProduct(entry.getKey());
            int stack = entry.getValue();

            int hasStack = product.has(shop, order.getCustomer(), 1) / stack;

            if (stackInTotal > hasStack) {
                stackInTotal = hasStack;
            }
        }
        return stackInTotal;
    }

    @Override
    public void logSettlement(@NotNull ShopOrder order) {
        List<String> orderedProductIds = new ArrayList<>();
        List<String> orderedProductNames = new ArrayList<>();
        List<Integer> orderedProductStacks = new ArrayList<>();
        for (Map.Entry<String, Integer> entry : order.getOrderedProducts().entrySet()) {
            Product product = DailyShop.PRODUCT_FACTORY.getProduct(entry.getKey());
            int stack = entry.getValue();
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
                .setOrderedProductIds(orderedProductIds)
                .setTotalStack(order.getTotalStack()));
    }
}
