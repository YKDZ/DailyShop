package cn.encmys.ykdz.forest.hyphashop.profile.cart;

import cn.encmys.ykdz.forest.hyphashop.api.HyphaShop;
import cn.encmys.ykdz.forest.hyphashop.api.profile.cart.Cart;
import cn.encmys.ykdz.forest.hyphashop.api.shop.Shop;
import cn.encmys.ykdz.forest.hyphashop.api.shop.order.ShopOrder;
import cn.encmys.ykdz.forest.hyphashop.api.shop.order.enums.OrderType;
import cn.encmys.ykdz.forest.hyphashop.api.shop.order.enums.SettlementResult;
import cn.encmys.ykdz.forest.hyphashop.shop.order.ShopOrderImpl;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Unmodifiable;

import java.util.*;

public class CartImpl implements Cart {
    private final @NotNull UUID ownerUUID;
    private @NotNull Map<String, ShopOrder> orders = new HashMap<>();
    private @NotNull OrderType mode = OrderType.SELL_TO;

    public CartImpl(@NotNull UUID ownerUUID) {
        this.ownerUUID = ownerUUID;
    }

    @Override
    public void setOrder(@NotNull String shopId, @NotNull ShopOrder shopOrder) {
        orders.put(shopId, shopOrder);
    }

    @Override
    public void setOrders(@NotNull Map<String, ShopOrder> orders) {
        this.orders = orders;
    }

    @Override
    public @NotNull @Unmodifiable Map<String, ShopOrder> getOrders() {
        return Collections.unmodifiableMap(orders);
    }

    @Override
    public @NotNull ShopOrder getOrder(@NotNull String shopId) {
        ShopOrder cartOrder = orders.get(shopId);
        if (cartOrder == null) {
            cartOrder = new ShopOrderImpl(ownerUUID)
                    .setOrderType(OrderType.SELL_TO);
            orders.put(shopId, cartOrder);
        }
        return cartOrder;
    }

    @Override
    public @NotNull UUID getOwnerUUID() {
        return ownerUUID;
    }

    @Override
    public @NotNull Map<String, SettlementResult> settle() {
        Map<String, SettlementResult> result = new HashMap<>() {{
            for (String shopId : orders.keySet()) {
                put(shopId, SettlementResult.UNKNOWN);
            }
        }};
        // 目前的设计难以同时处理两个来自不同商店的 ShopOrder
        // 所以选择将购物车中的商品按商店分开结算（允许仅部分交易成功）
        orders.entrySet().removeIf(entry -> {
            String shopId = entry.getKey();
            Shop shop = HyphaShop.SHOP_FACTORY.getShop(shopId);
            if (shop == null) {
                return false;
            }
            ShopOrder cartOrder = entry.getValue();
            SettlementResult orderResult = mode == OrderType.SELL_TO ?
                    shop.getShopCashier().canSellTo(cartOrder) :
                    shop.getShopCashier().canBuyFrom(cartOrder);
            result.put(shopId, orderResult);
            // 若成功交易则删除购物车中的此 ShopOrder
            if (orderResult == SettlementResult.SUCCESS) {
                shop.getShopCashier().settle(cartOrder);
                return true;
            } else {
                return false;
            }
        });
        return result;
    }

    @Override
    public void clear() {
        orders.clear();
    }

    @Override
    public void clean() {
        Iterator<Map.Entry<String, ShopOrder>> iterator = orders.entrySet().iterator();
        while (iterator.hasNext()) {
            Map.Entry<String, ShopOrder> entry = iterator.next();
            ShopOrder cartOrder = entry.getValue();
            String shopId = entry.getKey();
            Shop shop = HyphaShop.SHOP_FACTORY.getShop(shopId);

            // 商店不存在
            if (shop == null) {
                iterator.remove();
                continue;
            }

            cartOrder.clean(shop);
        }
    }

    @Override
    public @NotNull OrderType getMode() {
        return mode;
    }

    @Override
    public void setMode(@NotNull OrderType cartMode) {
        this.mode = cartMode;
        for (ShopOrder order : orders.values()) {
            order.setOrderType(cartMode);
        }
    }

    @Override
    public double getTotalPrice() {
        return orders.values().stream().mapToDouble(ShopOrder::getTotalPrice).sum();
    }
}
