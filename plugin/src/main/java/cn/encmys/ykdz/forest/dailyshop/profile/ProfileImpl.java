package cn.encmys.ykdz.forest.dailyshop.profile;

import cn.encmys.ykdz.forest.dailyshop.api.profile.Profile;
import cn.encmys.ykdz.forest.dailyshop.api.profile.enums.ShoppingMode;
import cn.encmys.ykdz.forest.dailyshop.api.shop.order.ShopOrder;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.HashMap;
import java.util.Map;

public class ProfileImpl implements Profile {
    private final Player owner;
    private final Map<String, ShopOrder> carts = new HashMap<>();
    private final Map<String, ShoppingMode> shopModes = new HashMap<>();

    public ProfileImpl(@NotNull Player owner) {
        this.owner = owner;
    }

    @Override
    public Player getOwner() {
        return owner;
    }

    @Override
    public Map<String, ShopOrder> getCarts() {
        return carts;
    }

    @Override
    public Map<String, ShoppingMode> getShoppingModes() {
        return shopModes;
    }

    @Override
    public ShoppingMode getShoppingMode(String shopId) {
        return shopModes.getOrDefault(shopId, ShoppingMode.DIRECT);
    }

    @Override
    public void setShopMode(String shopId, ShoppingMode shoppingMode) {
        shopModes.put(shopId, shoppingMode);
    }

    @Override
    public void setCartOrder(String shopId, ShopOrder shopOrder) {
        carts.put(shopId, shopOrder);
    }

    @Override
    public ShopOrder getCart(String shopId) {
        return carts.get(shopId);
    }
}
