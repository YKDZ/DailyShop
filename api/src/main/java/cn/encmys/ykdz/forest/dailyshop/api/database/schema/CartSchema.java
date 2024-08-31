package cn.encmys.ykdz.forest.dailyshop.api.database.schema;

import cn.encmys.ykdz.forest.dailyshop.api.profile.cart.Cart;
import cn.encmys.ykdz.forest.dailyshop.api.shop.order.ShopOrder;
import cn.encmys.ykdz.forest.dailyshop.api.shop.order.enums.OrderType;
import org.jetbrains.annotations.NotNull;

import java.util.Map;
import java.util.UUID;

public record CartSchema(@NotNull UUID ownerUUID, @NotNull Map<String, ShopOrder> orders, @NotNull OrderType mode) {
    public static CartSchema of(Cart cart) {
        return new CartSchema(cart.getOwnerUUID(), cart.getOrders(), cart.getMode());
    }
}
