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

import java.util.List;

public class BundleProduct implements Product {
    private final String id;
    private final PriceProvider priceProvider;
    private final Rarity rarity;
    private final Material material;
    private final int amount;
    private final String displayName;
    private final List<String> descLore;
    private final List<String> contents;
    private AbstractItem guiProductItem;

    public BundleProduct(
            String id,
            PriceProvider priceProvider,
            Rarity rarity,
            Material material,
            int amount,
            @Nullable String displayName,
            @Nullable List<String> descLore,
            @Nullable List<String> contents) {
        this.id = id;
        this.priceProvider = priceProvider;
        this.rarity = rarity;
        this.material = material;
        this.amount = amount;
        this.displayName = displayName;
        this.descLore = descLore;
        this.contents = contents;
        buildGUIProductItem();
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
        return new GUIProductItem(this);
    }

    public void buildGUIProductItem() {
        guiProductItem = new GUIProductItem(this);
    }

    @Override
    public void sellTo(Player player) {
        for (String id : contents) {
            DailyShop.getProductFactory().getProduct(id).sellTo(player);
        }
    }

    @Override
    public void buyFrom(Player player) {

    }

    @Override
    public Rarity getRarity() {
        return rarity;
    }
}
