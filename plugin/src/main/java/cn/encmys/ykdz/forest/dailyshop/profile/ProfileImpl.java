package cn.encmys.ykdz.forest.dailyshop.profile;

import cn.encmys.ykdz.forest.dailyshop.api.config.CartGUIConfig;
import cn.encmys.ykdz.forest.dailyshop.api.config.OrderHistoryGUIConfig;
import cn.encmys.ykdz.forest.dailyshop.api.config.StackPickerGUIConfig;
import cn.encmys.ykdz.forest.dailyshop.api.gui.GUI;
import cn.encmys.ykdz.forest.dailyshop.api.profile.Profile;
import cn.encmys.ykdz.forest.dailyshop.api.profile.cart.Cart;
import cn.encmys.ykdz.forest.dailyshop.api.profile.enums.GUIType;
import cn.encmys.ykdz.forest.dailyshop.api.profile.enums.ShoppingMode;
import cn.encmys.ykdz.forest.dailyshop.api.shop.Shop;
import cn.encmys.ykdz.forest.dailyshop.gui.CartGUI;
import cn.encmys.ykdz.forest.dailyshop.gui.OrderHistoryGUI;
import cn.encmys.ykdz.forest.dailyshop.gui.StackPickerGUI;
import cn.encmys.ykdz.forest.dailyshop.profile.cart.CartImpl;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.HashMap;
import java.util.Map;

public class ProfileImpl implements Profile {
    @NotNull
    private final Player owner;
    @NotNull
    private final Cart cart;
    @NotNull
    private final CartGUI cartGUI;
    @NotNull
    private final Map<String, ShoppingMode> shoppingModes = new HashMap<>();
    @NotNull
    private final OrderHistoryGUI orderHistoryGUI;
    @Nullable
    private StackPickerGUI currentStackPickerGUI;

    public ProfileImpl(@NotNull Player owner) {
        this.owner = owner;
        this.cartGUI = new CartGUI(CartGUIConfig.getGUIRecord());
        this.orderHistoryGUI = new OrderHistoryGUI(OrderHistoryGUIConfig.getGUIRecord());
        this.cart = new CartImpl(owner.getUniqueId());
    }

    @Override
    public @NotNull Player getOwner() {
        return owner;
    }

    @Override
    public @NotNull Map<String, ShoppingMode> getShoppingModes() {
        return shoppingModes;
    }

    @Override
    public @NotNull ShoppingMode getShoppingMode(@NotNull String shopId) {
        return shoppingModes.getOrDefault(shopId, ShoppingMode.DIRECT);
    }

    @Override
    public void setShoppingMode(@NotNull String shopId, @NotNull ShoppingMode shoppingMode) {
        shoppingModes.put(shopId, shoppingMode);
    }

    @Override
    public @NotNull Cart getCart() {
        return cart;
    }

    @Override
    public @NotNull GUI getCartGUI() {
        return cartGUI;
    }

    @Override
    public void pickProductStack(@NotNull Shop shop, @NotNull String productId) {
        currentStackPickerGUI = new StackPickerGUI(cart.getOrders().get(shop.getId()), productId, StackPickerGUIConfig.getGUIRecord());
        currentStackPickerGUI.open(owner);
    }

    @Override
    public @Nullable GUI getCurrentStackPickerGUI() {
        return currentStackPickerGUI;
    }

    @Override
    public @NotNull GUI getOrderHistoryGUI() {
        return orderHistoryGUI;
    }
}
