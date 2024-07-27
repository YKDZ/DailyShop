package cn.encmys.ykdz.forest.dailyshop.api.profile;

import cn.encmys.ykdz.forest.dailyshop.api.profile.enums.ShoppingMode;
import cn.encmys.ykdz.forest.dailyshop.api.shop.Shop;
import cn.encmys.ykdz.forest.dailyshop.api.shop.order.ShopOrder;
import cn.encmys.ykdz.forest.dailyshop.api.shop.order.enums.SettlementResult;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.Map;

public interface Profile {
    Player getOwner();

    Map<String, ShopOrder> getCarts();

    Map<String, ShoppingMode> getShoppingModes();

    ShoppingMode getShoppingMode(String shopId);

    void setShopMode(String shopId, ShoppingMode shoppingMode);

    void setCartOrder(String shopId, ShopOrder shopOrder);

    @NotNull
    ShopOrder getCart(String shopId);

    /**
     * Use the shop cashier of given shop to
     * settle the cart and init it if the result is SUCCESS.
     *
     * @param shop Shop to settle the cart in
     * @return SettlementResult
     */
    SettlementResult settleCart(@NotNull Shop shop);
}
