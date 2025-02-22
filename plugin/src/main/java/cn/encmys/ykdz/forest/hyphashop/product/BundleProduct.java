package cn.encmys.ykdz.forest.hyphashop.product;

import cn.encmys.ykdz.forest.hyphascript.context.Context;
import cn.encmys.ykdz.forest.hyphashop.api.HyphaShop;
import cn.encmys.ykdz.forest.hyphashop.api.item.decorator.BaseItemDecorator;
import cn.encmys.ykdz.forest.hyphashop.api.price.Price;
import cn.encmys.ykdz.forest.hyphashop.api.product.Product;
import cn.encmys.ykdz.forest.hyphashop.api.product.enums.ProductType;
import cn.encmys.ykdz.forest.hyphashop.api.product.stock.ProductStock;
import cn.encmys.ykdz.forest.hyphashop.api.rarity.Rarity;
import cn.encmys.ykdz.forest.hyphashop.api.shop.Shop;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Unmodifiable;

import java.util.Collections;
import java.util.List;
import java.util.Map;

public class BundleProduct extends Product {
    private final Map<String, Integer> bundleContents;

    public BundleProduct(
            @NotNull String id,
            @NotNull Price buyPrice,
            @NotNull Price sellPrice,
            @NotNull Rarity rarity,
            @NotNull BaseItemDecorator iconBuilder,
            @NotNull ProductStock productStock,
            @NotNull List<String> listConditions,
            @NotNull Context scriptContext,
            @NotNull Map<String, Integer> bundleContents) {
        super(id, buyPrice, sellPrice, rarity, iconBuilder, null, productStock, listConditions, scriptContext, false);
        this.bundleContents = bundleContents;
    }

    @Override
    public ProductType getType() {
        return ProductType.BUNDLE;
    }

    public @NotNull @Unmodifiable Map<String, Integer> getBundleContents() {
        return Collections.unmodifiableMap(bundleContents);
    }

    @Override
    public void give(@NotNull Shop shop, @NotNull Player player, int stack) {
        give(shop, player.getInventory(), player, stack);
    }

    @Override
    public void give(@NotNull Shop shop, @NotNull Inventory inv, @NotNull Player player, int stack) {
        for (Map.Entry<String, Integer> entry : bundleContents.entrySet()) {
            String contentId = entry.getKey();
            Product content = HyphaShop.PRODUCT_FACTORY.getProduct(contentId);

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
    public void take(@NotNull Shop shop, @NotNull Iterable<ItemStack> inv, @NotNull Player player, int stack) {
        for (Map.Entry<String, Integer> entry : bundleContents.entrySet()) {
            String contentId = entry.getKey();
            Product content = HyphaShop.PRODUCT_FACTORY.getProduct(contentId);

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
    public int has(@NotNull Shop shop, @NotNull Iterable<ItemStack> inv, @NotNull Player player, int stack) {
        int count = Integer.MAX_VALUE;

        for (Map.Entry<String, Integer> entry : bundleContents.entrySet()) {
            String contentId = entry.getKey();
            Product content = HyphaShop.PRODUCT_FACTORY.getProduct(contentId);

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
    public boolean canHold(@NotNull Shop shop, @NotNull Inventory inv, @NotNull Player player, int stack) {
        for (Map.Entry<String, Integer> entry : bundleContents.entrySet()) {
            String contentId = entry.getKey();
            int contentStack = entry.getValue() * stack;
            Product content = HyphaShop.PRODUCT_FACTORY.getProduct(contentId);
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
    public boolean isMatch(@NotNull String shopId, @NotNull ItemStack item, @NotNull Player player) {
        return false;
    }
}
