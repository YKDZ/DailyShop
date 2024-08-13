package cn.encmys.ykdz.forest.dailyshop.api.database.schema;

import cn.encmys.ykdz.forest.dailyshop.api.profile.enums.ShoppingMode;
import cn.encmys.ykdz.forest.dailyshop.api.shop.order.ShopOrder;
import cn.encmys.ykdz.forest.dailyshop.api.shop.order.enums.OrderType;
import org.jetbrains.annotations.NotNull;

import java.util.Map;
import java.util.UUID;

public record ProfileData(@NotNull UUID ownerUUID, @NotNull Map<String, ShopOrder> cartOrders,
                          @NotNull OrderType cartMode, @NotNull Map<String, ShoppingMode> shoppingModes) {
}
