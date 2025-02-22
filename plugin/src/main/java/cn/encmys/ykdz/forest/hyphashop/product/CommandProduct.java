package cn.encmys.ykdz.forest.hyphashop.product;

import cn.encmys.ykdz.forest.hyphascript.context.Context;
import cn.encmys.ykdz.forest.hyphashop.api.item.decorator.BaseItemDecorator;
import cn.encmys.ykdz.forest.hyphashop.api.price.Price;
import cn.encmys.ykdz.forest.hyphashop.api.product.Product;
import cn.encmys.ykdz.forest.hyphashop.api.product.enums.ProductType;
import cn.encmys.ykdz.forest.hyphashop.api.product.stock.ProductStock;
import cn.encmys.ykdz.forest.hyphashop.api.rarity.Rarity;
import cn.encmys.ykdz.forest.hyphashop.api.shop.Shop;
import cn.encmys.ykdz.forest.hyphashop.utils.CommandUtils;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.stream.IntStream;

public class CommandProduct extends Product {
    private final List<String> buyCommands;
    private final List<String> sellCommands;

    public CommandProduct(
            @NotNull String id,
            @NotNull Price buyPrice,
            @NotNull Price sellPrice,
            @NotNull Rarity rarity,
            @NotNull BaseItemDecorator iconBuilder,
            @NotNull ProductStock productStock,
            @NotNull List<String> listConditions,
            @NotNull Context scriptContext,
            @NotNull List<String> buyCommands,
            @NotNull List<String> sellCommands) {
        super(id, buyPrice, sellPrice, rarity, iconBuilder, null, productStock, listConditions, scriptContext, false);
        this.buyCommands = buyCommands;
        this.sellCommands = sellCommands;
    }

    @Override
    public ProductType getType() {
        return ProductType.COMMAND;
    }

    @Override
    public void give(@NotNull Shop shop, @NotNull Player player, int stack) {
        give(shop, player.getInventory(), player, stack);
    }

    @Override
    public void give(@NotNull Shop shop, @NotNull Inventory inv, @NotNull Player player, int stack) {
        IntStream.range(0, stack).forEach(i -> CommandUtils.dispatchCommands(player, getBuyCommands()));
    }

    @Override
    public void take(@NotNull Shop shop, @NotNull Player player, int stack) {
        take(shop, player.getInventory(), player, stack);
    }

    @Override
    public void take(@NotNull Shop shop, @NotNull Iterable<ItemStack> inv, @NotNull Player player, int stack) {
        IntStream.range(0, stack).forEach(i -> CommandUtils.dispatchCommands(player, getSellCommands()));
    }

    @Override
    public int has(@NotNull Shop shop, @NotNull Player player, int stack) {
        return Integer.MAX_VALUE;
    }

    @Override
    public int has(@NotNull Shop shop, @NotNull Iterable<ItemStack> inv, @NotNull Player player, int stack) {
        return Integer.MAX_VALUE;
    }

    @Override
    public boolean canHold(@NotNull Shop shop, @NotNull Player player, int stack) {
        return true;
    }

    @Override
    public boolean canHold(@NotNull Shop shop, @NotNull Inventory inv, @NotNull Player player, int stack) {
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

    public List<String> getBuyCommands() {
        return buyCommands;
    }

    public List<String> getSellCommands() {
        return sellCommands;
    }
}
