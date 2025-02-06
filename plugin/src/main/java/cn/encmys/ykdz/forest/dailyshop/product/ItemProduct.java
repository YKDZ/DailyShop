package cn.encmys.ykdz.forest.dailyshop.product;

import cn.encmys.ykdz.forest.dailyshop.api.DailyShop;
import cn.encmys.ykdz.forest.dailyshop.api.item.BaseItem;
import cn.encmys.ykdz.forest.dailyshop.api.item.decorator.BaseItemDecorator;
import cn.encmys.ykdz.forest.dailyshop.api.item.enums.BaseItemType;
import cn.encmys.ykdz.forest.dailyshop.api.price.Price;
import cn.encmys.ykdz.forest.dailyshop.api.product.Product;
import cn.encmys.ykdz.forest.dailyshop.api.product.enums.ProductType;
import cn.encmys.ykdz.forest.dailyshop.api.product.stock.ProductStock;
import cn.encmys.ykdz.forest.dailyshop.api.rarity.Rarity;
import cn.encmys.ykdz.forest.dailyshop.api.shop.Shop;
import cn.encmys.ykdz.forest.dailyshop.api.utils.PlayerUtils;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.stream.IntStream;

public class ItemProduct extends Product {
    public ItemProduct(
            String id,
            Price buyPrice,
            Price sellPrice,
            Rarity rarity,
            BaseItemDecorator iconBuilder,
            BaseItemDecorator productItemBuilder,
            ProductStock productStock,
            List<String> listConditions,
            boolean isCacheable) {
        super(id, buyPrice, sellPrice, rarity, iconBuilder, productItemBuilder, productStock, listConditions, isCacheable);
    }

    @Override
    public ProductType getType() {
        return ProductType.ITEM;
    }

    @Override
    public void give(@NotNull Shop shop, Player player, int stack) {
        give(shop, player.getInventory(), player, stack);
    }

    @Override
    public void give(@NotNull Shop shop, @NotNull Inventory inv, Player player, int stack) {
        ItemStack item = shop.getCachedProductItemOrBuildOne(this, player);
        IntStream.range(0, stack).forEach(i -> inv.addItem(item));
    }

    @Override
    public void take(@NotNull Shop shop, Player player, int stack) {
        take(shop, player.getInventory(), player, stack);
    }

    @Override
    public void take(@NotNull Shop shop, @NotNull Iterable<ItemStack> inv, Player player, int stack) {
        BaseItemDecorator decorator = getProductItemDecorator();
        if (decorator == null) {
            return;
        }
        int needed = shop.getShopCounter().getAmount(getId()) * stack;
        if (has(shop, inv, player, 1) < stack) {
            return;
        }

        for (ItemStack check : inv) {
            if (check != null && needed > 0 && isMatch(shop.getId(), check, player)) {
                int has = check.getAmount();
                if (needed <= has) {
                    check.setAmount(has - needed);
                    needed = 0;
                } else {
                    check.setAmount(0);
                    needed -= has;
                }
            }
        }
    }

    @Override
    public int has(@NotNull Shop shop, Player player, int stack) {
        return has(shop, player.getInventory(), player, stack);
    }

    @Override
    public int has(@NotNull Shop shop, @NotNull Iterable<ItemStack> inv, Player player, int stack) {
        BaseItemDecorator decorator = getProductItemDecorator();
        if (decorator == null) {
            return 0;
        }
        int total = 0;
        int stackedAmount = shop.getShopCounter().getAmount(getId()) * stack;
        for (ItemStack check : inv) {
            if (check != null && isMatch(shop.getId(), check, player)) {
                total += check.getAmount();
            }
        }
        return total / stackedAmount;
    }

    @Override
    public boolean canHold(@NotNull Shop shop, Player player, int stack) {
        return canHold(shop, player.getInventory(), player, stack);
    }

    @Override
    public boolean canHold(@NotNull Shop shop, @NotNull Inventory inv, Player player, int stack) {
        return PlayerUtils.hasInventorySpace(inv, shop.getCachedProductItemOrBuildOne(this, player), stack);
    }

    @Override
    public boolean isProductItemCacheable() {
        return isCacheable && getType() == ProductType.ITEM;
    }

    @Override
    public boolean isMatch(@NotNull String shopId, ItemStack item, Player player) {
        BaseItemDecorator decorator = getProductItemDecorator();
        Shop shop = DailyShop.SHOP_FACTORY.getShop(shopId);
        if (decorator == null || shop == null) {
            return false;
        }
        BaseItem baseItem = decorator.getBaseItem();
        if (baseItem.getItemType() != BaseItemType.VANILLA) {
            return baseItem.isSimilar(item);
        } else {
            ItemStack target = shop.getCachedProductItemOrBuildOne(this, player);
            return baseItem.isSimilar(item) && target.isSimilar(item);
        }
    }
}
