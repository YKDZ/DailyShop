package cn.encmys.ykdz.forest.dailyshop.api.utils;

import net.kyori.adventure.text.Component;
import org.bukkit.DyeColor;
import org.bukkit.FireworkEffect;
import org.bukkit.Material;
import org.bukkit.block.banner.Pattern;
import org.bukkit.block.banner.PatternType;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.BannerMeta;
import org.bukkit.inventory.meta.EnchantmentStorageMeta;
import org.bukkit.inventory.meta.FireworkMeta;
import org.bukkit.inventory.meta.ItemMeta;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Optional;

public class ItemBuilder {
    private final ItemStack raw;
    private final ItemMeta meta;

    public ItemBuilder(ItemStack raw) {
        this.raw = raw;
        this.meta = Optional.ofNullable(raw.getItemMeta())
                .orElse(new ItemStack(Material.BEDROCK).getItemMeta());
    }

    public ItemBuilder(Material material) {
        this.raw = new ItemStack(material);
        this.meta = Optional.ofNullable(raw.getItemMeta())
                .orElse(new ItemStack(Material.BEDROCK).getItemMeta());
    }

    public ItemBuilder setDisplayName(@Nullable Component displayName) {
        if (displayName != null) {
            meta.displayName(displayName);
        }
        return this;
    }

    public ItemBuilder setLore(@NotNull List<Component> lore) {
        if (!lore.isEmpty()) {
            lore.removeAll(Collections.singleton(null));
            meta.lore(lore);
        }
        return this;
    }

    public ItemBuilder setCustomModelData(Integer data) {
        if (data != null) {
            meta.setCustomModelData(data);
        }
        return this;
    }

    public ItemBuilder setItemFlags(Map<ItemFlag, Boolean> itemFlags) {
        for (Map.Entry<ItemFlag, Boolean> data : itemFlags.entrySet()) {
            if (!data.getValue()) {
                meta.removeItemFlags(data.getKey());
            } else {
                meta.addItemFlags(data.getKey());
            }
        }
        return this;
    }

    public ItemBuilder setBannerPatterns(Map<PatternType, DyeColor> bannerPatterns) {
        if (!(meta instanceof BannerMeta)) {
            return this;
        }

        for (Map.Entry<PatternType, DyeColor> data : bannerPatterns.entrySet()) {
            ((BannerMeta) meta).addPattern(new Pattern(data.getValue(), data.getKey()));
        }

        return this;
    }

    // -t:BALL -c:[#FFFFFF, #123456] -fc:[#FFFFFF, #123456] -trail:true -flicker:true
    public ItemBuilder setFireworkEffects(List<FireworkEffect> fireworkEffects) {
        if (!(meta instanceof FireworkMeta)) {
            return this;
        }

        ((FireworkMeta) meta).addEffects(fireworkEffects);

        return this;
    }

    public ItemBuilder setEnchantments(Map<Enchantment, Integer> enchantments) {
        if (meta instanceof EnchantmentStorageMeta) {
            for (Map.Entry<Enchantment, Integer> entry : enchantments.entrySet()) {
                ((EnchantmentStorageMeta) meta).addStoredEnchant(entry.getKey(), entry.getValue(), true);
            }
        } else {
            for (Map.Entry<Enchantment, Integer> entry : enchantments.entrySet()) {
                meta.addEnchant(entry.getKey(), entry.getValue(), true);
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
