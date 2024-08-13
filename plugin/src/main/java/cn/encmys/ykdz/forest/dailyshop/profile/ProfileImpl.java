package cn.encmys.ykdz.forest.dailyshop.profile;

import cn.encmys.ykdz.forest.dailyshop.api.DailyShop;
import cn.encmys.ykdz.forest.dailyshop.api.config.CartGUIConfig;
import cn.encmys.ykdz.forest.dailyshop.api.config.OrderHistoryGUIConfig;
import cn.encmys.ykdz.forest.dailyshop.api.config.StackPickerGUIConfig;
import cn.encmys.ykdz.forest.dailyshop.api.database.schema.ProfileData;
import cn.encmys.ykdz.forest.dailyshop.api.gui.PlayerRelatedGUI;
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

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ExecutionException;

public class ProfileImpl implements Profile {
    private final Player owner;
    private final Cart cart;
    private final CartGUI cartGUI;
    private final Map<String, ShoppingMode> shoppingModes = new HashMap<>();
    private final OrderHistoryGUI orderHistoryGUI;
    private StackPickerGUI currentStackPickerGUI;
    private GUIType viewingGuiType;

    public ProfileImpl(Player owner) {
        this.owner = owner;
        this.cartGUI = new CartGUI(owner, CartGUIConfig.getGUIRecord());
        this.orderHistoryGUI = new OrderHistoryGUI(owner, OrderHistoryGUIConfig.getGUIRecord());
        this.cart = new CartImpl(owner.getUniqueId());
        try {
            ProfileData data = DailyShop.DATABASE.queryProfileData(owner.getUniqueId()).get();
            if (data != null) {
                cart.setMode(data.cartMode());
                cart.setOrders(data.cartOrders());
                shoppingModes.putAll(data.shoppingModes());
            }
        } catch (ExecutionException | InterruptedException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public Player getOwner() {
        return owner;
    }

    @Override
    public Map<String, ShoppingMode> getShoppingModes() {
        return shoppingModes;
    }

    @Override
    public ShoppingMode getShoppingMode(String shopId) {
        return shoppingModes.getOrDefault(shopId, ShoppingMode.DIRECT);
    }

    @Override
    public void setShoppingMode(String shopId, ShoppingMode shoppingMode) {
        shoppingModes.put(shopId, shoppingMode);
    }

    @Override
    @NotNull
    public Cart getCart() {
        return cart;
    }

    @Override
    public PlayerRelatedGUI getCartGUI() {
        return cartGUI;
    }

    @Override
    public void pickProductStack(Shop shop, String productId) {
        currentStackPickerGUI = new StackPickerGUI(owner, cart.getOrders().get(shop.getId()), productId, StackPickerGUIConfig.getGUIRecord());
        currentStackPickerGUI.open();
    }

    @Override
    public PlayerRelatedGUI getCurrentStackPickerGUI() {
        return currentStackPickerGUI;
    }

    @Override
    public PlayerRelatedGUI getOrderHistoryGUI() {
        return orderHistoryGUI;
    }

    @Override
    public GUIType getViewingGuiType() {
        return viewingGuiType;
    }

    @Override
    public void setViewingGuiType(GUIType viewingGuiType) {
        this.viewingGuiType = viewingGuiType;
    }
}
