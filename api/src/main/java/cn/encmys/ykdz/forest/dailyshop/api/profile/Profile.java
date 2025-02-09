package cn.encmys.ykdz.forest.dailyshop.api.profile;

import cn.encmys.ykdz.forest.dailyshop.api.gui.GUI;
import cn.encmys.ykdz.forest.dailyshop.api.profile.cart.Cart;
import cn.encmys.ykdz.forest.dailyshop.api.profile.enums.ShoppingMode;
import cn.encmys.ykdz.forest.dailyshop.api.shop.Shop;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.Map;

public interface Profile {
    Player getOwner();

    Map<String, ShoppingMode> getShoppingModes();

    ShoppingMode getShoppingMode(String shopId);

    void setShoppingMode(String shopId, ShoppingMode shoppingMode);

    @NotNull
    Cart getCart();

    GUI getCartGUI();

    void pickProductStack(Shop shop, String productId);

    GUI getCurrentStackPickerGUI();

    GUI getOrderHistoryGUI();
}
