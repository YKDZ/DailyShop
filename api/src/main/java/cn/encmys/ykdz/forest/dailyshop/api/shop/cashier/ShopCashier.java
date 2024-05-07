package cn.encmys.ykdz.forest.dailyshop.api.shop.cashier;

import cn.encmys.ykdz.forest.dailyshop.api.shop.order.ShopOrder;
import cn.encmys.ykdz.forest.dailyshop.api.shop.order.enums.SettlementResult;
import org.jetbrains.annotations.NotNull;

public interface ShopCashier {
    void billOrder(@NotNull ShopOrder order);

    SettlementResult settle(@NotNull ShopOrder order);

    SettlementResult sellTo(@NotNull ShopOrder order);

    SettlementResult buyFrom(@NotNull ShopOrder order);

    SettlementResult buyAllFrom(@NotNull ShopOrder order);

    SettlementResult canSellTo(@NotNull ShopOrder order);

    SettlementResult canBuyFrom(@NotNull ShopOrder order);

    boolean canHold(@NotNull ShopOrder order);

    int hasStackInTotal(@NotNull ShopOrder order);

    void logSettlement(@NotNull ShopOrder order);
}
