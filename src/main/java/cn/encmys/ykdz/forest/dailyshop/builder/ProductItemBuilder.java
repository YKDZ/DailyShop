package cn.encmys.ykdz.forest.dailyshop.builder;

import cn.encmys.ykdz.forest.dailyshop.DailyShop;
import cn.encmys.ykdz.forest.dailyshop.adventure.AdventureManager;
import cn.encmys.ykdz.forest.dailyshop.util.TextUtils;
import net.Indyuce.mmoitems.MMOItems;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class ProductItemBuilder {
    private static final AdventureManager adventureManager = DailyShop.getAdventureManager();
    private ItemStack base;
    private Material material;
    private String name;
    private List<String> lores;
    private int amount;
    private List<String> itemFLags;

    public static ProductItemBuilder mmoitems(String type, String id) {
        return new ProductItemBuilder()
                .setBase(MMOItems.plugin.getItem(type.toUpperCase(), id));
    }

    public ProductItemBuilder setBase(ItemStack base) {
        this.base = base;
        return this;
    }

    public ItemStack getBase() {
        return base;
    }

    public Material getMaterial() {
        return material;
    }

    public ProductItemBuilder setMaterial(Material material) {
        this.material = material;
        return this;
    }

    public ProductItemBuilder setName(String name) {
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

    public List<String> getLores() {
        return lores;
    }

    public ProductItemBuilder setLores(List<String> lores) {
        this.lores = lores;
        return this;
    }

    public ItemStack build(@Nullable Player player) {
        if (getBase() != null) {
            ItemMeta meta = getBase().getItemMeta();

            if (name != null) {
                meta.setDisplayName(TextUtils.decorateText(getName(), player));
            }

            if (getLores() != null && !getLores().isEmpty()) {
                meta.setLore(TextUtils.decorateText(getLores(), player));
            }

            base.setItemMeta(meta);
            base.setAmount(getAmount());

            return base;
        } else if (getMaterial() != null) {
            ItemStack item = new ItemStack(getMaterial());
            ItemMeta meta = item.getItemMeta();

            if (name != null) {
                meta.setDisplayName(TextUtils.decorateText(getName(), player));
            }

            if (getLores() != null && !getLores().isEmpty()) {
                meta.setLore(TextUtils.decorateText(getLores(), player));
            }

            item.setItemMeta(meta);
            item.setAmount(getAmount());

            return item;
        }
        return null;
    }
}
