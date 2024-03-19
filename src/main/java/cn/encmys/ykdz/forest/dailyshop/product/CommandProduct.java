package cn.encmys.ykdz.forest.dailyshop.product;

import cn.encmys.ykdz.forest.dailyshop.DailyShop;
import cn.encmys.ykdz.forest.dailyshop.api.product.Product;
import cn.encmys.ykdz.forest.dailyshop.builder.ProductIconBuilder;
import cn.encmys.ykdz.forest.dailyshop.builder.ProductItemBuilder;
import cn.encmys.ykdz.forest.dailyshop.price.Price;
import cn.encmys.ykdz.forest.dailyshop.price.PricePair;
import cn.encmys.ykdz.forest.dailyshop.product.enums.ProductType;
import cn.encmys.ykdz.forest.dailyshop.rarity.Rarity;
import cn.encmys.ykdz.forest.dailyshop.shop.Shop;
import cn.encmys.ykdz.forest.dailyshop.util.BalanceUtils;
import cn.encmys.ykdz.forest.dailyshop.util.CommandUtils;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class CommandProduct extends Product {
    private final List<String> commands;

    public CommandProduct(
            String id,
            Price buyPrice,
            Price sellPrice,
            Rarity rarity,
            ProductIconBuilder productIconBuilder,
            ProductItemBuilder productItemBuilder,
            List<String> commands) {
        super(id, buyPrice, sellPrice, rarity, productIconBuilder, productItemBuilder, false);
        this.commands = commands;
    }

    @Override
    public ProductType getType() {
        return ProductType.COMMAND;
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
        Shop shop = DailyShop.getShopFactory().getShop(shopId);
        return BalanceUtils.checkBalance(player) >= shop.getBuyPrice(getId());
    }

    @Override
    public void give(@Nullable String shopId, @NotNull Player player) {
        for (int i = 0; i < getProductIconBuilder().getAmount(); i++) {
            CommandUtils.dispatchCommands(player, getCommands());
        }
    }

    @Override
    public boolean buyFrom(@Nullable String shopId, Player player) {
        return false;
    }

    @Override
    public int buyAllFrom(@Nullable String shopId, Player player) {
        return 0;
    }

    @Override
    public boolean canBuyFrom(@Nullable String shopId, Player player) {
        return false;
    }

    @Override
    public boolean take(Player player, int stack) {
        return false;
    }

    @Override
    public int takeAll(Player player) {
        return 0;
    }

    @Override
    public PricePair getNewPricePair(@Nullable String shopId) {
        return new PricePair(getBuyPrice().getNewPrice(), getSellPrice().getNewPrice());
    }

    public List<String> getCommands() {
        return commands;
    }
}
