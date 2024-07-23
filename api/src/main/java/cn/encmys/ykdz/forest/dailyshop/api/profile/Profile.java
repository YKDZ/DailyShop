package cn.encmys.ykdz.forest.dailyshop.api.profile;

import cn.encmys.ykdz.forest.dailyshop.api.profile.enums.ShoppingMode;
import cn.encmys.ykdz.forest.dailyshop.api.shop.order.ShopOrder;
import org.bukkit.entity.Player;

import java.util.Map;

public interface Profile {
    Player getOwner();

    Map<String, ShopOrder> getCarts();

    Map<String, ShoppingMode> getShoppingModes();

    ShoppingMode getShoppingMode(String shopId);

    void setShopMode(String shopId, ShoppingMode shoppingMode);

    void setCartOrder(String shopId, ShopOrder shopOrder);

    ShopOrder getCart(String shopId);
}
