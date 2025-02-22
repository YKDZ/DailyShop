package cn.encmys.ykdz.forest.hyphashop.api.profile.cart;

import cn.encmys.ykdz.forest.hyphashop.api.shop.order.ShopOrder;
import cn.encmys.ykdz.forest.hyphashop.api.shop.order.enums.OrderType;
import cn.encmys.ykdz.forest.hyphashop.api.shop.order.enums.SettlementResult;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Unmodifiable;

import java.util.Map;
import java.util.UUID;

public interface Cart {
    void setOrder(@NotNull String shopId, @NotNull ShopOrder shopOrder);

    void setOrders(@NotNull Map<String, ShopOrder> orders);

    @NotNull @Unmodifiable Map<String, ShopOrder> getOrders();

    @NotNull ShopOrder getOrder(@NotNull String shopId);

    @NotNull UUID getOwnerUUID();

    @NotNull @Unmodifiable Map<String, SettlementResult> settle();

    void clear();

    void clean();

    @NotNull OrderType getMode();

    void setMode(@NotNull OrderType cartMode);

    double getTotalPrice();
}
