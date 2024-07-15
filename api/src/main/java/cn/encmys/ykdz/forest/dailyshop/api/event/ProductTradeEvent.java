package cn.encmys.ykdz.forest.dailyshop.api.event;

import cn.encmys.ykdz.forest.dailyshop.api.shop.Shop;
import cn.encmys.ykdz.forest.dailyshop.api.shop.order.ShopOrder;
import org.bukkit.entity.Player;
import org.bukkit.event.Cancellable;
import org.bukkit.event.HandlerList;
import org.bukkit.event.player.PlayerEvent;
import org.jetbrains.annotations.NotNull;

public class PlayerSellProductEvent extends PlayerEvent implements Cancellable {
    private static final HandlerList handlerList = new HandlerList();
    private boolean isCancelled = false;
    private Shop shop;
    private ShopOrder order;

    public PlayerBuyProductEvent(@NotNull Player who, Shop shop, ShopOrder order) {
        super(who);
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
        return null;
    }
}
