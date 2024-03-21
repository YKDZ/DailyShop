package cn.encmys.ykdz.forest.dailyshop.product;

import cn.encmys.ykdz.forest.dailyshop.DailyShop;
import cn.encmys.ykdz.forest.dailyshop.api.item.ProductItem;
import cn.encmys.ykdz.forest.dailyshop.api.product.Product;
import cn.encmys.ykdz.forest.dailyshop.builder.ProductIconBuilder;
import cn.encmys.ykdz.forest.dailyshop.builder.ProductItemBuilder;
import cn.encmys.ykdz.forest.dailyshop.price.Price;
import cn.encmys.ykdz.forest.dailyshop.price.PricePair;
import cn.encmys.ykdz.forest.dailyshop.product.enums.FailureReason;
import cn.encmys.ykdz.forest.dailyshop.product.enums.ProductType;
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
    public FailureReason sellTo(@Nullable String shopId, Player player) {
        FailureReason failure = canSellTo(shopId, player);
        if (failure != FailureReason.SUCCESS) {
            return failure;
        }

        BalanceUtils.removeBalance(player, DailyShop.getShopFactory().getShop(shopId).getBuyPrice(getId()));
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
    public FailureReason buyFrom(@Nullable String shopId, Player player) {
        FailureReason failure = canBuyFrom(shopId, player);
        if (failure != FailureReason.SUCCESS) {
            return failure;
        }

        take(player, 1);
        BalanceUtils.addBalance(player, DailyShop.getShopFactory().getShop(shopId).getSellPrice(getId()));

        return FailureReason.SUCCESS;
    }

    @Override
    public int buyAllFrom(@Nullable String shopId, Player player) {
        FailureReason failure = canBuyFrom(shopId, player);
        if (failure != FailureReason.SUCCESS) {
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
    public FailureReason canBuyFrom(@Nullable String shopId, Player player) {
        if (DailyShop.getShopFactory().getShop(shopId).getSellPrice(getId()) == -1d) {
            return FailureReason.DISABLE;
        }

        int needed = getProductItemBuilder().getAmount();
        ProductItem item = getProductItemBuilder().getItem();
        for (ItemStack check : player.getInventory()) {
            if (check != null && needed > 0 && item.isSimilar(check)) {
                int has = check.getAmount();
                if (needed <= has) {
                    return FailureReason.SUCCESS;
                } else {
                    needed -= has;
                }
            }
        }
        return FailureReason.NOT_ENOUGH;
    }

    @Override
    public void take(Player player, int stack) {
        int needed = getProductItemBuilder().getAmount() * stack;
        if (getAmount(player.getInventory()) < needed) {
            return;
        }

        for (ItemStack check : player.getInventory()) {
            if (check != null && needed > 0 && getProductItemBuilder().getItem().isSimilar(check)) {
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
