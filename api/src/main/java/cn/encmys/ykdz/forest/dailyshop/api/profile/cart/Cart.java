package cn.encmys.ykdz.forest.dailyshop.api.profile.cart;

import cn.encmys.ykdz.forest.dailyshop.api.shop.order.ShopOrder;
import cn.encmys.ykdz.forest.dailyshop.api.shop.order.enums.OrderType;
import cn.encmys.ykdz.forest.dailyshop.api.shop.order.enums.SettlementResult;
import org.jetbrains.annotations.NotNull;

import java.util.Map;
import java.util.UUID;

public interface Cart {
    void setOrder(String shopId, ShopOrder shopOrder);

    @NotNull Map<String, ShopOrder> getOrders();

    ShopOrder getOrder(@NotNull String shopId);

    UUID getOwnerUUID();

    Map<String, SettlementResult> settle();

    void clear();

    void clean();

    OrderType getMode();

    void setMode(@NotNull OrderType cartMode);

    double getTotalPrice();
}
