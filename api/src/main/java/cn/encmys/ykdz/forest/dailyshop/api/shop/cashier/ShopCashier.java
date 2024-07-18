package cn.encmys.ykdz.forest.dailyshop.api.shop.cashier;

import cn.encmys.ykdz.forest.dailyshop.api.shop.order.ShopOrder;
import cn.encmys.ykdz.forest.dailyshop.api.shop.order.enums.SettlementResult;
import org.jetbrains.annotations.NotNull;

/**
 * Check and Checkout the ShopOrder.
 */
public interface ShopCashier {
    /**
     * Calculate the prices of all products in given ShopOrder and store them.
     * Each ShopOrder can only be billed once.
     * @param order ShopOrder that will be calculated
     */
    void billOrder(@NotNull ShopOrder order);

    /**
     * Checkout the given ShopOrder for the customer.
     * Each ShopOrder can only be settled once.
     * @param order ShopOrder that will be settled
     * @return SettlementResult of this settle action
     */
    SettlementResult settle(@NotNull ShopOrder order);

    SettlementResult canSellTo(@NotNull ShopOrder order);

    SettlementResult canBuyFrom(@NotNull ShopOrder order);

    boolean canHold(@NotNull ShopOrder order);

    int hasStackInTotal(@NotNull ShopOrder order);

    void logSettlement(@NotNull ShopOrder order);
}
