package cn.encmys.ykdz.forest.dailyshop.product;

import cn.encmys.ykdz.forest.dailyshop.DailyShop;
import cn.encmys.ykdz.forest.dailyshop.api.product.Product;
import cn.encmys.ykdz.forest.dailyshop.builder.ProductIconBuilder;
import cn.encmys.ykdz.forest.dailyshop.builder.ProductItemBuilder;
import cn.encmys.ykdz.forest.dailyshop.enums.ProductType;
import cn.encmys.ykdz.forest.dailyshop.price.Price;
import cn.encmys.ykdz.forest.dailyshop.price.PricePair;
import cn.encmys.ykdz.forest.dailyshop.rarity.Rarity;
import cn.encmys.ykdz.forest.dailyshop.util.BalanceUtils;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.HashMap;
import java.util.Map;

public class ItemProduct extends Product {
    public ItemProduct(
            String id,
            Price buyPrice,
            Price sellPrice,
            Rarity rarity,
            ProductIconBuilder productIconBuilder,
            ProductItemBuilder productItemBuilder,
            boolean isCacheable) {
        super(id, buyPrice, sellPrice, rarity, productIconBuilder, productItemBuilder, isCacheable);
    }

    @Override
    public boolean sellTo(@Nullable String shopId, Player player) {
        if (!canSellTo(shopId, player)) {
            return false;
        }

        BalanceUtils.removeBalance(player, DailyShop.getShopFactory().getShop(shopId).getBuyPrice(getId()));
        give(shopId, player);

        return true;
    }

    @Override
    public boolean canSellTo(@Nullable String shopId, Player player) {
        return BalanceUtils.checkBalance(player) >= DailyShop.getShopFactory().getShop(shopId).getBuyPrice(getId());
    }

    @Override
    public void give(@Nullable String shopId, @NotNull Player player) {
        if (!isCached(shopId) && isCacheable()) {
            cacheProductItem(shopId, player);
        }

        HashMap<Integer, ItemStack> left = player.getInventory().addItem(getCachedItem(shopId, player));
        if (!left.isEmpty()) {
            for (Map.Entry<Integer, ItemStack> entry : left.entrySet()) {
                player.getWorld().dropItem(player.getLocation().add(0, 0.5, 0), entry.getValue());
            }
        }
    }

    @Override
    public boolean buyFrom(@Nullable String shopId, Player player) {
        if (!canBuyFrom(shopId, player)) {
            return false;
        }

        if (!take(player, 1)) {
            return false;
        }

        BalanceUtils.addBalance(player, DailyShop.getShopFactory().getShop(shopId).getSellPrice(getId()));

        return true;
    }

    @Override
    public int buyAllFrom(@Nullable String shopId, Player player) {
        if (!canBuyFrom(shopId, player)) {
            return 0;
        }

        int stack = takeAll(player);

        if (stack == 0) {
            return 0;
        }

        BalanceUtils.addBalance(player, DailyShop.getShopFactory().getShop(shopId).getSellPrice(getId()) * stack);

        return stack;
    }

    @Override
    public boolean canBuyFrom(@Nullable String shopId, Player player) {
        int needed = getProductItemBuilder().getAmount();
        for (ItemStack check : player.getInventory()) {
            if (check != null && getProductItemBuilder().getItem().isSimilar(check)) {
                int has = check.getAmount();
                if (needed <= has) {
                    return true;
                } else {
                    needed -= has;
                }
            }
        }
        return false;
    }

    @Override
    public boolean take(Player player, int stack) {
        int needed = getProductItemBuilder().getAmount() * stack;
        if (getAmount(player.getInventory()) < needed) {
            return false;
        }

        for (ItemStack check : player.getInventory()) {
            if (check != null && getProductItemBuilder().getItem().isSimilar(check)) {
                int has = check.getAmount();
                if (needed <= has) {
                    check.setAmount(has - needed);
                } else {
                    check.setAmount(0);
                    needed -= has;
                }
            }
        }
        return true;
    }

    @Override
    public int takeAll(Player player) {
        int stack = getAmount(player.getInventory()) / getProductItemBuilder().getAmount();
        take(player, stack);
        return stack;
    }

    @Override
    public ProductType getType() {
        return ProductType.VANILLA;
    }

    @Override
    public PricePair getNewPricePair(@Nullable String shopId) {
        return new PricePair(getBuyPrice().getNewPrice(), getSellPrice().getNewPrice());
    }

    public int getAmount(Inventory inventory) {
        int total = 0;
        for (ItemStack check : inventory) {
            if (check != null && getProductItemBuilder().getItem().isSimilar(check)) {
                total += check.getAmount();
            }
        }
        return total;
    }

    public ItemStack getCachedItem(String shopId, Player player) {
        if (!isCached(shopId)) {
            if (isCacheable()) {
                return cacheProductItem(shopId, player);
            } else {
                return getProductItemBuilder().build(player);
            }
        } else {
            return getProductItemCache().get(shopId);
        }
    }
}
