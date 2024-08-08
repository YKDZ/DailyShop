package cn.encmys.ykdz.forest.dailyshop.profile;

import cn.encmys.ykdz.forest.dailyshop.api.DailyShop;
import cn.encmys.ykdz.forest.dailyshop.api.database.schema.ProfileData;
import cn.encmys.ykdz.forest.dailyshop.api.gui.PlayerRelatedGUI;
import cn.encmys.ykdz.forest.dailyshop.api.profile.Profile;
import cn.encmys.ykdz.forest.dailyshop.api.profile.enums.ShoppingMode;
import cn.encmys.ykdz.forest.dailyshop.api.shop.Shop;
import cn.encmys.ykdz.forest.dailyshop.api.shop.order.ShopOrder;
import cn.encmys.ykdz.forest.dailyshop.api.shop.order.enums.OrderType;
import cn.encmys.ykdz.forest.dailyshop.api.shop.order.enums.SettlementResult;
import cn.encmys.ykdz.forest.dailyshop.gui.CartGUI;
import cn.encmys.ykdz.forest.dailyshop.gui.OrderHistoryGUI;
import cn.encmys.ykdz.forest.dailyshop.gui.StackPickerGUI;
import cn.encmys.ykdz.forest.dailyshop.shop.order.ShopOrderImpl;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.concurrent.ExecutionException;

public class ProfileImpl implements Profile {
    private final Player owner;
    private final Map<String, ShopOrder> cart = new HashMap<>();
    private final CartGUI cartGUI;
    private OrderType cartMode = OrderType.SELL_TO;
    private final Map<String, ShoppingMode> shoppingModes = new HashMap<>();
    private StackPickerGUI currentStackPickerGUI;
    private OrderHistoryGUI orderHistoryGUI;

    public ProfileImpl(Player owner) {
        this.owner = owner;
        this.cartGUI = new CartGUI(owner);
        this.orderHistoryGUI = new OrderHistoryGUI(owner);
        try {
            ProfileData data = DailyShop.DATABASE.queryProfileData(owner.getUniqueId()).get();
            if (data != null) {
                cart.putAll(data.cart());
                cartMode = data.cartMode();
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
    public void setCartOrder(String shopId, ShopOrder shopOrder) {
        cart.put(shopId, shopOrder);
    }

    @Override
    @NotNull
    public Map<String, ShopOrder> getCart() {
        return Collections.unmodifiableMap(cart);
    }

    @Override
    public ShopOrder getCartOrder(@NotNull String shopId) {
        ShopOrder cartOrder = cart.get(shopId);
        if (cartOrder == null) {
            cartOrder = new ShopOrderImpl(owner)
                    .setOrderType(OrderType.SELL_TO);
            cart.put(shopId, cartOrder);
        }
        return cartOrder;
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
        for (Map.Entry<String, ShopOrder> entry : cart.entrySet()) {
            ShopOrder cartOrder = entry.getValue();
            String shopId = entry.getKey();
            Shop shop = DailyShop.SHOP_FACTORY.getShop(shopId);
            if (shop == null) {
                continue;
            }
            shop.getShopCashier().settle(cartOrder);
        }
        clearCart();
        return SettlementResult.SUCCESS;
    }

    @Override
    public void clearCart() {
        cart.clear();
    }

    @Override
    public void cleanCart() {
        Iterator<Map.Entry<String, ShopOrder>> iterator = cart.entrySet().iterator();
        while (iterator.hasNext()) {
            Map.Entry<String, ShopOrder> entry = iterator.next();
            ShopOrder cartOrder = entry.getValue();
            String shopId = entry.getKey();
            Shop shop = DailyShop.SHOP_FACTORY.getShop(shopId);

            // 商店不存在
            if (shop == null) {
                iterator.remove();
                continue;
            }

            cartOrder.clean(shop);
        }
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

    @Override
    public double getCartTotalPrice() {
        return cart.values().stream().mapToDouble(ShopOrder::getTotalPrice).sum();
    }

    @Override
    public void pickProductStack(Shop shop, String productId) {
        currentStackPickerGUI = new StackPickerGUI(owner, cart.get(shop.getId()), productId);
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
}
