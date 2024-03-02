package cn.encmys.ykdz.forest.dailyshop.product;

import cn.encmys.ykdz.forest.dailyshop.api.product.Product;
import cn.encmys.ykdz.forest.dailyshop.price.PriceProvider;
import cn.encmys.ykdz.forest.dailyshop.util.ItemUtils;
import cn.encmys.ykdz.forest.dailyshop.util.PlayerUtils;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class VanillaProduct implements Product {
    private final String id;
    private final PriceProvider priceProvider;
    private final String rarity;
    private final Material material;
    private final int amount;
    private final String displayName;
    private final List<String> descLore;
    private final List<String> productLore;

    public VanillaProduct(
            String id,
            PriceProvider priceProvider,
            String rarity,
            Material material,
            int amount ,
            @Nullable String displayName,
            @Nullable List<String> descLore,
            @Nullable List<String> productLore) {
        this.id = id;
        this.priceProvider = priceProvider;
        this.rarity = rarity;
        this.material = material;
        this.amount = amount;
        this.displayName = displayName;
        this.descLore = descLore;
        this.productLore = productLore;
    }

    @Override
    public String getId() {
        return id;
    }

    @Override
    public ItemStack getDisplayedItem() {
        ItemStack displayedItem = new ItemStack(material, amount);
        ItemUtils.lore(displayedItem, descLore);
        return displayedItem;
    }

    @Override
    public void deliver(Player player) {
        ItemStack product = new ItemStack(material, amount);
        ItemUtils.displayName(product, displayName);
        ItemUtils.lore(product, productLore);

        PlayerUtils.giveItem(player, product);
    }
}
