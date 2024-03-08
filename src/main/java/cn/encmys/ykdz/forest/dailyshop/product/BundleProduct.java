package cn.encmys.ykdz.forest.dailyshop.product;

import cn.encmys.ykdz.forest.dailyshop.DailyShop;
import cn.encmys.ykdz.forest.dailyshop.api.product.Product;
import cn.encmys.ykdz.forest.dailyshop.item.GUIProductItem;
import cn.encmys.ykdz.forest.dailyshop.price.PriceProvider;
import cn.encmys.ykdz.forest.dailyshop.rarity.Rarity;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.Nullable;
import xyz.xenondevs.invui.item.impl.AbstractItem;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class BundleProduct implements Product {
    private final String id;
    private final PriceProvider buyPriceProvider;
    private final PriceProvider sellPriceProvider;
    private final Rarity rarity;
    private final Material material;
    private final int amount;
    private final String displayName;
    private final List<String> descLore;
    private final List<String> contents;
    private final Map<String, GUIProductItem> guiProductItems = new HashMap<>();

    public BundleProduct(
            String id,
            PriceProvider buyPriceProvider,
            PriceProvider sellPriceProvider,
            Rarity rarity,
            Material material,
            int amount,
            @Nullable String displayName,
            @Nullable List<String> descLore,
            @Nullable List<String> contents) {
        this.id = id;
        this.buyPriceProvider = buyPriceProvider;
        this.sellPriceProvider = sellPriceProvider;
        this.rarity = rarity;
        this.material = material;
        this.amount = amount;
        this.displayName = displayName;
        this.descLore = descLore;
        this.contents = contents;
    }

    @Override
    public String getId() {
        return id;
    }

    @Override
    public String getDisplayName() {
        return displayName;
    }

    @Override
    public Material getMaterial() {
        return material;
    }

    @Override
    public List<String> getDescLore() {
        return descLore;
    }

    @Override
    public int getAmount() {
        return amount;
    }

    @Override
    public GUIProductItem buildGUIProductItem(String shopId) {
        guiProductItems.put(shopId, new GUIProductItem(shopId, this));
        return guiProductItems.get(shopId);
    }

    @Override
    public AbstractItem getGUIItem(String shopId) {
        return guiProductItems.get(shopId) == null ?
                buildGUIProductItem(shopId) :
                guiProductItems.get(shopId);
    }

    @Override
    public void sellTo(@Nullable String shopId, Player player) {
        for (String id : contents) {
            DailyShop.getProductFactory().getProduct(id).sellTo(id, player);
        }
    }

    @Override
    public void buyFrom(@Nullable String shopId, Player player) {
        if(canBuyFrom(shopId, player)) {
            return;
        }

        for (String id : contents) {
            DailyShop.getProductFactory().getProduct(id).buyFrom(shopId, player);
        }
    }

    @Override
    public boolean canBuyFrom(@Nullable String shopId, Player player) {
        for (String id : contents) {
            if(!DailyShop.getProductFactory().getProduct(id).canBuyFrom(shopId, player)) {
                return false;
            }
        }
        return true;
    }

    @Override
    public Rarity getRarity() {
        return rarity;
    }

    @Override
    public PriceProvider getBuyPriceProvider() {
        return buyPriceProvider;
    }

    @Override
    public PriceProvider getSellPriceProvider() {
        return sellPriceProvider;
    }

    @Override
    public void updatePrice(String shopId) {
        buyPriceProvider.update(shopId);
        sellPriceProvider.update(shopId);
    }
}
