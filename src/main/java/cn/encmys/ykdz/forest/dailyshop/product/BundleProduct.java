package cn.encmys.ykdz.forest.dailyshop.product;

import cn.encmys.ykdz.forest.dailyshop.DailyShop;
import cn.encmys.ykdz.forest.dailyshop.api.product.Product;
import cn.encmys.ykdz.forest.dailyshop.builder.ProductIconBuilder;
import cn.encmys.ykdz.forest.dailyshop.builder.ProductItemBuilder;
import cn.encmys.ykdz.forest.dailyshop.price.Price;
import cn.encmys.ykdz.forest.dailyshop.price.PricePair;
import cn.encmys.ykdz.forest.dailyshop.product.enums.FailureReason;
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
    public FailureReason sellTo(@Nullable String shopId, Player player) {
        FailureReason failure = canSellTo(shopId, player);
        if (failure != FailureReason.SUCCESS) {
            return failure;
        }
        Shop shop = DailyShop.getShopFactory().getShop(shopId);

        BalanceUtils.removeBalance(player, shop.getBuyPrice(getId()));
        give(shopId, player);

        return FailureReason.SUCCESS;
    }

    @Override
    public FailureReason canSellTo(@Nullable String shopId, Player player) {
        double price = DailyShop.getShopFactory().getShop(shopId).getBuyPrice(getId());
        if (price == -1d) {
            return FailureReason.DISABLE;
        }
        if (BalanceUtils.checkBalance(player) <= price) {
            return FailureReason.MONEY;
        }
        return FailureReason.SUCCESS;
    }

    @Override
    public void give(@Nullable String shopId, @NotNull Player player) {
        for (String productId : getBundleContents()) {
            DailyShop.getProductFactory().getProduct(productId).give(shopId, player);
        }
    }

    @Override
    public FailureReason buyFrom(@Nullable String shopId, Player player) {
        FailureReason failure = canBuyFrom(shopId, player);
        if (failure != FailureReason.SUCCESS) {
            return failure;
        }

        take(player, 1);

        return FailureReason.SUCCESS;
    }

    @Override
    public int buyAllFrom(@Nullable String shopId, Player player) {
        return 0;
    }

    @Override
    public FailureReason canBuyFrom(@Nullable String shopId, Player player) {
        if (DailyShop.getShopFactory().getShop(shopId).getSellPrice(getId()) == -1d) {
            return FailureReason.DISABLE;
        }

        for (String id : bundleContents) {
            FailureReason failure = DailyShop.getProductFactory().getProduct(id).canBuyFrom(shopId, player);
            if (failure != FailureReason.SUCCESS) {
                return failure;
            }
        }
        return FailureReason.SUCCESS;
    }

    @Override
    public void take(Player player, int stack) {
        for (String id : getBundleContents()) {
            DailyShop.getProductFactory().getProduct(id).take(player, stack);
        }
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
