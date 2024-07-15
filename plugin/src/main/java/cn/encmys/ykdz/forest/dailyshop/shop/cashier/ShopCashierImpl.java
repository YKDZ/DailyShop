package cn.encmys.ykdz.forest.dailyshop.shop.cashier;

import cn.encmys.ykdz.forest.dailyshop.api.DailyShop;
import cn.encmys.ykdz.forest.dailyshop.api.event.ProductTradeEvent;
import cn.encmys.ykdz.forest.dailyshop.api.price.enums.PriceMode;
import cn.encmys.ykdz.forest.dailyshop.api.product.Product;
import cn.encmys.ykdz.forest.dailyshop.api.shop.Shop;
import cn.encmys.ykdz.forest.dailyshop.api.shop.cashier.ShopCashier;
import cn.encmys.ykdz.forest.dailyshop.api.shop.cashier.log.SettlementLog;
import cn.encmys.ykdz.forest.dailyshop.api.shop.cashier.log.enums.SettlementLogType;
import cn.encmys.ykdz.forest.dailyshop.api.shop.order.ShopOrder;
import cn.encmys.ykdz.forest.dailyshop.api.shop.order.enums.OrderType;
import cn.encmys.ykdz.forest.dailyshop.api.shop.order.enums.SettlementResult;
import cn.encmys.ykdz.forest.dailyshop.shop.cashier.log.SettlementLogImpl;
import cn.encmys.ykdz.forest.dailyshop.util.BalanceUtils;
import cn.encmys.ykdz.forest.dailyshop.util.LogUtils;
import org.bukkit.Bukkit;
import org.jetbrains.annotations.NotNull;

import java.util.*;
import java.util.stream.IntStream;

public class ShopCashierImpl implements ShopCashier {
    private final Shop shop;

    public ShopCashierImpl(@NotNull Shop shop) {
        this.shop = shop;
    }

    @Override
    public void billOrder(@NotNull ShopOrder order) {
        if (order.isBilled()) {
            return;
        }
        order.setBilled(true);

        Map<Product, Double> bill = new HashMap<>();
        for (Map.Entry<Product, Integer> entry : order.getOrderedProducts().entrySet()) {
            Product product = entry.getKey();
            int stack = entry.getValue();
            double price;

            if (order.getOrderType() == OrderType.SELL_TO) {
                price = shop.getShopPricer().getBuyPrice(product.getId()) * stack;
            } else {
                price = shop.getShopPricer().getSellPrice(product.getId()) * stack;
            }

            bill.put(product, price);
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
            for (Map.Entry<Product, Integer> entry : order.getOrderedProducts().entrySet()) {
                Product product = entry.getKey();
                int stack = entry.getValue();

                BalanceUtils.removeBalance(order.getCustomer(), order.getBilledPrice(product));
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
                for (Map.Entry<Product, Integer> entry : order.getOrderedProducts().entrySet()) {
                    Product product = entry.getKey();
                    int stack = entry.getValue();

                    BalanceUtils.addBalance(order.getCustomer(), order.getBilledPrice(product));
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
        for (Map.Entry<Product, Integer> entry : order.getOrderedProducts().entrySet()) {
            Product product = entry.getKey();

            if (product.getBuyPrice().getPriceMode() == PriceMode.DISABLE || order.getBill(product) == -1d) {
                return SettlementResult.TRANSITION_DISABLED;
            } else if (BalanceUtils.checkBalance(order.getCustomer()) < order.getBilledPrice(product)) {
                return SettlementResult.NOT_ENOUGH_MONEY;
            } else if (!canHold(order)) {
                return SettlementResult.NOT_ENOUGH_MONEY;
            }
        }
        return SettlementResult.SUCCESS;
    }

    @Override
    public SettlementResult canBuyFrom(@NotNull ShopOrder order) {
        for (Map.Entry<Product, Integer> entry : order.getOrderedProducts().entrySet()) {
            Product product = entry.getKey();
            int stack = entry.getValue();

            if (product.getSellPrice().getPriceMode() == PriceMode.DISABLE || order.getBill(product) == -1d) {
                return SettlementResult.TRANSITION_DISABLED;
            } else if (product.has(shop, order.getCustomer(), stack) == 0) {
                return SettlementResult.NOT_ENOUGH_PRODUCT;
            }
        }
        return SettlementResult.SUCCESS;
    }

    @Override
    public boolean canHold(@NotNull cn.encmys.ykdz.forest.dailyshop.api.shop.order.ShopOrder order) {
        for (Map.Entry<Product, Integer> entry : order.getOrderedProducts().entrySet()) {
            Product product = entry.getKey();
            int stack = entry.getValue();

            if (!product.canHold(shop, order.getCustomer(), stack)) {
                return false;
            }
        }
        return true;
    }

    @Override
    public int hasStackInTotal(@NotNull cn.encmys.ykdz.forest.dailyshop.api.shop.order.ShopOrder order) {
        int stackInTotal = Integer.MAX_VALUE;
        for (Map.Entry<Product, Integer> entry : order.getOrderedProducts().entrySet()) {
            Product product = entry.getKey();
            int stack = entry.getValue();

            int hasStack = product.has(shop, order.getCustomer(), 1) / stack;

            if (stackInTotal > hasStack) {
                stackInTotal = hasStack;
            }
        }
        return stackInTotal;
    }

    @Override
    public void logSettlement(@NotNull cn.encmys.ykdz.forest.dailyshop.api.shop.order.ShopOrder order) {
        List<String> orderedProductIds = new ArrayList<>();
        List<String> orderedProductNames = new ArrayList<>();
        List<Integer> orderedProductStacks = new ArrayList<>();
        for (Map.Entry<Product, Integer> entry : order.getOrderedProducts().entrySet()) {
            Product product = entry.getKey();
            int stack = entry.getValue();
            orderedProductIds.add(product.getId());
            orderedProductNames.add(product.getIconBuilder().getName());
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
