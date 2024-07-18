package cn.encmys.ykdz.forest.dailyshop.api.event;

import cn.encmys.ykdz.forest.dailyshop.api.shop.Shop;
import cn.encmys.ykdz.forest.dailyshop.api.shop.order.ShopOrder;
import org.bukkit.entity.Player;
import org.bukkit.event.Cancellable;
import org.bukkit.event.HandlerList;
import org.bukkit.event.player.PlayerEvent;
import org.jetbrains.annotations.NotNull;

public class ProductTradeEvent extends PlayerEvent implements Cancellable {
    private static final HandlerList handlerList = new HandlerList();
    private boolean isCancelled = false;
    private final Shop shop;
    private final ShopOrder order;

    public ProductTradeEvent(@NotNull Player who, @NotNull Shop shop, @NotNull ShopOrder order) {
        super(who);
        this.shop = shop;
        this.order = order;
    }

    @NotNull
    public Shop getShop() {
        return shop;
    }

    @NotNull
    public ShopOrder getOrder() {
        return order;
    }

    @Override
    public boolean isCancelled() {
        return isCancelled;
    }

    @Override
    public void setCancelled(boolean cancel) {
        isCancelled = cancel;
    }

    @NotNull
    @Override
    public HandlerList getHandlers() {
        return handlerList;
    }

    public static HandlerList getHandlerList() {
        return handlerList;
    }
}
