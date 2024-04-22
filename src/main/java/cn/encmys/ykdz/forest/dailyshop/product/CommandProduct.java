package cn.encmys.ykdz.forest.dailyshop.product;

import cn.encmys.ykdz.forest.dailyshop.api.product.Product;
import cn.encmys.ykdz.forest.dailyshop.builder.BaseItemDecorator;
import cn.encmys.ykdz.forest.dailyshop.price.Price;
import cn.encmys.ykdz.forest.dailyshop.product.enums.ProductType;
import cn.encmys.ykdz.forest.dailyshop.rarity.Rarity;
import cn.encmys.ykdz.forest.dailyshop.shop.Shop;
import cn.encmys.ykdz.forest.dailyshop.util.CommandUtils;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

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
            List<String> buyCommands,
            List<String> sellCommands) {
        super(id, buyPrice, sellPrice, rarity, iconBuilder, null, false);
        this.buyCommands = buyCommands;
        this.sellCommands = sellCommands;
    }

    @Override
    public ProductType getType() {
        return ProductType.COMMAND;
    }

    @Override
    public void give(@NotNull Shop shop, @NotNull Player player, int stack) {
        IntStream.range(0, stack).forEach(i -> CommandUtils.dispatchCommands(player, getBuyCommands()));
    }

    @Override
    public void take(@NotNull Shop shop, @NotNull Player player, int stack) {
        IntStream.range(0, stack).forEach(i -> CommandUtils.dispatchCommands(player, getSellCommands()));
    }

    // Player can not "have" a command
    @Override
    public int has(@NotNull Shop shop, @NotNull Player player, int stack) {
        return Integer.MAX_VALUE;
    }

    @Override
    public boolean canHold(@NotNull Shop shop, @NotNull Player player, int stack) {
        return true;
    }

    public List<String> getBuyCommands() {
        return buyCommands;
    }

    public List<String> getSellCommands() {
        return sellCommands;
    }
}
