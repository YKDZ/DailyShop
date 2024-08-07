package cn.encmys.ykdz.forest.dailyshop.api.profile;

import cn.encmys.ykdz.forest.dailyshop.api.gui.PlayerRelatedGUI;
import cn.encmys.ykdz.forest.dailyshop.api.profile.enums.ShoppingMode;
import cn.encmys.ykdz.forest.dailyshop.api.shop.Shop;
import cn.encmys.ykdz.forest.dailyshop.api.shop.order.ShopOrder;
import cn.encmys.ykdz.forest.dailyshop.api.shop.order.enums.OrderType;
import cn.encmys.ykdz.forest.dailyshop.api.shop.order.enums.SettlementResult;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.Map;

public interface Profile {
    Player getOwner();

    Map<String, ShoppingMode> getShoppingModes();

    ShoppingMode getShoppingMode(String shopId);

    void setShoppingMode(String shopId, ShoppingMode shoppingMode);

    void setCartOrder(String shopId, ShopOrder shopOrder);

    @NotNull
    Map<String, ShopOrder> getCart();

    ShopOrder getCartOrder(@NotNull String shopId);

    /**
     * Use the shop cashier of given shop to
     * settle the cart and init it if the result is SUCCESS.
     *
     * @return SettlementResult
     */
    SettlementResult settleCart();

    void cleanCart();

    void clearCart();

    OrderType getCartMode();

    void setCartMode(OrderType cartMode);

    PlayerRelatedGUI getCartGUI();

    double getCartTotalPrice();

    void pickProductStack(Shop shop, String productId);

    PlayerRelatedGUI getCurrentStackPickerGUI();

    PlayerRelatedGUI getOrderHistoryGUI();
}
