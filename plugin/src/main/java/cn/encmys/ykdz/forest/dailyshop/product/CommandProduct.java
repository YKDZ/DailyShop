package cn.encmys.ykdz.forest.dailyshop.product;

import cn.encmys.ykdz.forest.dailyshop.api.item.decorator.BaseItemDecorator;
import cn.encmys.ykdz.forest.dailyshop.api.price.Price;
import cn.encmys.ykdz.forest.dailyshop.api.product.Product;
import cn.encmys.ykdz.forest.dailyshop.api.product.enums.ProductType;
import cn.encmys.ykdz.forest.dailyshop.api.product.stock.ProductStock;
import cn.encmys.ykdz.forest.dailyshop.api.rarity.Rarity;
import cn.encmys.ykdz.forest.dailyshop.api.shop.Shop;
import cn.encmys.ykdz.forest.dailyshop.api.utils.CommandUtils;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.stream.IntStream;

public class CommandProduct extends Product {
    private final List<String> buyCommands;
    private final List<String> sellCommands;

    public CommandProduct(
            String id,
            Price buyPrice,
            Price sellPrice,
            Rarity rarity,
            BaseItemDecorator iconBuilder,
            ProductStock productStock,
            List<String> buyCommands,
            List<String> sellCommands) {
        super(id, buyPrice, sellPrice, rarity, iconBuilder, null, productStock, false);
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
    public void give(@NotNull Shop shop, @NotNull Inventory inv, @Nullable Player player, int stack) {
        IntStream.range(0, stack).forEach(i -> CommandUtils.dispatchCommands(player, getBuyCommands()));
    }

    @Override
    public void take(@NotNull Shop shop, @NotNull Player player, int stack) {
        take(shop, player.getInventory(), player, stack);
    }

    @Override
    public void take(@NotNull Shop shop, @NotNull Iterable<ItemStack> inv, @Nullable Player player, int stack) {
        IntStream.range(0, stack).forEach(i -> CommandUtils.dispatchCommands(player, getSellCommands()));
    }

    // Player can not "have" a command
    @Override
    public int has(@NotNull Shop shop, @NotNull Player player, int stack) {
        return Integer.MAX_VALUE;
    }

    @Override
    public int has(@NotNull Shop shop, @NotNull Iterable<ItemStack> inv, @Nullable Player player, int stack) {
        return Integer.MAX_VALUE;
    }

    @Override
    public boolean canHold(@NotNull Shop shop, @NotNull Player player, int stack) {
        return true;
    }

    @Override
    public boolean canHold(@NotNull Shop shop, @NotNull Inventory inv, @Nullable Player player, int stack) {
        return true;
    }

    @Override
    public boolean isMatch(@NotNull String shopId, ItemStack item, @Nullable Player player) {
        return false;
    }

    public List<String> getBuyCommands() {
        return buyCommands;
    }

    public List<String> getSellCommands() {
        return sellCommands;
    }
}
