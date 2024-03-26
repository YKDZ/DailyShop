package cn.encmys.ykdz.forest.dailyshop.product;

import cn.encmys.ykdz.forest.dailyshop.DailyShop;
import cn.encmys.ykdz.forest.dailyshop.api.product.Product;
import cn.encmys.ykdz.forest.dailyshop.builder.BaseItemDecorator;
import cn.encmys.ykdz.forest.dailyshop.price.Price;
import cn.encmys.ykdz.forest.dailyshop.price.PricePair;
import cn.encmys.ykdz.forest.dailyshop.product.enums.FailureReason;
import cn.encmys.ykdz.forest.dailyshop.product.enums.ProductType;
import cn.encmys.ykdz.forest.dailyshop.rarity.Rarity;
import cn.encmys.ykdz.forest.dailyshop.util.BalanceUtils;
import cn.encmys.ykdz.forest.dailyshop.util.CommandUtils;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;

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
        CommandUtils.dispatchCommands(player, getBuyCommands());
    }

    @Override
    public FailureReason buyFrom(@Nullable String shopId, Player player) {
        take(shopId, player, 1);
        return FailureReason.SUCCESS;
    }

    @Override
    public int buyAllFrom(@Nullable String shopId, Player player) {
        return takeAll(shopId, player);
    }

    @Override
    public FailureReason canBuyFrom(@Nullable String shopId, Player player) {
        double price = DailyShop.getShopFactory().getShop(shopId).getSellPrice(getId());
        if (price == -1d) {
            return FailureReason.DISABLE;
        }
        return FailureReason.SUCCESS;
    }

    @Override
    public void take(String shopId, Player player, int stack) {
        CommandUtils.dispatchCommands(player, getSellCommands());
    }

    @Override
    public int takeAll(String shopId, Player player) {
        CommandUtils.dispatchCommands(player, getSellCommands());
        return 1;
    }

    @Override
    public PricePair getNewPricePair(@Nullable String shopId) {
        return new PricePair(getBuyPrice().getNewPrice(), getSellPrice().getNewPrice());
    }

    public List<String> getBuyCommands() {
        return buyCommands;
    }

    public List<String> getSellCommands() {
        return sellCommands;
    }
}
