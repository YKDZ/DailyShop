package cn.encmys.ykdz.forest.dailyshop.product;

import cn.encmys.ykdz.forest.dailyshop.DailyShop;
import cn.encmys.ykdz.forest.dailyshop.api.product.Product;
import cn.encmys.ykdz.forest.dailyshop.builder.ProductIconBuilder;
import cn.encmys.ykdz.forest.dailyshop.builder.ProductItemBuilder;
import cn.encmys.ykdz.forest.dailyshop.enums.ProductType;
import cn.encmys.ykdz.forest.dailyshop.hook.MMOItemsHook;
import cn.encmys.ykdz.forest.dailyshop.price.Price;
import cn.encmys.ykdz.forest.dailyshop.price.PricePair;
import cn.encmys.ykdz.forest.dailyshop.rarity.Rarity;
import cn.encmys.ykdz.forest.dailyshop.util.BalanceUtils;
import cn.encmys.ykdz.forest.dailyshop.util.PlayerUtils;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.Nullable;

public class MMOItemsProduct extends Product {
    private final String mmoitemsType;
    private final String mmoitemsId;

    public MMOItemsProduct(
            String id,
            Price buyPrice,
            Price sellPrice,
            Rarity rarity,
            ProductIconBuilder productIconBuilder,
            ProductItemBuilder productItemBuilder,
            boolean isCacheable,
            String mmoitemsType,
            String mmoitemsId) {
        super(id, buyPrice, sellPrice, rarity, productIconBuilder, productItemBuilder, isCacheable);
        this.mmoitemsType = mmoitemsType;
        this.mmoitemsId = mmoitemsId;
    }

    @Override
    public ProductType getType() {
        return ProductType.MMOITEMS;
    }

    @Override
    public boolean sellTo(@Nullable String shopId, Player player) {
        ItemStack item = null;
        if (isCacheable()) {
            if (!getProductItemCache().containsKey(shopId)) {
                item = getProductItemBuilder().build(player);
                getProductItemCache().put(shopId, item);
            }
        } else {
            item = MMOItemsHook.buildItem(player, getMMOItemsType(), getMMOItemsId());
        }

        if (!canSellTo(shopId, player)) {
            return false;
        }

        if (item == null) {
            return false;
        }

        BalanceUtils.removeBalance(player, DailyShop.getShopFactory().getShop(shopId).getBuyPrice(getId()));
        PlayerUtils.giveItem(player, item);

        return true;
    }

    @Override
    public boolean canSellTo(@Nullable String shopId, Player player) {
        return BalanceUtils.checkBalance(player) >= DailyShop.getShopFactory().getShop(shopId).getBuyPrice(getId());
    }

    @Override
    public boolean buyFrom(@Nullable String shopId, Player player) {
        if (!canBuyFrom(shopId, player)) {
            return false;
        }

        MMOItemsHook.takeItem(player, getMMOItemsType(), getMMOItemsId(), getProductItemBuilder().getAmount());

        BalanceUtils.addBalance(player, DailyShop.getShopFactory().getShop(shopId).getSellPrice(getId()));

        return true;
    }

    @Override
    public int buyAllFrom(@Nullable String shopId, Player player) {
        return 0;
    }

    @Override
    public boolean canBuyFrom(@Nullable String shopId, Player player) {
        return MMOItemsHook.hasItem(player, getMMOItemsType(), getMMOItemsId(), getProductItemBuilder().getAmount());
    }

    @Override
    public PricePair getNewPricePair(@Nullable String shopId) {
        return new PricePair(getBuyPrice().getNewPrice(), getSellPrice().getNewPrice());
    }

    public String getMMOItemsType() {
        return mmoitemsType;
    }

    public String getMMOItemsId() {
        return mmoitemsId;
    }
}
