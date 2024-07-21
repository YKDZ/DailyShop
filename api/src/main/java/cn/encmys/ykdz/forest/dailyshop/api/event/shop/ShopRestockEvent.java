package cn.encmys.ykdz.forest.dailyshop.api.event.shop;

import cn.encmys.ykdz.forest.dailyshop.api.product.Product;
import cn.encmys.ykdz.forest.dailyshop.api.shop.Shop;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;
import org.jetbrains.annotations.NotNull;

import java.util.Collections;
import java.util.List;

/**
 * This event is triggered when the shop restock is complete,
 * at which point the products to be listed have already been determined and cannot be changed.
 */
public class ShopRestockEvent extends Event {
    private static final HandlerList handlerList = new HandlerList();
    private final Shop shop;
    private final List<Product> listedProducts;

    public ShopRestockEvent(@NotNull Shop shop, @NotNull List<Product> listedProducts) {
        this.shop = shop;
        this.listedProducts = listedProducts;
    }

    public static HandlerList getHandlerList() {
        return handlerList;
    }

    @NotNull
    public Shop getShop() {
        return shop;
    }

    @NotNull
    public List<Product> getListedProducts() {
        return Collections.unmodifiableList(listedProducts);
    }

    @NotNull
    @Override
    public HandlerList getHandlers() {
        return handlerList;
    }
}
