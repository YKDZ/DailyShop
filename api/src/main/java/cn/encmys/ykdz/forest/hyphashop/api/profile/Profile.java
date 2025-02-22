package cn.encmys.ykdz.forest.hyphashop.api.profile;

import cn.encmys.ykdz.forest.hyphashop.api.gui.GUI;
import cn.encmys.ykdz.forest.hyphashop.api.profile.cart.Cart;
import cn.encmys.ykdz.forest.hyphashop.api.profile.enums.ShoppingMode;
import cn.encmys.ykdz.forest.hyphashop.api.shop.Shop;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.jetbrains.annotations.Unmodifiable;

import java.util.Map;

public interface Profile {
    @NotNull Player getOwner();

    @NotNull @Unmodifiable
    Map<String, ShoppingMode> getShoppingModes();

    @NotNull ShoppingMode getShoppingMode(@NotNull String shopId);

    void setShoppingMode(@NotNull String shopId, @NotNull ShoppingMode shoppingMode);

    void setShoppingModes(@NotNull Map<String, ShoppingMode> shoppingModes);

    @NotNull Cart getCart();

    @NotNull GUI getCartGUI();

    void pickProductStack(@NotNull Shop shop, @NotNull String productId);

    @Nullable GUI getCurrentStackPickerGUI();

    @NotNull GUI getOrderHistoryGUI();
}
