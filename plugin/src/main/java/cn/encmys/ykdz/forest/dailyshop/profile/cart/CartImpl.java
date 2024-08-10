package cn.encmys.ykdz.forest.dailyshop.profile.cart;

import cn.encmys.ykdz.forest.dailyshop.api.DailyShop;
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
    private Map<String, ShopOrder> cartOrders = new HashMap<>();
    private OrderType cartMode = OrderType.SELL_TO;

    public CartImpl(UUID ownerUUID) {
        this.ownerUUID = ownerUUID;
    }

    @Override
    public void setOrder(String shopId, ShopOrder shopOrder) {
        cartOrders.put(shopId, shopOrder);
    }

    @Override
    public @NotNull Map<String, ShopOrder> getOrders() {
        return Collections.unmodifiableMap(cartOrders);
    }

    @Override
    public void setOrders(Map<String, ShopOrder> cartOrders) {
        this.cartOrders = cartOrders;
    }

    @Override
    public ShopOrder getOrder(@NotNull String shopId) {
        ShopOrder cartOrder = cartOrders.get(shopId);
        if (cartOrder == null) {
            cartOrder = new ShopOrderImpl(ownerUUID)
                    .setOrderType(OrderType.SELL_TO);
            cartOrders.put(shopId, cartOrder);
        }
        return cartOrder;
    }

    @Override
    public UUID getOwnerUUID() {
        return ownerUUID;
    }

    @Override
    public SettlementResult settle() {
        if (cartOrders.isEmpty()) {
            return SettlementResult.EMPTY;
        }
        ShopOrder orderForCheck = new ShopOrderImpl(ownerUUID);
        // TODO 合并检查
        clear();
        return SettlementResult.SUCCESS;
    }

    @Override
    public void clear() {
        cartOrders.clear();
    }

    @Override
    public void clean() {
        Iterator<Map.Entry<String, ShopOrder>> iterator = cartOrders.entrySet().iterator();
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
        return cartMode;
    }

    @Override
    public void setMode(@NotNull OrderType cartMode) {
        this.cartMode = cartMode;
        for (ShopOrder order : cartOrders.values()) {
            order.setOrderType(cartMode);
        }
    }

    @Override
    public double getTotalPrice() {
        return cartOrders.values().stream().mapToDouble(ShopOrder::getTotalPrice).sum();
    }
}
