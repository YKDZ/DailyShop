package cn.encmys.ykdz.forest.dailyshop.builder;

import cn.encmys.ykdz.forest.dailyshop.DailyShop;
import cn.encmys.ykdz.forest.dailyshop.adventure.AdventureManager;
import cn.encmys.ykdz.forest.dailyshop.api.item.ProductItem;
import cn.encmys.ykdz.forest.dailyshop.item.ItemsAdderProductItem;
import cn.encmys.ykdz.forest.dailyshop.item.MMOItemsProductItem;
import cn.encmys.ykdz.forest.dailyshop.item.OraxenProductItem;
import cn.encmys.ykdz.forest.dailyshop.item.VanillaProductItem;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class ProductItemBuilder {
    private static final AdventureManager adventureManager = DailyShop.getAdventureManager();
    private ProductItem productItem;
    private String name;
    private List<String> lores;
    private int amount;
    private List<String> itemFLags;

    private ProductItemBuilder() {
    }

    public static ProductItemBuilder mmoitems(String type, String id) {
        return new ProductItemBuilder()
                .setItem(new MMOItemsProductItem(type, id));
    }

    public static ProductItemBuilder itemsadder(String namespacedId) {
        return new ProductItemBuilder()
                .setItem(new ItemsAdderProductItem(namespacedId));
    }

    public static ProductItemBuilder oraxen(String id) {
        ProductItem item = new OraxenProductItem(id);
        return new ProductItemBuilder()
                .setItem(item);
    }

    public static ProductItemBuilder vanilla(Material material) {
        return new ProductItemBuilder()
                .setItem(new VanillaProductItem(material));
    }

    public ProductItemBuilder setItem(ProductItem productItem) {
        this.productItem = productItem;
        return this;
    }

    public ProductItem getItem() {
        return productItem;
    }

    public ProductItemBuilder setName(String name) {
        if (name == null) {
            return this;
        }
        this.name = name;
        return this;
    }

    public String getName() {
        return name;
    }

    public List<String> getItemFLags() {
        return itemFLags;
    }

    public ProductItemBuilder setItemFLags(List<String> itemFLags) {
        this.itemFLags = itemFLags;
        return this;
    }

    public ProductItemBuilder setAmount(int amount) {
        this.amount = amount;
        return this;
    }

    public int getAmount() {
        return amount;
    }

    public List<String> getLore() {
        return lores;
    }

    public ProductItemBuilder setLore(List<String> lores) {
        this.lores = lores;
        return this;
    }

    public ItemStack build(@Nullable Player player) {
        ItemStack base = getItem().buildItem(player);

        base.setAmount(getAmount());

        return base;
    }
}
