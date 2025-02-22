package cn.encmys.ykdz.forest.hyphashop.utils;

import cn.encmys.ykdz.forest.hyphashop.api.item.BaseItem;
import cn.encmys.ykdz.forest.hyphashop.api.item.decorator.BaseItemDecorator;
import cn.encmys.ykdz.forest.hyphashop.api.item.decorator.enums.PropertyType;
import cn.encmys.ykdz.forest.hyphashop.item.builder.BaseItemBuilder;
import cn.encmys.ykdz.forest.hyphautils.HyphaConfigUtils;
import io.papermc.paper.registry.RegistryAccess;
import io.papermc.paper.registry.RegistryKey;
import net.kyori.adventure.key.InvalidKeyException;
import net.kyori.adventure.key.Key;
import org.bukkit.Color;
import org.bukkit.DyeColor;
import org.bukkit.FireworkEffect;
import org.bukkit.Registry;
import org.bukkit.block.banner.PatternType;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.meta.trim.ArmorTrim;
import org.bukkit.inventory.meta.trim.TrimMaterial;
import org.bukkit.inventory.meta.trim.TrimPattern;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.potion.PotionType;
import org.intellij.lang.annotations.Subst;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.*;
import java.util.regex.Matcher;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class ConfigUtils {
    private static final List<String> nonInheritableKeys = new ArrayList<>() {{
        add("icons");
        // 单独处理
        add("buy-price");
        add("sell-price");
    }};

    public static void inheritConfigSection(@Nullable ConfigurationSection iconSection, @Nullable ConfigurationSection parentSection) {
        if (iconSection == null || parentSection == null) return;

        // 获取默认配置节中的键值对
        for (String key : parentSection.getKeys(true)) {
            // 如果目标配置节中没有此键，设置默认值
            if (!iconSection.contains(key) && !nonInheritableKeys.contains(key)) {
                Object value = parentSection.get(key);
                if (value instanceof ConfigurationSection defaultSection) {
                    // 如果是嵌套的配置节，递归调用
                    ConfigurationSection section = iconSection.createSection(key);
                    inheritConfigSection(section, defaultSection);
                } else {
                    // 直接设置值
                    iconSection.set(key, value);
                }
            }
        }
    }

    public static @NotNull ConfigurationSection inheritPriceSection(@Nullable ConfigurationSection section, @Nullable ConfigurationSection defaultSection) {
        if (section == null) {
            return defaultSection == null ? new YamlConfiguration() : defaultSection;
        } else if (defaultSection == null) {
            return section;
        }

        if (section.contains("formula") && !section.contains("context")) {
            section.set("context", defaultSection.getStringList("context"));
        } else if (!section.contains("formula") && section.contains("context")) {
            section.set("formula", defaultSection.getString("formula"));
        } else if (section.contains("min") && !section.contains("max")) {
            section.set("max", defaultSection.getDouble("max"));
        } else if (!section.contains("min") && section.contains("max")) {
            section.set("min", defaultSection.getDouble("min"));
        } else if (section.contains("mean") && !section.contains("dev")) {
            section.set("dev", defaultSection.getDouble("dev"));
        } else if (!section.contains("mean") && section.contains("dev")) {
            section.set("mean", defaultSection.getDouble("mean"));
        }

        return section;
    }

    public static int getInt(ConfigurationSection mainSection, ConfigurationSection defaultSection, String key, int defaultValue) {
        int value = defaultValue;
        if (mainSection != null) {
            value = mainSection.getInt(key, defaultValue);
        }
        if (value == defaultValue && defaultSection != null) {
            value = defaultSection.getInt(key, defaultValue);
        }
        return value;
    }

    public static double getDouble(ConfigurationSection mainSection, ConfigurationSection defaultSection, String key, double defaultValue) {
        double value = defaultValue;
        if (mainSection != null) {
            value = mainSection.getDouble(key, defaultValue);
        }
        if (value == defaultValue && defaultSection != null) {
            value = defaultSection.getDouble(key, defaultValue);
        }
        return value;
    }

    public static String getString(ConfigurationSection mainSection, ConfigurationSection defaultSection, String key, String defaultValue) {
        String value = defaultValue;
        if (mainSection != null) {
            value = mainSection.getString(key, defaultValue);
        }
        if (Objects.equals(value, defaultValue) && defaultSection != null) {
            value = defaultSection.getString(key, defaultValue);
        }
        return value;
    }

    public static boolean getBoolean(ConfigurationSection mainSection, ConfigurationSection defaultSection, String key, boolean defaultValue) {
        boolean value = defaultValue;
        if (mainSection != null) {
            value = mainSection.getBoolean(key, defaultValue);
        }
        if (Objects.equals(value, defaultValue) && defaultSection != null) {
            value = defaultSection.getBoolean(key, defaultValue);
        }
        return value;
    }

    public static int getLayoutMarkerAmount(List<String> layout, char markerIdentifier) {
        return layout.stream()
                .flatMapToInt(String::chars)
                .filter(c -> c == markerIdentifier)
                .map(c -> 1)
                .sum();
    }

    public static int getLayoutMarkerRowAmount(List<String> layout, char markerIdentifier) {
        return (int) layout.stream()
                .filter(line -> line.indexOf(markerIdentifier) != -1)
                .count();
    }

    public static int getLayoutMarkerColumAmount(List<String> layout, char markerIdentifier) {
        if (layout.isEmpty()) {
            return 0;
        }

        int maxColumnCount = layout.stream()
                .mapToInt(String::length)
                .max()
                .orElse(0);

        return (int) IntStream.range(0, maxColumnCount)
                .filter(colIndex -> layout.stream()
                        .anyMatch(str -> str.length() > colIndex && str.charAt(colIndex) == markerIdentifier))
                .count();
    }

    /**
     * @param data Trim data format like "diamond:vex"
     * @return ArmorTrim
     */
    public static @Nullable ArmorTrim parseArmorTrimData(@Nullable String data) {
        if (data == null) return null;

        String[] parsed = data.split(":");
        try {
            Registry<@NotNull TrimMaterial> materialRegistry = RegistryAccess.registryAccess().getRegistry(RegistryKey.TRIM_MATERIAL);
            Registry<@NotNull TrimPattern> patternRegistry = RegistryAccess.registryAccess().getRegistry(RegistryKey.TRIM_PATTERN);
            return new ArmorTrim(
                    materialRegistry.getOrThrow(Key.key(parsed[0])),
                    patternRegistry.getOrThrow(Key.key(parsed[1]))
            );
        } catch (Throwable ignored) {
            LogUtils.warn("Format of armor data: " + data + " is invalid. Use diamond:vex as fallback.");
            return new ArmorTrim(TrimMaterial.DIAMOND, TrimPattern.VEX);
        }
    }

    /**
     * @param data Hex color or enum field name of Color
     * @return Color
     */
    public static @Nullable Color parseColorData(@Nullable String data) {
        if (data == null) return null;

        Color color = Color.GREEN;
        if (data.startsWith("#")) {
            try {
                color = Color.fromRGB(Integer.parseInt(data.substring(1), 16));
            } catch (IllegalArgumentException e) {
                LogUtils.warn("Invalid color definition: " + data + ". Use green as fallback color.");
            }
        } else {
            try {
                return (Color) Color.class.getField(data).get(null);
            } catch (NoSuchFieldException | IllegalAccessException ignored) {
                LogUtils.warn("Invalid color definition: " + data + ". Use green as fallback color.");
            }
        }
        return color;
    }

    /**
     * @param data List of potion effect data format like "night_vision:100:1:true:true:true" (PotionEffectType:duration:amplifier:ambient:particles:icon)
     * @return Enchantment and Level
     */
    public static @Nullable List<PotionEffect> parsePotionEffectsData(@NotNull List<String> data) {
        if (data.isEmpty()) return null;
        return data.stream()
                .map(ConfigUtils::parsePotionEffectsData)
                .collect(Collectors.toList());
    }

    /**
     * @param data Potion effect data format like "night_vision:100:1:true:true:true" (PotionEffectType:duration:amplifier:ambient:particles:icon)
     * @return PotionEffect
     */
    private static @NotNull PotionEffect parsePotionEffectsData(@NotNull String data) {
        String[] parsed = data.split(":");
        try {
            Registry<@NotNull PotionEffectType> registry = RegistryAccess.registryAccess().getRegistry(RegistryKey.MOB_EFFECT);
            return new PotionEffect(
                    registry.getOrThrow(Key.key(parsed[0])),
                    Integer.parseInt(parsed[1]),
                    Integer.parseInt(parsed[2]),
                    Boolean.parseBoolean(parsed[3]),
                    Boolean.parseBoolean(parsed[4]),
                    Boolean.parseBoolean(parsed[5])
            );
        } catch (Throwable ignored) {
            LogUtils.warn("Potion effect data: " + data + " is invalid. Use night_vision:100:1:true:true:true as fallback.");
            return new PotionEffect(PotionEffectType.NIGHT_VISION, 100, 1, true, true, true);
        }
    }

    /**
     * @param data List of enchantment data format like "sharpness:5" or "knockback"
     * @return Enchantment and Level
     */
    public static @Nullable Map<Enchantment, Integer> parseEnchantmentData(@NotNull List<String> data) {
        if (data.isEmpty()) return null;
        return data.stream()
                .map(ConfigUtils::parseEnchantmentData)
                .collect(Collectors.toMap(
                        Map.Entry::getKey,
                        Map.Entry::getValue,
                        (existing, replacement) -> existing,
                        LinkedHashMap::new
                ));
    }

    /**
     * @param data Enchantment data format like "sharpness:5"
     * @return Enchantment and Level
     */
    private static @NotNull Map.Entry<Enchantment, Integer> parseEnchantmentData(@NotNull String data) {
        String[] parsed = data.split(":");

        try {
            Registry<@NotNull Enchantment> enchantmentRegistry = RegistryAccess.registryAccess().getRegistry(RegistryKey.ENCHANTMENT);
            Enchantment enchantment = enchantmentRegistry.getOrThrow(Key.key(parsed[0]));
            int level = Integer.parseInt(parsed[1]);

            return Map.entry(enchantment, level);
        } catch (Throwable ignored) {
            LogUtils.warn("Enchantment data: " + data + " is invalid. Use sharpness:5 as fallback");
            return Map.entry(Enchantment.SHARPNESS, 5);
        }
    }

    /**
     * @param data List of banner pattern data format like "YELLOW:bricks"
     * @return Pattern type and its color
     */
    public static @Nullable Map<PatternType, DyeColor> parseBannerPatternData(@NotNull List<String> data) {
        if (data.isEmpty()) return null;

        // 列表中靠上的图案在底层（先被绘制）
        return data.stream()
                .map(ConfigUtils::parseBannerPatternData)
                .collect(Collectors.toMap(
                        Map.Entry::getKey,
                        Map.Entry::getValue,
                        (existing, replacement) -> existing,
                        LinkedHashMap::new
                ));
    }

    /**
     * @param data Banner pattern data format like "YELLOW:bricks"
     * @return Pattern type and its color
     */
    private static @NotNull Map.Entry<PatternType, DyeColor> parseBannerPatternData(@NotNull @Subst("YELLOW:bricks") String data) {
        String[] parsed = data.split(":");

        try {
            DyeColor color = DyeColor.valueOf(parsed[0]);
            Registry<@NotNull PatternType> bannerPatternRegistry = RegistryAccess.registryAccess().getRegistry(RegistryKey.BANNER_PATTERN);
            PatternType type = bannerPatternRegistry.getOrThrow(Key.key(parsed[1]));

            return Map.entry(type, color);
        } catch (NoSuchElementException | InvalidKeyException e) {
            LogUtils.warn("Banner pattern data: " + data + " is invalid. Use YELLOW:bricks as fallback");
            return Map.entry(PatternType.BRICKS,  DyeColor.YELLOW);
        }
    }

    /**
     * @param data List of item flag data format like "HIDE_POTION_EFFECTS" or "-HIDE_ATTRIBUTES"
     * @return ItemFlag: isAdd
     */
    public static @Nullable Map<ItemFlag, Boolean> parseItemFlagData(@NotNull List<String> data) {
        if (data.isEmpty()) return null;
        return data.stream()
                .map(ConfigUtils::parseItemFlagData)
                .collect(Collectors.toMap(
                        Map.Entry::getKey,
                        Map.Entry::getValue,
                        (existing, replacement) -> existing,
                        LinkedHashMap::new
                ));
    }

    /**
     * @param data Item flag data format like "HIDE_ADDITIONAL_TOOLTIP"
     * @return ItemFlag: isAdd
     */
    private static Map.@NotNull Entry<ItemFlag, Boolean> parseItemFlagData(@NotNull String data) {
        try {
            ItemFlag flag = ItemFlag.valueOf(data.replaceAll("-", ""));
            boolean isAdd = !data.startsWith("-");
            return Map.entry(flag, !isAdd);
        } catch (IllegalArgumentException e) {
            LogUtils.warn("Banner pattern data: " + data + " is invalid. Use HIDE_ADDITIONAL_TOOLTIP as fallback");
            return Map.entry(ItemFlag.HIDE_ADDITIONAL_TOOLTIP, true);
        }
    }

    /**
     * @param data List of firework effect data format like "-t:BALL -c:[#FFFFFF, #123456] -fc:[#FFFFFF, #123456] -trail:true -flicker:true"
     * @return Firework effects
     */
    public static @Nullable List<FireworkEffect> parseFireworkEffectData(@NotNull List<String> data) {
        if (data.isEmpty()) return null;
        return data.stream().map(ConfigUtils::parseFireworkEffectData).collect(Collectors.toList());
    }

    /**
     * @param data Firework effect data format like "-t:BALL -c:[#FFFFFF, #123456] -fc:[#FFFFFF, #123456] -trail:true -flicker:true"
     * @return Firework effect
     */
    private static @NotNull FireworkEffect parseFireworkEffectData(@NotNull String data) {
        Map<String, String> params = new HashMap<>();
        Map<String, List<String>> listParams = new HashMap<>();

        java.util.regex.Pattern p = java.util.regex.Pattern.compile("-(\\w+):(\\[.*?]|\\w+)");
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

        return FireworkEffect.builder()
                .with(FireworkEffect.Type.valueOf(params.getOrDefault("t", "BALL")))
                .withColor(colors)
                .withFade(fadeColors)
                .flicker(Boolean.parseBoolean(params.getOrDefault("flicker", "false")))
                .trail(Boolean.parseBoolean(params.getOrDefault("trail", "false")))
                .build();
    }

    public static @Nullable PotionType parsePotionTypeData(@Nullable String data) {
        if (data == null) return null;

        Registry<@NotNull PotionType> registry = RegistryAccess.registryAccess().getRegistry(RegistryKey.POTION);

        PotionType type = PotionType.HEALING;
        try {
            type = registry.getOrThrow(Key.key(data));
        } catch (NoSuchElementException | InvalidKeyException ignored) {
            LogUtils.warn("Invalid potion type data: " + data + ". Use HEALING as fallback");
        }

        return type;
    }

    public static @NotNull Map<Character, BaseItemDecorator> getIconDecorators(@Nullable ConfigurationSection iconsSection) {
        if (iconsSection == null) return new HashMap<>();

        Map<Character, BaseItemDecorator> icons = new HashMap<>();
        for (String key : iconsSection.getKeys(false)) {
            ConfigurationSection section = iconsSection.getConfigurationSection(key);
            if (section == null) continue;
            icons.put(key.charAt(0), parseDecorator(section));
        }
        return icons;
    }

    public static @Nullable BaseItemDecorator parseDecorator(@Nullable ConfigurationSection section) {
        if (section == null) return null;

        String base = section.getString("base", "DIRT");
        BaseItem item = BaseItemBuilder.get(base);

        if (item == null) {
            LogUtils.warn("An icon has invalid base setting: " + base + ". Config section of this icon is: " + section);
            return null;
        }

        BaseItemDecorator decorator = new BaseItemDecorator(item)
                .setProperty(PropertyType.NAME, section.getString("name"))
                .setProperty(PropertyType.LORE, section.getStringList("lore"))
                .setProperty(PropertyType.AMOUNT, section.getString("amount", "1"))
                .setProperty(PropertyType.CUSTOM_MODEL_DATA, !section.contains("custom-model-data") ? null : section.getInt("custom-model-data"))
                .setProperty(PropertyType.UPDATE_PERIOD, TextUtils.parseTimeToTicks(section.getString("update-period")))
                .setProperty(PropertyType.UPDATE_ON_CLICK, section.getBoolean("update-on-click"))
                .setProperty(PropertyType.ITEM_FLAGS, parseItemFlagData(section.getStringList("item-flags")))
                .setProperty(PropertyType.BANNER_PATTERNS, parseBannerPatternData(section.getStringList("banner-patterns")))
                .setProperty(PropertyType.ENCHANTMENTS, parseEnchantmentData(section.getStringList("enchantments")))
                .setProperty(PropertyType.FIREWORK_EFFECTS, parseFireworkEffectData(section.getStringList("firework-effects")))
                .setProperty(PropertyType.POTION_EFFECTS, parsePotionEffectsData(section.getStringList("potion-effects")))
                .setProperty(PropertyType.ARMOR_TRIM, parseArmorTrimData(section.getString("armor-trim")))
                .setProperty(PropertyType.ENCHANT_GLINT, !section.contains("enchantment-glint") ? null : section.getBoolean("enchantment-glint"))
                .setProperty(PropertyType.ENCHANTABLE, !section.contains("enchantable") ? null : section.getInt("enchantable"))
                .setProperty(PropertyType.GLIDER, !section.contains("glider") ? null : section.getBoolean("glider"))
                .setProperty(PropertyType.FLIGHT_DURATION, !section.contains("flight-duration") ? null : section.getInt("flight-duration"))
                .setProperty(PropertyType.POTION_TYPE, parsePotionTypeData(!section.contains("potion-type") ? null : section.getString("potion-type")))
                .setProperty(PropertyType.POTION_COLOR, parseColorData(!section.contains("potion-color") ? null : section.getString("potion-color")))
                .setProperty(PropertyType.POTION_CUSTOM_NAME, !section.contains("potion-custom-name") ? null : section.getString("potion-custom-name"))
                .setProperty(PropertyType.CONDITIONAL_ICONS, getConditionIconRecords(section.getMapList("icons"), section));

        ConfigurationSection commands = section.getConfigurationSection("commands");
        if (commands != null) {
            decorator.setProperty(PropertyType.COMMANDS_DATA, new HashMap<>() {{
                put(ClickType.LEFT, commands.getStringList("left"));
                put(ClickType.RIGHT, commands.getStringList("right"));
                put(ClickType.SHIFT_LEFT, commands.getStringList("shift-left"));
                put(ClickType.SHIFT_RIGHT, commands.getStringList("shift-right"));
                put(ClickType.DOUBLE_CLICK, commands.getStringList("double-click"));
                put(ClickType.DROP, commands.getStringList("drop"));
                put(ClickType.CONTROL_DROP, commands.getStringList("control-drop"));
                put(ClickType.MIDDLE, commands.getStringList("middle"));
                put(ClickType.SWAP_OFFHAND, commands.getStringList("swap-offhand"));
                put(ClickType.NUMBER_KEY, commands.getStringList("number-key"));
                put(ClickType.WINDOW_BORDER_LEFT, commands.getStringList("window-border-left"));
                put(ClickType.WINDOW_BORDER_RIGHT, commands.getStringList("window-border-right"));
            }});
        }

        ConfigurationSection features = section.getConfigurationSection("features");
        if (features != null) {
            decorator.setProperty(PropertyType.FEATURE_SCROLL, EnumUtils.getEnumFromName(ClickType.class, features.getString("scroll")))
                    .setProperty(PropertyType.FEATURE_SCROLL_AMOUNT, features.getInt("scroll-amount", 0))
                    .setProperty(PropertyType.FEATURE_PAGE_CHANGE, EnumUtils.getEnumFromName(ClickType.class, features.getString("page-change")))
                    .setProperty(PropertyType.FEATURE_PAGE_CHANGE_AMOUNT, features.getInt("page-change-amount", 0))
                    .setProperty(PropertyType.FEATURE_BACK_TO_SHOP, EnumUtils.getEnumFromName(ClickType.class, features.getString("back-to-shop")))
                    .setProperty(PropertyType.FEATURE_SETTLE_CART, EnumUtils.getEnumFromName(ClickType.class, features.getString("settle-cart")))
                    .setProperty(PropertyType.FEATURE_OPEN_CART, EnumUtils.getEnumFromName(ClickType.class, features.getString("open-cart")))
                    .setProperty(PropertyType.FEATURE_SWITCH_SHOPPING_MODE, EnumUtils.getEnumFromName(ClickType.class, features.getString("switch-shopping-mode")))
                    .setProperty(PropertyType.FEATURE_SWITCH_CART_MODE, EnumUtils.getEnumFromName(ClickType.class, features.getString("switch-cart-mode")))
                    .setProperty(PropertyType.FEATURE_CLEAN_CART, EnumUtils.getEnumFromName(ClickType.class, features.getString("clean-cart")))
                    .setProperty(PropertyType.FEATURE_CLEAR_CART, EnumUtils.getEnumFromName(ClickType.class, features.getString("clear-cart")))
                    .setProperty(PropertyType.FEATURE_LOAD_MORE_LOG, EnumUtils.getEnumFromName(ClickType.class, features.getString("load-more-log")))
                    .setProperty(PropertyType.FEATURE_OPEN_SHOP, EnumUtils.getEnumFromName(ClickType.class, features.getString("open-shop")))
                    .setProperty(PropertyType.FEATURE_OPEN_SHOP_TARGET, features.getString("open-shop-target"))
                    .setProperty(PropertyType.FEATURE_OPEN_ORDER_HISTORY, EnumUtils.getEnumFromName(ClickType.class, features.getString("open-order-history")));

        }

        return decorator;
    }

    @NotNull
    public static Map<String, BaseItemDecorator> getConditionIconRecords(List<Map<?, ?>> conditionIconsList, ConfigurationSection parentIconSection) {
        Map<String, BaseItemDecorator> conditionIcons = new HashMap<>();
        for (Map<?, ?> map : conditionIconsList) {
            YamlConfiguration conditionIconSection = new YamlConfiguration();
            HyphaConfigUtils.loadMapIntoConfiguration(conditionIconSection, map, "");
            if (conditionIconSection.getBoolean("inherit", true)) {
                inheritConfigSection(conditionIconSection.getConfigurationSection("icon"), parentIconSection);
            }
            conditionIcons.put(conditionIconSection.getString("condition"), parseDecorator(conditionIconSection.getConfigurationSection("icon")));
        }
        return conditionIcons;
    }
}
