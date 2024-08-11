package cn.encmys.ykdz.forest.dailyshop.api.database.schema;

import cn.encmys.ykdz.forest.dailyshop.api.profile.cart.Cart;
import cn.encmys.ykdz.forest.dailyshop.api.profile.enums.ShoppingMode;
import org.jetbrains.annotations.NotNull;

import java.util.Map;
import java.util.UUID;

public record ProfileData(@NotNull UUID ownerUUID, @NotNull Cart cart, @NotNull Map<String, ShoppingMode> shoppingModes) {
}
