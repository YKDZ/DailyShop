package cn.encmys.ykdz.forest.dailyshop.profile;

import cn.encmys.ykdz.forest.dailyshop.api.profile.Profile;
import cn.encmys.ykdz.forest.dailyshop.api.profile.enums.ShoppingMode;
import cn.encmys.ykdz.forest.dailyshop.api.shop.Shop;
import cn.encmys.ykdz.forest.dailyshop.api.shop.order.ShopOrder;
import cn.encmys.ykdz.forest.dailyshop.api.shop.order.enums.OrderType;
import cn.encmys.ykdz.forest.dailyshop.api.shop.order.enums.SettlementResult;
import cn.encmys.ykdz.forest.dailyshop.shop.order.ShopOrderImpl;
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
    public void setShoppingMode(String shopId, ShoppingMode shoppingMode) {
        shopModes.put(shopId, shoppingMode);
    }

    @Override
    public void setCartOrder(String shopId, ShopOrder shopOrder) {
        carts.put(shopId, shopOrder);
    }

    @Override
    public @NotNull ShopOrder getCart(String shopId) {
        ShopOrder cart = carts.get(shopId);
        if (cart == null) {
            cart = new ShopOrderImpl(getOwner())
                    .setOrderType(OrderType.SELL_TO);
            setCartOrder(shopId, cart);
        }
        return cart;
    }

    @Override
    public SettlementResult settleCart(@NotNull Shop shop) {
        SettlementResult result = shop.getShopCashier().settle(getCart(shop.getId()));
        if (result == SettlementResult.SUCCESS) {
            setCartOrder(shop.getId(),
                    new ShopOrderImpl(getOwner())
                            .setOrderType(OrderType.SELL_TO)
            );
        }
        return result;
    }
}
