package cn.encmys.ykdz.forest.dailyshop.api.event.product;

import cn.encmys.ykdz.forest.dailyshop.api.product.Product;
import cn.encmys.ykdz.forest.dailyshop.api.shop.Shop;
import org.bukkit.event.Cancellable;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;
import org.jetbrains.annotations.NotNull;

public class ProductPreListEvent extends Event implements Cancellable {
    private static final HandlerList handlerList = new HandlerList();
    private boolean isCancelled = false;
    private final Shop shop;
    private final Product product;

    public ProductPreListEvent(@NotNull Shop shop, @NotNull Product product) {
        this.shop = shop;
        this.product = product;
    }

    @NotNull
    public Shop getShop() {
        return shop;
    }

    @NotNull
    public Product getProduct() {
        return product;
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

