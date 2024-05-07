package cn.encmys.ykdz.forest.dailyshop.product;

import cn.encmys.ykdz.forest.dailyshop.DailyShop;
import cn.encmys.ykdz.forest.dailyshop.api.product.Product;
import cn.encmys.ykdz.forest.dailyshop.api.product.enums.ProductType;
import cn.encmys.ykdz.forest.dailyshop.builder.BaseItemDecorator;
import cn.encmys.ykdz.forest.dailyshop.price.Price;
import cn.encmys.ykdz.forest.dailyshop.rarity.RarityImpl;
import cn.encmys.ykdz.forest.dailyshop.shop.ShopImpl;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.Map;

public class BundleProduct extends Product {
    private final Map<String, Integer> bundleContents;

    public BundleProduct(
            String id,
            Price buyPrice,
            Price sellPrice,
            RarityImpl rarity,
            BaseItemDecorator iconBuilder,
            Map<String, Integer> bundleContents) {
        super(id, buyPrice, sellPrice, rarity, iconBuilder, null, false);
        this.bundleContents = bundleContents;
    }

    @Override
    public ProductType getType() {
        return ProductType.BUNDLE;
    }

    public Map<String, Integer> getBundleContents() {
        return bundleContents;
    }

    @Override
    public void give(@NotNull ShopImpl shop, @NotNull Player player, int stack) {
        for (Map.Entry<String, Integer> entry : bundleContents.entrySet()) {
            String contentId = entry.getKey();
            int contentStack = entry.getValue();
            DailyShop.PRODUCT_FACTORY.getProduct(contentId).give(shop, player, contentStack);
        }
    }

    @Override
    public void take(@NotNull ShopImpl shop, @NotNull Player player, int stack) {
        for (Map.Entry<String, Integer> entry : bundleContents.entrySet()) {
            String contentId = entry.getKey();
            int contentStack = entry.getValue();

            DailyShop.PRODUCT_FACTORY.getProduct(contentId).take(shop, player, contentStack);
        }
    }

    @Override
    public int has(@NotNull ShopImpl shop, @NotNull Player player, int stack) {
        int count = Integer.MAX_VALUE;

        for (Map.Entry<String, Integer> entry : bundleContents.entrySet()) {
            String contentId = entry.getKey();
            Product content = DailyShop.PRODUCT_FACTORY.getProduct(contentId);
            int contentStack = entry.getValue();

            if (content.has(shop, player, 1) < contentStack) {
                count = 0;
                break;
            }

            count = Math.min(count, content.has(shop, player, 1) / contentStack);
        }

        return count;
    }

    @Override
    public boolean canHold(@NotNull ShopImpl shop, @NotNull Player player, int stack) {
        for (Map.Entry<String, Integer> entry : bundleContents.entrySet()) {
            String contentId = entry.getKey();
            int contentStack = entry.getValue();
            Product content = DailyShop.PRODUCT_FACTORY.getProduct(contentId);
            if (!content.canHold(shop, player, contentStack)) {
                return false;
            }
        }
        return true;
    }
}
