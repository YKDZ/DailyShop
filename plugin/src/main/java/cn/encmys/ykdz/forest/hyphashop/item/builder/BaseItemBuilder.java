package cn.encmys.ykdz.forest.hyphashop.item.builder;

import cn.encmys.ykdz.forest.hyphashop.api.item.BaseItem;
import cn.encmys.ykdz.forest.hyphashop.hook.ItemsAdderHook;
import cn.encmys.ykdz.forest.hyphashop.hook.MMOItemsHook;
import cn.encmys.ykdz.forest.hyphashop.hook.MythicMobsHook;
import cn.encmys.ykdz.forest.hyphashop.item.*;
import cn.encmys.ykdz.forest.hyphashop.utils.LogUtils;
import net.kyori.adventure.key.InvalidKeyException;
import net.kyori.adventure.key.Key;
import org.bukkit.DyeColor;
import org.bukkit.Material;
import org.bukkit.Registry;
import org.bukkit.entity.Axolotl;
import org.bukkit.entity.TropicalFish;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.NoSuchElementException;

public class BaseItemBuilder {
    public static BaseItem get(String base) {
        if (base.startsWith(MMOItemsHook.getIdentifier())) {
            if (!MMOItemsHook.isHooked()) return null;

            String[] typeId = base.substring(MMOItemsHook.getIdentifier().length()).split(":");
            String type = typeId[0];
            String id = typeId[1];
            return mmoitems(type, id);
        } else if (base.startsWith(ItemsAdderHook.getIdentifier())) {
            if (!ItemsAdderHook.isHooked()) return null;

            String namespacedId = base.substring(ItemsAdderHook.getIdentifier().length());
            return itemsadder(namespacedId);
        } else if (base.startsWith(MythicMobsHook.getIdentifier())) {
            if (!MythicMobsHook.isHooked()) return null;

            String id = base.substring(MythicMobsHook.getIdentifier().length());
            return mythicmobs(id);
        } else if (base.startsWith("SKULL:")) {
            String data = base.substring(6);
            return skull(data);
        } else if (base.startsWith("TROPICAL_FISH_BUCKET:")) {
            String[] data = base.substring(21).split(":");

            TropicalFish.Pattern pattern = TropicalFish.Pattern.valueOf(data[0]);
            DyeColor bodyColor = DyeColor.valueOf(data[1]);
            DyeColor patternColor = DyeColor.valueOf(data[2]);

            return tropicalFishBucket(pattern, patternColor, bodyColor);
        } else if (base.startsWith("AXOLOTL_BUCKET:")) {
            String[] data = base.substring(15).split(":");

            Axolotl.Variant variant = Axolotl.Variant.valueOf(data[0]);

            return axolotlBucket(variant);
        } else {
            Registry<@NotNull Material> materialRegistry = Registry.MATERIAL;
            Material material;
            try {
                material = materialRegistry.getOrThrow(Key.key(base));
                if (material.name().toLowerCase().contains("potion")) return potion(material);
            } catch (NoSuchElementException | InvalidKeyException e) {
                LogUtils.warn("Material: " + base + " is invalid. Please check your item config.");
                return null;
            }
            return vanilla(material);
        }
    }

    public static @Nullable BaseItem mmoitems(@NotNull String type, @NotNull String id) {
        BaseItem item = new MMOItemsItem(type, id);
        if (!item.isExist()) {
            return null;
        }
        return item;
    }

    public static @Nullable BaseItem itemsadder(@NotNull String namespacedId) {
        BaseItem item = new ItemsAdderItem(namespacedId);
        if (!item.isExist()) {
            return null;
        }
        return item;
    }

    public static @Nullable BaseItem mythicmobs(@NotNull String id) {
        BaseItem item = new MythicMobsItem(id);
        if (!item.isExist()) {
            return null;
        }
        return item;
    }

    public static @Nullable BaseItem vanilla(@NotNull Material material) {
        BaseItem item = new VanillaItem(material);
        if (!item.isExist()) {
            return null;
        }
        return item;
    }

    public static @Nullable BaseItem potion(@NotNull Material material) {
        BaseItem item = new PotionItem(material);
        if (!item.isExist()) {
            return null;
        }
        return item;
    }

    public static @Nullable BaseItem skull(@NotNull String data) {
        BaseItem item = new SkullItem(data);
        if (!item.isExist()) {
            return null;
        }
        return item;
    }

    public static @Nullable BaseItem tropicalFishBucket(@NotNull TropicalFish.Pattern pattern, @NotNull DyeColor patternColor, @NotNull DyeColor bodyColor) {
        BaseItem item = new TropicalFishBucketItem(pattern, patternColor, bodyColor);
        if (!item.isExist()) {
            return null;
        }
        return item;
    }

    public static @Nullable BaseItem axolotlBucket(@NotNull Axolotl.Variant variant) {
        BaseItem item = new AxolotlBucketItem(variant);
        if (!item.isExist()) {
            return null;
        }
        return item;
    }
}
