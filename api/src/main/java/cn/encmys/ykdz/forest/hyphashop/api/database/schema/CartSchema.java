package cn.encmys.ykdz.forest.hyphashop.api.database.schema;

import cn.encmys.ykdz.forest.hyphashop.api.profile.cart.Cart;
import cn.encmys.ykdz.forest.hyphashop.api.shop.order.ShopOrder;
import cn.encmys.ykdz.forest.hyphashop.api.shop.order.enums.OrderType;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

import java.util.Map;
import java.util.UUID;

public record CartSchema(@NotNull UUID ownerUUID, @NotNull Map<String, ShopOrder> orders, @NotNull OrderType mode) {
    @Contract("_ -> new")
    public static @NotNull CartSchema of(@NotNull Cart cart) {
        return new CartSchema(cart.getOwnerUUID(), cart.getOrders(), cart.getMode());
    }
}
