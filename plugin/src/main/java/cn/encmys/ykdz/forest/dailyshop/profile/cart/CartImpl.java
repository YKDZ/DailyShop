package cn.encmys.ykdz.forest.dailyshop.profile.cart;

import cn.encmys.ykdz.forest.dailyshop.api.DailyShop;
import cn.encmys.ykdz.forest.dailyshop.api.database.schema.CartSchema;
import cn.encmys.ykdz.forest.dailyshop.api.profile.cart.Cart;
import cn.encmys.ykdz.forest.dailyshop.api.shop.Shop;
import cn.encmys.ykdz.forest.dailyshop.api.shop.order.ShopOrder;
import cn.encmys.ykdz.forest.dailyshop.api.shop.order.enums.OrderType;
import cn.encmys.ykdz.forest.dailyshop.api.shop.order.enums.SettlementResult;
import cn.encmys.ykdz.forest.dailyshop.shop.order.ShopOrderImpl;
import org.jetbrains.annotations.NotNull;

import java.util.*;

public class CartImpl implements Cart {
    private final UUID ownerUUID;
    private final Map<String, ShopOrder> orders = new HashMap<>();
    private OrderType mode = OrderType.SELL_TO;

    public CartImpl(UUID ownerUUID) {
        this.ownerUUID = ownerUUID;
        CartSchema schema = DailyShop.DATABASE_FACTORY.getCartDao().querySchema(ownerUUID);
        if (schema != null) {
            orders.putAll(schema.orders());
            mode = schema.mode();
        }
    }

    @Override
    public void setOrder(String shopId, ShopOrder shopOrder) {
        orders.put(shopId, shopOrder);
    }

    @Override
    @NotNull
    public Map<String, ShopOrder> getOrders() {
        return Collections.unmodifiableMap(orders);
    }

    @Override
    public ShopOrder getOrder(@NotNull String shopId) {
        ShopOrder cartOrder = orders.get(shopId);
        if (cartOrder == null) {
            cartOrder = new ShopOrderImpl(ownerUUID)
                    .setOrderType(OrderType.SELL_TO);
            orders.put(shopId, cartOrder);
        }
        return cartOrder;
    }

    @Override
    public UUID getOwnerUUID() {
        return ownerUUID;
    }

    @Override
    public Map<String, SettlementResult> settle() {
        Map<String, SettlementResult> result = new HashMap<>() {{
            for (String shopId : orders.keySet()) {
                put(shopId, SettlementResult.UNKNOWN);
            }
        }};
        // 目前的设计难以同时处理两个来自不同商店的 ShopOrder
        // 所以选择将购物车中的商品按商店分开结算（允许仅部分交易成功）
        orders.entrySet().removeIf(entry -> {
            String shopId = entry.getKey();
            Shop shop = DailyShop.SHOP_FACTORY.getShop(shopId);
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
    public OrderType getMode() {
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
