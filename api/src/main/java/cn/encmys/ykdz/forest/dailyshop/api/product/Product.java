package cn.encmys.ykdz.forest.dailyshop.api.product;

import cn.encmys.ykdz.forest.dailyshop.api.item.decorator.BaseItemDecorator;
import cn.encmys.ykdz.forest.dailyshop.api.price.Price;
import cn.encmys.ykdz.forest.dailyshop.api.product.enums.ProductType;
import cn.encmys.ykdz.forest.dailyshop.api.product.stock.ProductStock;
import cn.encmys.ykdz.forest.dailyshop.api.rarity.Rarity;
import cn.encmys.ykdz.forest.dailyshop.api.shop.Shop;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public abstract class Product {
    private final String id;
    private final Price buyPrice;
    private final Price sellPrice;
    private final Rarity rarity;
    private final BaseItemDecorator iconDecorator;
    private final BaseItemDecorator itemDecorator;
    private final ProductStock productStock;
    private final boolean isCacheable;

    public Product(
            String id,
            Price buyPrice,
            Price sellPrice,
            Rarity rarity,
            BaseItemDecorator iconDecorator,
            BaseItemDecorator itemDecorator,
            ProductStock productStock,
            boolean isCacheable) {
        this.id = id;
        this.buyPrice = buyPrice;
        this.sellPrice = sellPrice;
        this.rarity = rarity;
        this.iconDecorator = iconDecorator;
        this.itemDecorator = itemDecorator;
        this.productStock = productStock;
        this.isCacheable = isCacheable;
    }

    public String getId() {
        return id;
    }

    public Rarity getRarity() {
        return rarity;
    }

    public Price getBuyPrice() {
        return buyPrice;
    }

    public Price getSellPrice() {
        return sellPrice;
    }

    public abstract ProductType getType();

    public BaseItemDecorator getIconDecorator() {
        return iconDecorator;
    }

    public BaseItemDecorator getItemDecorator() {
        return itemDecorator;
    }

    public ProductStock getProductStock() {
        return productStock;
    }

    public abstract void give(@NotNull Shop shop, @NotNull Player player, int stack);

    public abstract void give(@NotNull Shop shop, @NotNull Inventory inv, @Nullable Player player, int stack);

    public abstract void take(@NotNull Shop shop, @NotNull Player player, int stack);

    public abstract void take(@NotNull Shop shop, @NotNull Iterable<ItemStack> inv, @Nullable Player player, int stack);

    public abstract int has(@NotNull Shop shop, @NotNull Player player, int stack);

    public abstract int has(@NotNull Shop shop, @NotNull Iterable<ItemStack> inv, @Nullable Player player, int stack);

    public abstract boolean canHold(@NotNull Shop shop, @NotNull Player player, int stack);

    public abstract boolean canHold(@NotNull Shop shop, @NotNull Inventory inv, @Nullable Player player, int stack);

    public boolean isCacheable() {
        return isCacheable;
    }

    public abstract boolean isMatch(@NotNull String shopId, ItemStack item, @Nullable Player player);
}
