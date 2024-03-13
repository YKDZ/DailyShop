package cn.encmys.ykdz.forest.dailyshop.product;

import cn.encmys.ykdz.forest.dailyshop.DailyShop;
import cn.encmys.ykdz.forest.dailyshop.api.product.Product;
import cn.encmys.ykdz.forest.dailyshop.builder.ProductIconBuilder;
import cn.encmys.ykdz.forest.dailyshop.builder.ProductItemBuilder;
import cn.encmys.ykdz.forest.dailyshop.enums.ProductType;
import cn.encmys.ykdz.forest.dailyshop.price.Price;
import cn.encmys.ykdz.forest.dailyshop.rarity.Rarity;
import cn.encmys.ykdz.forest.dailyshop.shop.Shop;
import cn.encmys.ykdz.forest.dailyshop.util.BalanceUtils;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class BundleProduct extends Product {
    private final List<String> bundleContents;

    public BundleProduct(
            String id,
            Price buyPrice,
            Price sellPrice,
            Rarity rarity,
            ProductIconBuilder productIconBuilder,
            ProductItemBuilder productItemBuilder,
            List<String> bundleContents) {
        super(id, buyPrice, sellPrice, rarity, productIconBuilder, productItemBuilder, false);
        this.bundleContents = bundleContents;
    }

    @Override
    public ProductType getType() {
        return ProductType.BUNDLE;
    }

    @Override
    public List<String> getBundleContents() {
        return bundleContents;
    }

    @Override
    public boolean sellTo(@Nullable String shopId, Player player) {
        if (!canSellTo(shopId, player)) {
            return false;
        }
        Shop shop = DailyShop.getShopFactory().getShop(shopId);

        BalanceUtils.removeBalance(player, shop.getBuyPrice(shopId));

        for (String productId : bundleContents) {
            DailyShop.getProductFactory().getProduct(productId).sellTo(shopId, player);
        }

        return true;
    }

    @Override
    public boolean canSellTo(@Nullable String shopId, Player player) {
        Shop shop = DailyShop.getShopFactory().getShop(shopId);
        return BalanceUtils.checkBalance(player) >= shop.getBuyPrice(getId());
    }

    @Override
    public boolean buyFrom(@Nullable String shopId, Player player) {
        if (!canBuyFrom(shopId, player)) {
            return false;
        }

        for (String id : bundleContents) {
            DailyShop.getProductFactory().getProduct(id).buyFrom(shopId, player);
        }

        return true;
    }

    @Override
    public boolean buyAllFrom(@Nullable String shopId, Player player) {
        return false;
    }

    @Override
    public boolean canBuyFrom(@Nullable String shopId, Player player) {
        for (String id : bundleContents) {
            if (!DailyShop.getProductFactory().getProduct(id).canBuyFrom(shopId, player)) {
                return false;
            }
        }
        return true;
    }
}
