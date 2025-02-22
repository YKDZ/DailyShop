package cn.encmys.ykdz.forest.hyphashop.api.event.product;

import cn.encmys.ykdz.forest.hyphashop.api.product.Product;
import cn.encmys.ykdz.forest.hyphashop.api.shop.Shop;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;
import org.jetbrains.annotations.NotNull;

public class ProductListEvent extends Event {
    private static final HandlerList handlerList = new HandlerList();
    private final Shop shop;
    private final Product product;

    public ProductListEvent(@NotNull Shop shop, @NotNull Product product) {
        this.shop = shop;
        this.product = product;
    }

    public static HandlerList getHandlerList() {
        return handlerList;
    }

    @NotNull
    public Shop getShop() {
        return shop;
    }

    @NotNull
    public Product getProduct() {
        return product;
    }

    @NotNull
    @Override
    public HandlerList getHandlers() {
        return handlerList;
    }
}
