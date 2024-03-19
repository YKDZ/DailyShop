package cn.encmys.ykdz.forest.dailyshop.product;

import cn.encmys.ykdz.forest.dailyshop.DailyShop;
import cn.encmys.ykdz.forest.dailyshop.api.product.Product;
import cn.encmys.ykdz.forest.dailyshop.builder.ProductIconBuilder;
import cn.encmys.ykdz.forest.dailyshop.builder.ProductItemBuilder;
import cn.encmys.ykdz.forest.dailyshop.price.Price;
import cn.encmys.ykdz.forest.dailyshop.price.PricePair;
import cn.encmys.ykdz.forest.dailyshop.product.enums.ProductType;
import cn.encmys.ykdz.forest.dailyshop.rarity.Rarity;
import cn.encmys.ykdz.forest.dailyshop.shop.Shop;
import cn.encmys.ykdz.forest.dailyshop.util.BalanceUtils;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
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

    public List<String> getBundleContents() {
        return bundleContents;
    }

    @Override
    public boolean sellTo(@Nullable String shopId, Player player) {
        if (!canSellTo(shopId, player)) {
            return false;
        }
        Shop shop = DailyShop.getShopFactory().getShop(shopId);

        BalanceUtils.removeBalance(player, shop.getBuyPrice(getId()));
        give(shopId, player);

        return true;
    }

    @Override
    public boolean canSellTo(@Nullable String shopId, Player player) {
        Shop shop = DailyShop.getShopFactory().getShop(shopId);
        return BalanceUtils.checkBalance(player) >= shop.getBuyPrice(getId());
    }

    @Override
    public void give(@Nullable String shopId, @NotNull Player player) {
        for (String productId : getBundleContents()) {
            DailyShop.getProductFactory().getProduct(productId).give(shopId, player);
        }
    }

    @Override
    public boolean buyFrom(@Nullable String shopId, Player player) {
        if (!canBuyFrom(shopId, player)) {
            return false;
        }

        take(player, 1);

        return true;
    }

    @Override
    public int buyAllFrom(@Nullable String shopId, Player player) {
        return 0;
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

    @Override
    public boolean take(Player player, int stack) {
        for (String id : getBundleContents()) {
            DailyShop.getProductFactory().getProduct(id).take(player, stack);
        }
        return true;
    }

    @Override
    public int takeAll(Player player) {
        return 0;
    }

    @Override
    public PricePair getNewPricePair(@Nullable String shopId) {
        Price buyPrice = getBuyPrice();
        Price sellPrice = getSellPrice();
        double buy = 0d;
        double sell = 0d;

        switch (buyPrice.getPriceMode()) {
            case BUNDLE_AUTO_NEW -> {
                for (String contentId : getBundleContents()) {
                    Product content = DailyShop.getProductFactory().getProduct(contentId);
                    buy += content.getBuyPrice().getNewPrice();
                }
            } case BUNDLE_AUTO_REUSE -> {
                Shop shop = DailyShop.getShopFactory().getShop(shopId);
                for (String contentId : getBundleContents()) {
                    buy += shop.getBuyPrice(contentId);
                }
            } default -> buy = buyPrice.getNewPrice();
        }

        switch (sellPrice.getPriceMode()) {
            case BUNDLE_AUTO_NEW -> {
                for (String contentId : getBundleContents()) {
                    Product content = DailyShop.getProductFactory().getProduct(contentId);
                    sell += content.getSellPrice().getNewPrice();
                }
            } case BUNDLE_AUTO_REUSE -> {
                Shop shop = DailyShop.getShopFactory().getShop(shopId);
                for (String contentId : getBundleContents()) {
                    sell += shop.getSellPrice(contentId);
                }
            } default -> sell = sellPrice.getNewPrice();
        }

        return new PricePair(buy, sell);
    }
}
