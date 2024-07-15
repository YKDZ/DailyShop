package cn.encmys.ykdz.forest.dailyshop.api.event;

import cn.encmys.ykdz.forest.dailyshop.api.product.Product;
import cn.encmys.ykdz.forest.dailyshop.api.shop.Shop;
import org.bukkit.event.Cancellable;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public class ShopRestockEvent extends Event implements Cancellable {
    private static final HandlerList handlerList = new HandlerList();
    private boolean isCancelled = false;
    private final Shop shop;
    private final List<Product> productsPreparedToBeListed;

    public ShopRestockEvent(Shop shop, List<Product> productsPreparedToBeListed) {
        this.shop = shop;
        this.productsPreparedToBeListed = productsPreparedToBeListed;
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

    public Shop getShop() {
        return shop;
    }

    public List<Product> getProductsPreparedToBeListed() {
        return productsPreparedToBeListed;
    }
}
