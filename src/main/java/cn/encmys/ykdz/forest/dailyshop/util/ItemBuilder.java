package cn.encmys.ykdz.forest.dailyshop.util;

import org.bukkit.Color;
import org.bukkit.DyeColor;
import org.bukkit.FireworkEffect;
import org.bukkit.block.banner.Pattern;
import org.bukkit.block.banner.PatternType;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.BannerMeta;
import org.bukkit.inventory.meta.FireworkMeta;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.*;
import java.util.regex.Matcher;

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
        if (lore != null && !lore.isEmpty()) {
            lore.removeAll(Collections.singleton(null));
            meta.setLore(lore);
        }
        return this;
    }

    public ItemBuilder setCustomModelData(Integer data) {
        if (data != null) {
            meta.setCustomModelData(data);
        }
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

    public ItemBuilder setBannerPatterns(List<String> patternsData) {
        if (!(meta instanceof BannerMeta)) {
            return this;
        }

        for (String data : patternsData) {
            String[] parsed = data.split(":");
            DyeColor color = DyeColor.valueOf(parsed[0]);
            PatternType type = PatternType.valueOf(parsed[1]);
            ((BannerMeta) meta).addPattern(new Pattern(color, type));
        }
        return this;
    }

    // -t:BALL -c:[#FFFFFF, #123456] -fc:[#FFFFFF, #123456] -trail:true -flicker:true
    public ItemBuilder setFireworkEffects(List<String> fireworkEffects) {
        if (!(meta instanceof FireworkMeta)) {
            return this;
        }

        List<FireworkEffect> effects = new ArrayList<>();
        for (String data : fireworkEffects) {
            Map<String, String> params = new HashMap<>();
            Map<String, List<String>> listParams = new HashMap<>();

            java.util.regex.Pattern p = java.util.regex.Pattern.compile("-(\\w+):(\\[.*?\\]|\\w+)");
            Matcher m = p.matcher(data);

            while (m.find()) {
                String key = m.group(1);
                String value = m.group(2);

                if (value.startsWith("[")) {
                    String[] listValues = value.substring(1, value.length() - 1).split(",\\s*");
                    listParams.put(key, Arrays.asList(listValues));
                } else {
                    params.put(key, value);
                }
            }

            List<Color> colors = new ArrayList<>();
            List<Color> fadeColors = new ArrayList<>();

            for (String hex : listParams.get("c")) {
                colors.add(ColorUtils.getFromHex(hex));
            }

            for (String hex : listParams.get("fc")) {
                fadeColors.add(ColorUtils.getFromHex(hex));
            }

            effects.add(FireworkEffect.builder()
                            .with(FireworkEffect.Type.valueOf(params.getOrDefault("t", "BALL")))
                            .withColor(colors)
                            .withFade(fadeColors)
                            .flicker(Boolean.parseBoolean(params.getOrDefault("flicker", "false")))
                            .trail(Boolean.parseBoolean(params.getOrDefault("trail", "false")))
                            .build());
        }

        ((FireworkMeta) meta).addEffects(effects);

        return this;
    }

    public ItemStack build(int amount) {
        raw.setItemMeta(meta);
        raw.setAmount(amount);
        return raw;
    }
}
