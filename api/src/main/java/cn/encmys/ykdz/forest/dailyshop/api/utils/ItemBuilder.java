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
    @NotNull
    private final ItemStack raw;
    @NotNull
    private final ItemMeta meta;

    public ItemBuilder(@NotNull ItemStack raw) {
        this.raw = raw;
        this.meta = Optional.ofNullable(raw.getItemMeta())
                .orElse(new ItemStack(Material.BEDROCK).getItemMeta());
    }

    public ItemBuilder(@NotNull Material material) {
        this.raw = new ItemStack(material);
        this.meta = Optional.ofNullable(raw.getItemMeta())
                .orElse(new ItemStack(Material.BEDROCK).getItemMeta());
    }

    public @NotNull ItemBuilder setDisplayName(@Nullable Component displayName) {
        if (displayName != null) {
            meta.displayName(displayName);
        }
        return this;
    }

    public @NotNull ItemBuilder setLore(@Nullable List<Component> lore) {
        if (lore == null) return this;

        if (!lore.isEmpty()) {
            lore.removeAll(Collections.singleton(null));
            meta.lore(lore);
        }
        return this;
    }

    public @NotNull ItemBuilder setCustomModelData(@Nullable Integer data) {
        if (data != null) {
            meta.setCustomModelData(data);
        }
        return this;
    }

    public @NotNull ItemBuilder setItemFlags(@Nullable Map<ItemFlag, Boolean> itemFlags) {
        if (itemFlags == null) return this;

        for (Map.Entry<ItemFlag, Boolean> data : itemFlags.entrySet()) {
            if (!data.getValue()) {
                meta.removeItemFlags(data.getKey());
            } else {
                meta.addItemFlags(data.getKey());
            }
        }
        return this;
    }

    public @NotNull ItemBuilder setBannerPatterns(@Nullable Map<PatternType, DyeColor> bannerPatterns) {
        if (bannerPatterns == null) return this;

        if (!(meta instanceof BannerMeta)) {
            return this;
        }

        for (Map.Entry<PatternType, DyeColor> data : bannerPatterns.entrySet()) {
            ((BannerMeta) meta).addPattern(new Pattern(data.getValue(), data.getKey()));
        }

        return this;
    }

    // -t:BALL -c:[#FFFFFF, #123456] -fc:[#FFFFFF, #123456] -trail:true -flicker:true
    public @NotNull ItemBuilder setFireworkEffects(@Nullable List<FireworkEffect> fireworkEffects) {
        if (fireworkEffects == null) return this;

        if (!(meta instanceof FireworkMeta)) {
            return this;
        }

        ((FireworkMeta) meta).addEffects(fireworkEffects);

        return this;
    }

    public @NotNull ItemBuilder setEnchantments(@Nullable Map<Enchantment, Integer> enchantments) {
        if (enchantments == null) return this;

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

    public @NotNull ItemStack build(int amount) {
        raw.setItemMeta(meta);
        raw.setAmount(amount);
        return raw;
    }
}
