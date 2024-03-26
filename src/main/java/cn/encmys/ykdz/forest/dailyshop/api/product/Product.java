package cn.encmys.ykdz.forest.dailyshop.api.product;

import cn.encmys.ykdz.forest.dailyshop.builder.BaseItemDecorator;
import cn.encmys.ykdz.forest.dailyshop.price.Price;
import cn.encmys.ykdz.forest.dailyshop.price.PricePair;
import cn.encmys.ykdz.forest.dailyshop.product.enums.FailureReason;
import cn.encmys.ykdz.forest.dailyshop.product.enums.ProductType;
import cn.encmys.ykdz.forest.dailyshop.rarity.Rarity;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

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

    /**
     * @param shopId Seller
     * @param player Buyer
     */
    public abstract FailureReason sellTo(@Nullable String shopId, Player player);

    public abstract FailureReason canSellTo(@Nullable String shopId, Player player);

    public abstract void give(@Nullable String shopId, @NotNull Player player);

    /**
     * @param shopId Buyer
     * @param player Seller
     */
    public abstract FailureReason buyFrom(@Nullable String shopId, Player player);

    /**
     * @param shopId Buyer
     * @param player Seller
     */
    public abstract int buyAllFrom(@Nullable String shopId, Player player);

    /**
     * @param shopId Buyer
     * @param player Seller
     */
    public abstract FailureReason canBuyFrom(@Nullable String shopId, Player player);

    public abstract void take(String shopId, Player player, int stack);

    public abstract int takeAll(String shopId, Player player);

    public boolean isCacheable() {
        return isCacheable;
    }

    public abstract PricePair getNewPricePair(@Nullable String shopId);
}
