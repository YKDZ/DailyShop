package cn.encmys.ykdz.forest.dailyshop.product;

import cn.encmys.ykdz.forest.dailyshop.api.product.Product;
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

import java.util.List;

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
    private ItemStack productItem;
    private AbstractItem guiProductItem;

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
        buildGUIProductItem();
        buildProductItem();
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
    public AbstractItem getGUIItem() {
        return guiProductItem;
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

    public void buildGUIProductItem() {
        guiProductItem = new GUIProductItem(this);
    }

    @Override
    public void sellTo(Player player) {
        if (sellPriceProvider.getPrice() == -1d) {
            return;
        }

        if (BalanceUtils.removeBalance(player, sellPriceProvider.getPrice()).transactionSuccess()) {
            PlayerUtils.giveItem(player, getProductItem());
        }
    }

    @Override
    public void buyFrom(Player player) {
        if (buyPriceProvider.getPrice() == -1d) {
            return;
        }

        if (PlayerUtils.takeItem(player, getProductItem())) {
            BalanceUtils.addBalance(player, buyPriceProvider.getPrice());
        }
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
}
