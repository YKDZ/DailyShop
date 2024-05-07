package cn.encmys.ykdz.forest.dailyshop.product;

import cn.encmys.ykdz.forest.dailyshop.api.DailyShop;
import cn.encmys.ykdz.forest.dailyshop.api.builder.BaseItemDecorator;
import cn.encmys.ykdz.forest.dailyshop.api.item.BaseItem;
import cn.encmys.ykdz.forest.dailyshop.api.item.enums.BaseItemType;
import cn.encmys.ykdz.forest.dailyshop.api.price.Price;
import cn.encmys.ykdz.forest.dailyshop.api.product.Product;
import cn.encmys.ykdz.forest.dailyshop.api.product.enums.ProductType;
import cn.encmys.ykdz.forest.dailyshop.api.rarity.Rarity;
import cn.encmys.ykdz.forest.dailyshop.api.shop.Shop;
import cn.encmys.ykdz.forest.dailyshop.util.PlayerUtils;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.stream.IntStream;

public class ItemProduct extends Product {
    public ItemProduct(
            String id,
            Price buyPrice,
            Price sellPrice,
            Rarity rarity,
            BaseItemDecorator iconBuilder,
            BaseItemDecorator productItemBuilder,
            boolean isCacheable) {
        super(id, buyPrice, sellPrice, rarity, iconBuilder, productItemBuilder, isCacheable);
    }

    @Override
    public ProductType getType() {
        return ProductType.ITEM;
    }

    @Override
    public void give(@NotNull Shop shop, @NotNull Player player, int stack) {
        ItemStack item = shop.getCachedProductItem(this);
        // Check whether player has enough inventory space at first
        IntStream.range(0, stack).forEach(i -> player.getInventory().addItem(item));
    }

    @Override
    public void take(@NotNull Shop shop, @NotNull Player player, int stack) {
        int needed = getProductItemBuilder().getAmount() * stack;
        if (has(shop, player, 1) < stack) {
            return;
        }

        for (ItemStack check : player.getInventory().getContents()) {
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
    public int has(@NotNull Shop shop, @NotNull Player player, int stack) {
        int total = 0;
        int stackedAmount = getProductItemBuilder().getAmount() * stack;
        for (ItemStack check : player.getInventory().getContents()) {
            if (check != null && isMatch(shop.getId(), check, player)) {
                total += check.getAmount();
            }
        }
        return total / stackedAmount;
    }

    @Override
    public boolean canHold(@NotNull Shop shop, @NotNull Player player, int stack) {
        return PlayerUtils.hasInventorySpace(player, shop.getCachedProductItemOrCreateOne(this, player), stack);
    }

    public boolean isMatch(String shopId, ItemStack item, @Nullable Player player) {
        Shop shop = DailyShop.SHOP_FACTORY.getShop(shopId);
        BaseItem baseItem = getProductItemBuilder().getItem();
        if (baseItem.getItemType() != BaseItemType.VANILLA) {
            return baseItem.isSimilar(item);
        } else {
            ItemStack target = shop.getCachedProductItemOrCreateOne(this, player);
            return baseItem.isSimilar(item) && target.isSimilar(item);
        }
    }
}
