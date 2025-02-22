package cn.encmys.ykdz.forest.hyphashop.profile;

import cn.encmys.ykdz.forest.hyphashop.api.gui.GUI;
import cn.encmys.ykdz.forest.hyphashop.api.profile.Profile;
import cn.encmys.ykdz.forest.hyphashop.api.profile.cart.Cart;
import cn.encmys.ykdz.forest.hyphashop.api.profile.enums.ShoppingMode;
import cn.encmys.ykdz.forest.hyphashop.api.shop.Shop;
import cn.encmys.ykdz.forest.hyphashop.config.CartGUIConfig;
import cn.encmys.ykdz.forest.hyphashop.config.OrderHistoryGUIConfig;
import cn.encmys.ykdz.forest.hyphashop.config.StackPickerGUIConfig;
import cn.encmys.ykdz.forest.hyphashop.gui.CartGUI;
import cn.encmys.ykdz.forest.hyphashop.gui.OrderHistoryGUI;
import cn.encmys.ykdz.forest.hyphashop.gui.StackPickerGUI;
import cn.encmys.ykdz.forest.hyphashop.profile.cart.CartImpl;
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
    private Map<String, ShoppingMode> shoppingModes = new HashMap<>();
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
    public void setShoppingModes(@NotNull Map<String, ShoppingMode> shoppingModes) {
        this.shoppingModes = shoppingModes;
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
