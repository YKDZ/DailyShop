package cn.encmys.ykdz.forest.dailyshop.product;

import cn.encmys.ykdz.forest.dailyshop.DailyShop;
import cn.encmys.ykdz.forest.dailyshop.api.product.Product;
import cn.encmys.ykdz.forest.dailyshop.factory.ProductFactory;
import cn.encmys.ykdz.forest.dailyshop.price.PriceProvider;
import cn.encmys.ykdz.forest.dailyshop.util.ItemUtils;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class BundleProduct implements Product {
    private final String id;
    private final PriceProvider priceProvider;
    private final String rarity;
    private final Material material;
    private final int amount;
    private final String displayName;
    private final List<String> descLore;
    private final List<String> contents;

    public BundleProduct(
            String id,
            PriceProvider priceProvider,
            String rarity,
            Material material,
            int amount ,
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
    public ItemStack getDisplayedItem() {
        ItemStack displayedItem = new ItemStack(material, amount);
        ItemUtils.displayName(displayedItem, displayName);
        ItemUtils.lore(displayedItem, descLore);
        return displayedItem;
    }

    @Override
    public void deliver(Player player) {
        for(String id : contents) {
            DailyShop.getProductFactory().getProduct(id).deliver(player);
        }
    }
}
