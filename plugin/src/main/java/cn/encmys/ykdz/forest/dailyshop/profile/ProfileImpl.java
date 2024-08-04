package cn.encmys.ykdz.forest.dailyshop.profile;

import cn.encmys.ykdz.forest.dailyshop.api.DailyShop;
import cn.encmys.ykdz.forest.dailyshop.api.gui.PlayerRelatedGUI;
import cn.encmys.ykdz.forest.dailyshop.api.profile.Profile;
import cn.encmys.ykdz.forest.dailyshop.api.profile.enums.ShoppingMode;
import cn.encmys.ykdz.forest.dailyshop.api.shop.Shop;
import cn.encmys.ykdz.forest.dailyshop.api.shop.order.ShopOrder;
import cn.encmys.ykdz.forest.dailyshop.api.shop.order.enums.OrderType;
import cn.encmys.ykdz.forest.dailyshop.api.shop.order.enums.SettlementResult;
import cn.encmys.ykdz.forest.dailyshop.gui.CartGUI;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.HashMap;
import java.util.Map;

public class ProfileImpl implements Profile {
    private final Player owner;
    private final Map<String, ShopOrder> cart = new HashMap<>();
    private final CartGUI cartGUI;
    private OrderType cartMode = OrderType.SELL_TO;
    private final Map<String, ShoppingMode> shopModes = new HashMap<>();

    public ProfileImpl(@NotNull Player owner) {
        this.owner = owner;
        this.cartGUI = new CartGUI(owner);
    }

    @Override
    public Player getOwner() {
        return owner;
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
        cart.put(shopId, shopOrder);
    }

    @Override
    @NotNull
    public Map<String, ShopOrder> getCart() {
        return cart;
    }

    @Override
    public ShopOrder getCartOrder(@NotNull String shopId) {
        return cart.get(shopId);
    }

    @Override
    public SettlementResult settleCart() {
        for (Map.Entry<String, ShopOrder> entry : cart.entrySet()) {
            ShopOrder cartOrder = entry.getValue();
            String shopId = entry.getKey();
            Shop shop = DailyShop.SHOP_FACTORY.getShop(shopId);
            if (shop == null) {
                continue;
            }
            SettlementResult result;
            if (cartMode == OrderType.SELL_TO) {
                result = shop.getShopCashier().canSellTo(cartOrder);
                if (result != SettlementResult.SUCCESS) {
                    return result;
                }
            } else {
                result = shop.getShopCashier().canBuyFrom(cartOrder);
            }
            if (result != SettlementResult.SUCCESS) {
                return result;
            }
        }
        return SettlementResult.SUCCESS;
    }

    @Override
    public OrderType getCartMode() {
        return cartMode;
    }

    @Override
    public void setCartMode(@NotNull OrderType cartMode) {
        this.cartMode = cartMode;
        for (ShopOrder order : cart.values()) {
            order.setOrderType(cartMode);
        }
    }

    @Override
    public PlayerRelatedGUI getCartGUI() {
        return cartGUI;
    }
}
