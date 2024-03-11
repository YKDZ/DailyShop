package cn.encmys.ykdz.forest.dailyshop.api.product;

import cn.encmys.ykdz.forest.dailyshop.builder.ProductIconBuilder;
import cn.encmys.ykdz.forest.dailyshop.builder.ProductItemBuilder;
import cn.encmys.ykdz.forest.dailyshop.enums.ProductType;
import cn.encmys.ykdz.forest.dailyshop.price.Price;
import cn.encmys.ykdz.forest.dailyshop.rarity.Rarity;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.Nullable;

import java.util.HashMap;
import java.util.List;

public abstract class Product {
    private HashMap<String, ItemStack> productItemCache = new HashMap<>();
    private final String id;
    private final Price buyPrice;
    private final Price sellPrice;
    private final Rarity rarity;
    private final ProductIconBuilder productIconBuilder;
    private final ProductItemBuilder productItemBuilder;
    private final boolean isCacheable;

    public Product(
            String id,
            Price buyPrice,
            Price sellPrice,
            Rarity rarity,
            ProductIconBuilder productIconBuilder,
            ProductItemBuilder productItemBuilder,
            boolean isCacheable) {
        this.id = id;
        this.buyPrice = buyPrice;
        this.sellPrice = sellPrice;
        this.rarity = rarity;
        this.productIconBuilder = productIconBuilder;
        this.productItemBuilder = productItemBuilder;
        this.isCacheable = isCacheable;
    }

    public String getId() {
        return id;
    }

    public Rarity getRarity() {
        return rarity;
    }

    public Price getBuyPriceProvider() {
        return buyPrice;
    }

    public Price getSellPriceProvider() {
        return sellPrice;
    }

    public void updatePrice(String shopId) {
        getBuyPriceProvider().update(shopId);
        getSellPriceProvider().update(shopId);
    }

    public abstract ProductType getType();

    public ProductIconBuilder getIconBuilder() {
        return productIconBuilder;
    }

    public ProductItemBuilder getProductItemBuilder() {
        return productItemBuilder;
    }

    public abstract List<String> getBundleContents();

    /**
     * @param shopId Seller
     * @param player Buyer
     */
    public abstract boolean sellTo(@Nullable String shopId, Player player);

    public abstract boolean canSellTo(@Nullable String shopId, Player player);

    /**
     * @param shopId Buyer
     * @param player Seller
     */
    public abstract boolean buyFrom(@Nullable String shopId, Player player);

    /**
     * @param shopId Buyer
     * @param player Seller
     */
    public abstract boolean buyAllFrom(@Nullable String shopId, Player player);

    /**
     * @param shopId Buyer
     * @param player Seller
     */
    public abstract boolean canBuyFrom(@Nullable String shopId, Player player);

    public void cacheProductItem(String shopId, @Nullable Player player) {
        productItemCache.put(shopId, getProductItemBuilder().build(player));
    }

    public HashMap<String, ItemStack> getProductItemCache() {
        return productItemCache;
    }

    public boolean isCacheable() {
        return isCacheable;
    }
}
