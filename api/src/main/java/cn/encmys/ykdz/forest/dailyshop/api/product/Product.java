package cn.encmys.ykdz.forest.dailyshop.api.product;

import cn.encmys.ykdz.forest.dailyshop.api.builder.BaseItemDecorator;
import cn.encmys.ykdz.forest.dailyshop.api.price.Price;
import cn.encmys.ykdz.forest.dailyshop.api.product.enums.ProductType;
import cn.encmys.ykdz.forest.dailyshop.api.rarity.Rarity;
import cn.encmys.ykdz.forest.dailyshop.api.shop.Shop;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

public abstract class Product {
    private final String id;
    private final Price buyPrice;
    private final Price sellPrice;
    private final Rarity rarity;
    private final BaseItemDecorator iconBuilder;
    private final BaseItemDecorator productItemBuilder;
    private final boolean isCacheable;

    public Product(
            String id,
            Price buyPrice,
            Price sellPrice,
            Rarity rarity,
            BaseItemDecorator iconBuilder,
            BaseItemDecorator productItemBuilder,
            boolean isCacheable) {
        this.id = id;
        this.buyPrice = buyPrice;
        this.sellPrice = sellPrice;
        this.rarity = rarity;
        this.iconBuilder = iconBuilder;
        this.productItemBuilder = productItemBuilder;
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

    public BaseItemDecorator getIconBuilder() {
        return iconBuilder;
    }

    public BaseItemDecorator getProductItemBuilder() {
        return productItemBuilder;
    }

    public abstract void give(@NotNull Shop shop, @NotNull Player player, int stack);

    public abstract void take(@NotNull Shop shop, @NotNull Player player, int stack);

    public abstract int has(@NotNull Shop shop, @NotNull Player player, int stack);

    public abstract boolean canHold(@NotNull Shop shop, @NotNull Player player, int stack);

    public boolean isCacheable() {
        return isCacheable;
    }
}
