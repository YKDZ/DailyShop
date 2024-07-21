package cn.encmys.ykdz.forest.dailyshop.api.event.shop;

import cn.encmys.ykdz.forest.dailyshop.api.shop.Shop;
import cn.encmys.ykdz.forest.dailyshop.api.shop.order.ShopOrder;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;
import org.jetbrains.annotations.NotNull;

public class ShopSettleEvent extends Event {
    private static final HandlerList handlerList = new HandlerList();
    private final Shop shop;
    private final ShopOrder order;

    public ShopSettleEvent(@NotNull Shop shop, @NotNull ShopOrder order) {
        this.shop = shop;
        this.order = order;
    }

    public static HandlerList getHandlerList() {
        return handlerList;
    }

    @NotNull
    public Shop getShop() {
        return shop;
    }

    @NotNull
    public ShopOrder getOrder() {
        return order;
    }

    @NotNull
    @Override
    public HandlerList getHandlers() {
        return handlerList;
    }
}
