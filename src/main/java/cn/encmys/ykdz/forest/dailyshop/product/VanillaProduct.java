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
import cn.encmys.ykdz.forest.dailyshop.util.PlayerUtils;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.Nullable;

public class VanillaProduct extends Product {
    public VanillaProduct(
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
        if (!getProductItemCache().containsKey(shopId) && isCacheable()) {
            getProductItemCache().put(shopId, getProductItemBuilder().build(player));
        }

        if (!canSellTo(shopId, player)) {
            return false;
        }

        BalanceUtils.removeBalance(player, DailyShop.getShopFactory().getShop(shopId).getBuyPrice(getId()));
        PlayerUtils.giveItem(player, getProductItemCache().get(shopId));

        return true;
    }

    @Override
    public boolean canSellTo(@Nullable String shopId, Player player) {
        return BalanceUtils.checkBalance(player) >= DailyShop.getShopFactory().getShop(shopId).getBuyPrice(getId());
    }

    @Override
    public boolean buyFrom(@Nullable String shopId, Player player) {
        if (!getProductItemCache().containsKey(shopId) && isCacheable()) {
            getProductItemCache().put(shopId, getProductItemBuilder().build(player));
        }

        if (!canBuyFrom(shopId, player)) {
            return false;
        }

        PlayerUtils.takeItem(player, getProductItemCache().get(shopId), getProductItemBuilder().getAmount());
        BalanceUtils.addBalance(player, DailyShop.getShopFactory().getShop(shopId).getSellPrice(getId()));

        return true;
    }

    @Override
    public int buyAllFrom(@Nullable String shopId, Player player) {
        if (!getProductItemCache().containsKey(shopId) && isCacheable()) {
            getProductItemCache().put(shopId, getProductItemBuilder().build(player));
        }

        if (!canBuyFrom(shopId, player)) {
            return 0;
        }

        int stack = PlayerUtils.takeAllItems(player, getProductItemCache().get(shopId));

        if (stack == 0) {
            return 0;
        }

        BalanceUtils.addBalance(player, DailyShop.getShopFactory().getShop(shopId).getSellPrice(getId()) * stack);

        return stack;
    }

    @Override
    public boolean canBuyFrom(@Nullable String shopId, Player player) {
        if (!getProductItemCache().containsKey(shopId) && isCacheable()) {
            getProductItemCache().put(shopId, getProductItemBuilder().build(player));
        }
        return PlayerUtils.hasItem(player, getProductItemCache().get(shopId));
    }

    @Override
    public ProductType getType() {
        return ProductType.VANILLA;
    }

    @Override
    public PricePair getNewPricePair(@Nullable String shopId) {
        return new PricePair(getBuyPrice().getNewPrice(), getSellPrice().getNewPrice());
    }
}
