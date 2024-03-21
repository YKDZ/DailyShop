package cn.encmys.ykdz.forest.dailyshop.util;

import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.Collections;
import java.util.List;

public class ItemBuilder {
    private final ItemStack raw;
    private final ItemMeta meta;

    public ItemBuilder(ItemStack raw) {
        this.raw = raw;
        this.meta = raw.getItemMeta();
    }

    public ItemBuilder setDisplayName(String displayName) {
        if (displayName != null) {
            meta.setDisplayName(displayName);
        }
        return this;
    }

    public ItemBuilder setLore(List<String> lore) {
        if (lore != null) {
            lore.removeAll(Collections.singleton(null));
            meta.setLore(lore);
        }
        return this;
    }

    public ItemBuilder setCustomModelData(Integer data) {
        meta.setCustomModelData(data);
        return this;
    }

    public ItemBuilder setItemFlags(List<String> itemFlags) {
        for (String itemFlag : itemFlags) {
            ItemFlag flag = ItemFlag.valueOf(itemFlag);
            if (itemFlag.startsWith("-")) {
                meta.removeItemFlags(flag);
            } else {
                meta.addItemFlags(flag);
            }
        }
        return this;
    }

    public ItemStack build(int amount) {
        raw.setItemMeta(meta);
        raw.setAmount(amount);
        return raw;
    }
}
