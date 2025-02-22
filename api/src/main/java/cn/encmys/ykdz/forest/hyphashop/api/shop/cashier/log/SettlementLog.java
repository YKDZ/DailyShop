package cn.encmys.ykdz.forest.hyphashop.api.shop.cashier.log;

import cn.encmys.ykdz.forest.hyphashop.api.shop.cashier.log.amount.AmountPair;
import cn.encmys.ykdz.forest.hyphashop.api.shop.order.enums.OrderType;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Unmodifiable;

import java.util.Date;
import java.util.Map;
import java.util.UUID;

public interface SettlementLog {
    @NotNull SettlementLog setCustomerUUID(@NotNull UUID customerUUID);

    @NotNull OrderType getType();

    @NotNull SettlementLog setTransitionTime(@NotNull Date transitionTime);

    @NotNull SettlementLog setType(@NotNull OrderType type);

    @NotNull Date getTransitionTime();

    double getTotalPrice();

    @NotNull UUID getCustomerUUID();

    @NotNull SettlementLog setTotalPrice(double price);

    @NotNull
    @Unmodifiable Map<String, AmountPair> getOrderedProducts();

    @NotNull SettlementLog setOrderedProducts(@NotNull Map<String, AmountPair> orderedProducts);

    @NotNull String getSettledShopId();

    @NotNull SettlementLog setSettledShopId(@NotNull String settledShopId);
}
