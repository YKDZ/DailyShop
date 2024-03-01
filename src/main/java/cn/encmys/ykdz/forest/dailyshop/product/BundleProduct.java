package cn.encmys.ykdz.forest.dailyshop.product;

import cn.encmys.ykdz.forest.dailyshop.api.product.Product;
import cn.encmys.ykdz.forest.dailyshop.factory.ProductFactory;
import cn.encmys.ykdz.forest.dailyshop.util.ItemUtils;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class BundleProduct implements Product {
    private final String id;
    private final Material material;
    private final int amount;
    private final String displayName;
    private final List<String> displayedLore;
    private final List<String> contents;

    public BundleProduct(
            String id,
            Material material,
            int amount ,
            @Nullable String displayName,
            @Nullable List<String> displayedLore,
            @Nullable List<String> contents) {
        this.id = id;
        this.material = material;
        this.amount = amount;
        this.displayName = displayName;
        this.displayedLore = displayedLore;
        this.contents = contents;
    }

    @Override
    public String getId() {
        return id;
    }

    @Override
    public ItemStack getDisplayedItem() {
        ItemStack displayedItem = new ItemStack(material, amount);
        ItemUtils.displayName(displayedItem, displayName);
        ItemUtils.lore(displayedItem, displayedLore);
        return displayedItem;
    }

    @Override
    public void deliver(Player player) {
        for(String id : contents) {
            ProductFactory.getProduct(id).deliver(player);
        }
    }
}
