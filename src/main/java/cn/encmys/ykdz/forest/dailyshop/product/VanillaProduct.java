package cn.encmys.ykdz.forest.dailyshop.product;

import cn.encmys.ykdz.forest.dailyshop.DailyShop;
import cn.encmys.ykdz.forest.dailyshop.api.product.Product;
import cn.encmys.ykdz.forest.dailyshop.config.Config;
import cn.encmys.ykdz.forest.dailyshop.enums.ProductType;
import cn.encmys.ykdz.forest.dailyshop.item.GUIProductItem;
import cn.encmys.ykdz.forest.dailyshop.price.PriceProvider;
import cn.encmys.ykdz.forest.dailyshop.rarity.Rarity;
import cn.encmys.ykdz.forest.dailyshop.util.BalanceUtils;
import cn.encmys.ykdz.forest.dailyshop.util.ItemUtils;
import cn.encmys.ykdz.forest.dailyshop.util.PlayerUtils;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.Nullable;
import xyz.xenondevs.invui.item.impl.AbstractItem;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class VanillaProduct implements Product {
    private final String id;
    private final PriceProvider buyPriceProvider;
    private final PriceProvider sellPriceProvider;
    private final Rarity rarity;
    private final Material material;
    private final int amount;
    private final String displayName;
    private final List<String> descLore;
    private final List<String> productLore;
    private final Map<String, GUIProductItem> guiProductItems = new HashMap<>();
    private ItemStack productItem;

    public VanillaProduct(
            String id,
            PriceProvider buyPriceProvider,
            PriceProvider sellPriceProvider,
            Rarity rarity,
            Material material,
            int amount,
            @Nullable String displayName,
            @Nullable List<String> descLore,
            @Nullable List<String> productLore) {
        this.id = id;
        this.buyPriceProvider = buyPriceProvider;
        this.sellPriceProvider = sellPriceProvider;
        this.rarity = rarity;
        this.material = material;
        this.amount = amount;
        this.displayName = displayName;
        this.descLore = descLore;
        this.productLore = productLore;
        buildProductItem();
    }

    @Override
    public String getId() {
        return id;
    }

    @Override
    public String getDisplayName() {
        return displayName == null && DailyShop.getItemsLangAPI() != null ? DailyShop.getItemsLangAPI().translate(material, Config.language) : displayName;
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
    public AbstractItem getGUIItem(String shopId) {
        return guiProductItems.get(shopId) == null ?
                buildGUIProductItem(shopId) :
                guiProductItems.get(shopId);
    }

    public ItemStack getProductItem() {
        return productItem;
    }

    public ItemStack buildProductItem() {
        productItem = new ItemStack(material, amount);
        ItemUtils.displayName(productItem, displayName);
        ItemUtils.lore(productItem, productLore);
        return productItem;
    }

    @Override
    public GUIProductItem buildGUIProductItem(String shopId) {
        guiProductItems.put(shopId, new GUIProductItem(shopId, this));
        return guiProductItems.get(shopId);
    }

    @Override
    public void sellTo(@Nullable String shopId, Player player) {
        if (BalanceUtils.removeBalance(player, sellPriceProvider.getPrice(shopId)).transactionSuccess()) {
            PlayerUtils.giveItem(player, getProductItem());
        }
    }

    @Override
    public void buyFrom(@Nullable String shopId, Player player) {
        if (buyPriceProvider.getPrice(shopId) == -1d) {
            return;
        }

        if (PlayerUtils.takeItem(player, getProductItem())) {
            BalanceUtils.addBalance(player, buyPriceProvider.getPrice(shopId));
        }
    }

    @Override
    public void buyAllFrom(@Nullable String shopId, Player player) {

    }

    @Override
    public boolean canBuyFrom(@Nullable String shopId, Player player) {
        return PlayerUtils.hasItem(player, getProductItem());
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

    @Override
    public ProductType getType() {
        return ProductType.VANILLA;
    }

    @Override
    public List<String> getBundleContents() {
        return new ArrayList<>();
    }
}
