package cn.encmys.ykdz.forest.dailyshop.shop.cashier;

import cn.encmys.ykdz.forest.dailyshop.api.product.Product;
import cn.encmys.ykdz.forest.dailyshop.price.enums.PriceMode;
import cn.encmys.ykdz.forest.dailyshop.shop.Shop;
import cn.encmys.ykdz.forest.dailyshop.shop.cashier.log.SettlementLog;
import cn.encmys.ykdz.forest.dailyshop.shop.cashier.log.enums.SettlementLogType;
import cn.encmys.ykdz.forest.dailyshop.shop.order.ShopOrder;
import cn.encmys.ykdz.forest.dailyshop.shop.order.enums.OrderType;
import cn.encmys.ykdz.forest.dailyshop.shop.order.enums.SettlementResult;
import cn.encmys.ykdz.forest.dailyshop.util.BalanceUtils;
import cn.encmys.ykdz.forest.dailyshop.util.LogUtils;
import com.google.gson.annotations.Expose;
import org.jetbrains.annotations.NotNull;

import java.util.*;
import java.util.stream.IntStream;

public class ShopCashier {
    private final Shop shop;
    @Expose
    private final List<SettlementLog> settlementLogs = new ArrayList<>();

    public ShopCashier(@NotNull Shop shop) {
        this.shop = shop;
    }

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

    public SettlementResult settle(@NotNull ShopOrder order) {
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

    public SettlementResult sellTo(@NotNull ShopOrder order) {
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

    public SettlementResult buyFrom(@NotNull ShopOrder order) {
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

    public SettlementResult buyAllFrom(@NotNull ShopOrder order) {
        order.setTotalStack(hasStackInTotal(order));
        return buyFrom(order);
    }

    public SettlementResult canSellTo(@NotNull ShopOrder order) {
        for (Map.Entry<Product, Integer> entry : order.getOrderedProducts().entrySet()) {
            Product product = entry.getKey();

            if (product.getBuyPrice().getPriceMode() == PriceMode.DISABLE) {
                return SettlementResult.TRANSITION_DISABLED;
            } else if (BalanceUtils.checkBalance(order.getCustomer()) < order.getBilledPrice(product)) {
                return SettlementResult.NOT_ENOUGH_MONEY;
            } else if (product.getBuyPrice().getPriceMode() == PriceMode.DISABLE) {
                return SettlementResult.TRANSITION_DISABLED;
            } else if (!canHold(order)) {
                return SettlementResult.NOT_ENOUGH_MONEY;
            }
        }
        return SettlementResult.SUCCESS;
    }

    public SettlementResult canBuyFrom(@NotNull ShopOrder order) {
        for (Map.Entry<Product, Integer> entry : order.getOrderedProducts().entrySet()) {
            Product product = entry.getKey();
            int stack = entry.getValue();

            if (product.getSellPrice().getPriceMode() == PriceMode.DISABLE) {
                return SettlementResult.TRANSITION_DISABLED;
            } else if (product.has(shop, order.getCustomer(), stack) == 0) {
                return SettlementResult.NOT_ENOUGH_PRODUCT;
            } else if (product.getBuyPrice().getPriceMode() == PriceMode.DISABLE) {
                return SettlementResult.TRANSITION_DISABLED;
            }
        }
        return SettlementResult.SUCCESS;
    }

    public boolean canHold(@NotNull ShopOrder order) {
        for (Map.Entry<Product, Integer> entry : order.getOrderedProducts().entrySet()) {
            Product product = entry.getKey();
            int stack = entry.getValue();

            if (!product.canHold(shop, order.getCustomer(), stack)) {
                return false;
            }
        }
        return true;
    }

    public int hasStackInTotal(@NotNull ShopOrder order) {
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

    public void logSettlement(@NotNull ShopOrder order) {
        Map<String, Integer> orderedProductsNameAndStack = new HashMap<>();
        List<String> orderedProductIds = new ArrayList<>();
        for (Map.Entry<Product, Integer> entry : order.getOrderedProducts().entrySet()) {
            Product product = entry.getKey();
            int stack = entry.getValue();

            orderedProductsNameAndStack.put(product.getIconBuilder().getName(), stack);
            orderedProductIds.add(product.getId());
        }

        SettlementLog log;
        UUID customerUUID = order.getCustomer().getUniqueId();

        switch (order.getOrderType()) {
            case BUY_FROM -> log = SettlementLog.buyFromLog(customerUUID);
            case BUY_ALL_FROM -> log = SettlementLog.buyAllFromLog(customerUUID);
            case SELL_TO -> log = SettlementLog.sellToLog(customerUUID);
            default -> log = SettlementLog.buyFromLog(customerUUID);
        }

        settlementLogs.add(log
                .setPrice(order.getTotalPrice())
                .setType(SettlementLogType.getFromOrderType(order.getOrderType()))
                .setOrderedProductsNameAndStack(orderedProductsNameAndStack)
                .setOrderedProductIds(orderedProductIds)
                .setTotalStack(order.getTotalStack()));
    }
}
