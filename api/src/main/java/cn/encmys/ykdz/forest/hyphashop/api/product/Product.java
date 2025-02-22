package cn.encmys.ykdz.forest.hyphashop.api.product;

import cn.encmys.ykdz.forest.hyphascript.context.Context;
import cn.encmys.ykdz.forest.hyphashop.api.item.decorator.BaseItemDecorator;
import cn.encmys.ykdz.forest.hyphashop.api.price.Price;
import cn.encmys.ykdz.forest.hyphashop.api.product.enums.ProductType;
import cn.encmys.ykdz.forest.hyphashop.api.product.stock.ProductStock;
import cn.encmys.ykdz.forest.hyphashop.api.rarity.Rarity;
import cn.encmys.ykdz.forest.hyphashop.api.shop.Shop;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;

public abstract class Product {
    @NotNull
    private final String id;
    @NotNull
    private final Price buyPrice;
    @NotNull
    private final Price sellPrice;
    @NotNull
    private final Rarity rarity;
    @NotNull
    private final BaseItemDecorator iconDecorator;
    @Nullable
    private final BaseItemDecorator itemDecorator;
    @NotNull
    private final ProductStock productStock;
    @NotNull
    private final List<String> listConditions = new ArrayList<>();
    @NotNull
    private final Context scriptContext;
    protected final boolean isCacheable;

    public Product(
            @NotNull String id,
            @NotNull Price buyPrice,
            @NotNull Price sellPrice,
            @NotNull Rarity rarity,
            @NotNull BaseItemDecorator iconDecorator,
            @Nullable BaseItemDecorator itemDecorator,
            @NotNull ProductStock productStock,
            @NotNull List<String> listConditions,
            @NotNull Context scriptContext,
            boolean isCacheable) {
        this.id = id;
        this.buyPrice = buyPrice;
        this.sellPrice = sellPrice;
        this.rarity = rarity;
        this.iconDecorator = iconDecorator;
        this.itemDecorator = itemDecorator;
        this.productStock = productStock;
        this.listConditions.addAll(listConditions);
        this.isCacheable = isCacheable;
        this.scriptContext = scriptContext;
    }

    public @NotNull String getId() {
        return id;
    }

    public @NotNull Rarity getRarity() {
        return rarity;
    }

    public @NotNull Price getBuyPrice() {
        return buyPrice;
    }

    public @NotNull Price getSellPrice() {
        return sellPrice;
    }

    public abstract ProductType getType();

    @NotNull
    public BaseItemDecorator getIconDecorator() {
        return iconDecorator;
    }

    @Nullable
    public BaseItemDecorator getProductItemDecorator() {
        return itemDecorator;
    }

    public @NotNull List<String> getListConditions() {
        return listConditions;
    }

    public @NotNull ProductStock getProductStock() {
        return productStock;
    }

    public abstract void give(@NotNull Shop shop, @NotNull Player player, int stack);

    public abstract void give(@NotNull Shop shop, @NotNull Inventory inv, @NotNull Player player, int stack);

    public abstract void take(@NotNull Shop shop, @NotNull Player player, int stack);

    public abstract void take(@NotNull Shop shop, @NotNull Iterable<ItemStack> inv, @NotNull Player player, int stack);

    public abstract int has(@NotNull Shop shop, @NotNull Player player, int stack);

    public abstract int has(@NotNull Shop shop, @NotNull Iterable<ItemStack> inv, @NotNull Player player, int stack);

    public abstract boolean canHold(@NotNull Shop shop, @NotNull Player player, int stack);

    public abstract boolean canHold(@NotNull Shop shop, @NotNull Inventory inv, @NotNull Player player, int stack);

    public abstract boolean isProductItemCacheable();

    public abstract boolean isMatch(@NotNull String shopId, @NotNull ItemStack item, @NotNull Player player);

    public @NotNull Context getScriptContext() {
        return scriptContext;
    }
}
