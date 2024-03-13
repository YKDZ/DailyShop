package cn.encmys.ykdz.forest.dailyshop.product;

import cn.encmys.ykdz.forest.dailyshop.DailyShop;
import cn.encmys.ykdz.forest.dailyshop.api.product.Product;
import cn.encmys.ykdz.forest.dailyshop.builder.ProductIconBuilder;
import cn.encmys.ykdz.forest.dailyshop.builder.ProductItemBuilder;
import cn.encmys.ykdz.forest.dailyshop.enums.ProductType;
import cn.encmys.ykdz.forest.dailyshop.price.Price;
import cn.encmys.ykdz.forest.dailyshop.rarity.Rarity;
import cn.encmys.ykdz.forest.dailyshop.shop.Shop;
import cn.encmys.ykdz.forest.dailyshop.util.BalanceUtils;
import me.clip.placeholderapi.PlaceholderAPI;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
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
    public List<String> getBundleContents() {
        return new ArrayList<>();
    }

    @Override
    public boolean sellTo(@Nullable String shopId, Player player) {
        if (!canSellTo(shopId, player)) {
            return false;
        }
        Shop shop = DailyShop.getShopFactory().getShop(shopId);

        BalanceUtils.removeBalance(player, shop.getBuyPrice(getId()));

        for (int i = 0; i < getProductItemBuilder().getAmount(); i++) {
            for (String command : commands) {
                Bukkit.getServer().dispatchCommand(Bukkit.getConsoleSender(), PlaceholderAPI.setPlaceholders(player, command));
            }
        }
        return true;
    }

    @Override
    public boolean canSellTo(@Nullable String shopId, Player player) {
        Shop shop = DailyShop.getShopFactory().getShop(shopId);
        return BalanceUtils.checkBalance(player) >= shop.getBuyPrice(getId());
    }

    @Override
    public boolean buyFrom(@Nullable String shopId, Player player) {
        return false;
    }

    @Override
    public boolean buyAllFrom(@Nullable String shopId, Player player) {
        return false;
    }

    @Override
    public boolean canBuyFrom(@Nullable String shopId, Player player) {
        return false;
    }
}
