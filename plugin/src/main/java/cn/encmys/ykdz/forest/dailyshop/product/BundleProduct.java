package cn.encmys.ykdz.forest.dailyshop.product;

import cn.encmys.ykdz.forest.dailyshop.api.DailyShop;
import cn.encmys.ykdz.forest.dailyshop.api.item.decorator.BaseItemDecorator;
import cn.encmys.ykdz.forest.dailyshop.api.price.Price;
import cn.encmys.ykdz.forest.dailyshop.api.product.Product;
import cn.encmys.ykdz.forest.dailyshop.api.product.enums.ProductType;
import cn.encmys.ykdz.forest.dailyshop.api.product.stock.ProductStock;
import cn.encmys.ykdz.forest.dailyshop.api.rarity.Rarity;
import cn.encmys.ykdz.forest.dailyshop.api.shop.Shop;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Map;

public class BundleProduct extends Product {
    private final Map<String, Integer> bundleContents;

    public BundleProduct(
            String id,
            Price buyPrice,
            Price sellPrice,
            Rarity rarity,
            BaseItemDecorator iconBuilder,
            ProductStock productStock,
            Map<String, Integer> bundleContents) {
        super(id, buyPrice, sellPrice, rarity, iconBuilder, null, productStock, false);
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
    public void give(@NotNull Shop shop, @NotNull Player player, int stack) {
        give(shop, player.getInventory(), player, stack);
    }

    @Override
    public void give(@NotNull Shop shop, @NotNull Inventory inv, @Nullable Player player, int stack) {
        for (Map.Entry<String, Integer> entry : bundleContents.entrySet()) {
            String contentId = entry.getKey();
            Product content = DailyShop.PRODUCT_FACTORY.getProduct(contentId);

            if (content == null) {
                break;
            }

            int contentStack = entry.getValue() * stack;
            content.give(shop, inv, player, contentStack);
        }
    }

    @Override
    public void take(@NotNull Shop shop, @NotNull Player player, int stack) {
        take(shop, player.getInventory(), player, stack);
    }

    @Override
    public void take(@NotNull Shop shop, @NotNull Iterable<ItemStack> inv, @Nullable Player player, int stack) {
        for (Map.Entry<String, Integer> entry : bundleContents.entrySet()) {
            String contentId = entry.getKey();
            Product content = DailyShop.PRODUCT_FACTORY.getProduct(contentId);

            if (content == null) {
                break;
            }

            int contentStack = entry.getValue() * stack;

            content.take(shop, inv, player, contentStack);
        }
    }

    @Override
    public int has(@NotNull Shop shop, @NotNull Player player, int stack) {
        return has(shop, player.getInventory(), player, stack);
    }

    @Override
    public int has(@NotNull Shop shop, @NotNull Iterable<ItemStack> inv, @Nullable Player player, int stack) {
        int count = Integer.MAX_VALUE;

        for (Map.Entry<String, Integer> entry : bundleContents.entrySet()) {
            String contentId = entry.getKey();
            Product content = DailyShop.PRODUCT_FACTORY.getProduct(contentId);

            if (content == null) {
                break;
            }

            int contentStack = entry.getValue() * stack;

            if (content.has(shop, inv, player, 1) < contentStack) {
                count = 0;
                break;
            }

            count = Math.min(count, content.has(shop, inv, player, 1) / contentStack);
        }

        return count;
    }

    @Override
    public boolean canHold(@NotNull Shop shop, @NotNull Player player, int stack) {
        return canHold(shop, player.getInventory(), player, stack);
    }

    @Override
    public boolean canHold(@NotNull Shop shop, @NotNull Inventory inv, @Nullable Player player, int stack) {
        for (Map.Entry<String, Integer> entry : bundleContents.entrySet()) {
            String contentId = entry.getKey();
            int contentStack = entry.getValue() * stack;
            Product content = DailyShop.PRODUCT_FACTORY.getProduct(contentId);
            if (content != null && !content.canHold(shop, inv, player, contentStack)) {
                return false;
            }
        }
        return true;
    }

    @Override
    public boolean isProductItemCacheable() {
        return false;
    }

    @Override
    public boolean isMatch(@NotNull String shopId, ItemStack item, @Nullable Player player) {
        return false;
    }
}
