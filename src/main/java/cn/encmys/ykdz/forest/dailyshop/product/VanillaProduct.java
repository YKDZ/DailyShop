package cn.encmys.ykdz.forest.dailyshop.product;

import cn.encmys.ykdz.forest.dailyshop.DailyShop;
import cn.encmys.ykdz.forest.dailyshop.api.product.Product;
import cn.encmys.ykdz.forest.dailyshop.builder.ProductIconBuilder;
import cn.encmys.ykdz.forest.dailyshop.builder.ProductItemBuilder;
import cn.encmys.ykdz.forest.dailyshop.enums.ProductType;
import cn.encmys.ykdz.forest.dailyshop.price.Price;
import cn.encmys.ykdz.forest.dailyshop.rarity.Rarity;
import cn.encmys.ykdz.forest.dailyshop.util.BalanceUtils;
import cn.encmys.ykdz.forest.dailyshop.util.PlayerUtils;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;

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

        if (PlayerUtils.takeItem(player, getProductItemCache().get(shopId))) {
            BalanceUtils.addBalance(player, DailyShop.getShopFactory().getShop(shopId).getSellPrice(getId()));
            return true;
        }

        return false;
    }

    @Override
    public boolean buyAllFrom(@Nullable String shopId, Player player) {
        return false;
    }

    @Override
    public boolean canBuyFrom(@Nullable String shopId, Player player) {
        ItemStack productItem = getProductItemBuilder().build(player);
        return PlayerUtils.hasItem(player, productItem);
    }

    @Override
    public ProductType getType() {
        return ProductType.VANILLA;
    }

    @Override
    public List<String> getBundleContents() {
        return new ArrayList<>();
    }
}
