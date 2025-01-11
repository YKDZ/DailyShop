package cn.encmys.ykdz.forest.dailyshop.item.builder;

import cn.encmys.ykdz.forest.dailyshop.api.item.BaseItem;
import cn.encmys.ykdz.forest.dailyshop.api.utils.LogUtils;
import cn.encmys.ykdz.forest.dailyshop.hook.ItemsAdderHook;
import cn.encmys.ykdz.forest.dailyshop.hook.MMOItemsHook;
import cn.encmys.ykdz.forest.dailyshop.hook.MythicMobsHook;
import cn.encmys.ykdz.forest.dailyshop.item.*;
import net.kyori.adventure.key.InvalidKeyException;
import net.kyori.adventure.key.Key;
import org.bukkit.DyeColor;
import org.bukkit.Material;
import org.bukkit.Registry;
import org.bukkit.entity.Axolotl;
import org.bukkit.entity.TropicalFish;

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
            String url = base.substring(6);
            return skull(url);
        } else if (base.startsWith("FIREWORK:")) {
            String power = base.substring(9);
            return fireworkRocket(Integer.parseInt(power));
        } else if (base.startsWith("POTION:")) {
            String[] data = base.substring(7).split(":");
            Material potionType = Material.POTION;
            if (data[0].equalsIgnoreCase("LINGERING")) {
                potionType = Material.LINGERING_POTION;
            } else if (data[0].equalsIgnoreCase("SPLASH")) {
                potionType = Material.SPLASH_POTION;
            }
            if (data[1].equals("NONE")) {
                return potion(potionType, data[1]);
            }
            return potion(potionType, data[1]);
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
            Registry<Material> materialRegistry = Registry.MATERIAL;
            Material material;
            try {
                material = materialRegistry.getOrThrow(Key.key(base));
            } catch (NoSuchElementException | InvalidKeyException e) {
                LogUtils.warn("Material: " + base + " is invalid. Please check your item config.");
                return null;
            }
            return vanilla(material);
        }
    }

    public static BaseItem mmoitems(String type, String id) {
        BaseItem item = new MMOItemsItem(type, id);
        if (!item.isExist()) {
            return null;
        }
        return item;
    }

    public static BaseItem itemsadder(String namespacedId) {
        BaseItem item = new ItemsAdderItem(namespacedId);
        if (!item.isExist()) {
            return null;
        }
        return item;
    }

    public static BaseItem mythicmobs(String id) {
        BaseItem item = new MythicMobsItem(id);
        if (!item.isExist()) {
            return null;
        }
        return item;
    }

    public static BaseItem vanilla(Material material) {
        BaseItem item = new VanillaItem(material);
        if (!item.isExist()) {
            return null;
        }
        return item;
    }

    public static BaseItem skull(String url) {
        BaseItem item = new SkullItem(url);
        if (!item.isExist()) {
            return null;
        }
        return item;
    }

    public static BaseItem fireworkRocket(int power) {
        BaseItem item = new FireworkRocketItem(power);
        if (!item.isExist()) {
            return null;
        }
        return item;
    }

    public static BaseItem potion(Material potionType, String effectType) {
        BaseItem item = new PotionItem(potionType, effectType);
        if (!item.isExist()) {
            return null;
        }
        return item;
    }

    public static BaseItem tropicalFishBucket(TropicalFish.Pattern pattern, DyeColor patternColor, DyeColor bodyColor) {
        BaseItem item = new TropicalFishBucketItem(pattern, patternColor, bodyColor);
        if (!item.isExist()) {
            return null;
        }
        return item;
    }

    public static BaseItem axolotlBucket(Axolotl.Variant variant) {
        BaseItem item = new AxolotlBucketItem(variant);
        if (!item.isExist()) {
            return null;
        }
        return item;
    }
}
