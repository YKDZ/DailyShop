package cn.encmys.ykdz.forest.dailyshop.builder;

import cn.encmys.ykdz.forest.dailyshop.api.item.ProductItem;
import cn.encmys.ykdz.forest.dailyshop.hook.ItemsAdderHook;
import cn.encmys.ykdz.forest.dailyshop.hook.MMOItemsHook;
import cn.encmys.ykdz.forest.dailyshop.hook.OraxenHook;
import cn.encmys.ykdz.forest.dailyshop.item.ItemsAdderItem;
import cn.encmys.ykdz.forest.dailyshop.item.MMOItemsItem;
import cn.encmys.ykdz.forest.dailyshop.item.OraxenItem;
import cn.encmys.ykdz.forest.dailyshop.item.VanillaItem;
import cn.encmys.ykdz.forest.dailyshop.util.ItemBuilder;
import cn.encmys.ykdz.forest.dailyshop.util.TextUtils;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class ProductItemBuilder {
    private ProductItem productItem;
    private String name;
    private List<String> lores;
    private int amount;
    private List<String> itemFlags;
    private Integer customModelData;

    private ProductItemBuilder() {
    }

    public static ProductItemBuilder get(String item) {
        if (item.startsWith("MI:") && MMOItemsHook.isHooked()) {
            String[] typeId = item.substring(3).split(":");
            String type = typeId[0];
            String id = typeId[1];
            return ProductItemBuilder.mmoitems(type, id);
        } else if (item.startsWith("IA:") && ItemsAdderHook.isHooked()) {
            String namespacedId = item.substring(3);
            return ProductItemBuilder.itemsadder(namespacedId);
        } else if (item.startsWith("OXN:") && OraxenHook.isHooked()) {
            String id = item.substring(3);
            return ProductItemBuilder.oraxen(id);
        } else {
            Material material = Material.valueOf(item);
            return ProductItemBuilder.vanilla(material);
        }
    }

    public static ProductItemBuilder mmoitems(String type, String id) {
        return new ProductItemBuilder()
                .setItem(new MMOItemsItem(type, id));
    }

    public static ProductItemBuilder itemsadder(String namespacedId) {
        return new ProductItemBuilder()
                .setItem(new ItemsAdderItem(namespacedId));
    }

    public static ProductItemBuilder oraxen(String id) {
        ProductItem item = new OraxenItem(id);
        return new ProductItemBuilder()
                .setItem(item);
    }

    public static ProductItemBuilder vanilla(Material material) {
        return new ProductItemBuilder()
                .setItem(new VanillaItem(material));
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

    public List<String> getItemFlags() {
        return itemFlags;
    }

    public ProductItemBuilder setItemFlags(List<String> itemFlags) {
        this.itemFlags = itemFlags;
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

    public ProductItemBuilder setCustomModelData(Integer data) {
        this.customModelData = data;
        return this;
    }

    public int getCustomModelData() {
        return customModelData;
    }

    public ItemStack build(@Nullable Player player) {
        return new ItemBuilder(getItem().build(player))
                .setDisplayName(TextUtils.decorateText(getName(), player))
                .setLore(TextUtils.decorateText(getLore(), player))
                .setItemFlags(getItemFlags())
                .setCustomModelData(getCustomModelData())
                .build(getAmount());
    }
}
