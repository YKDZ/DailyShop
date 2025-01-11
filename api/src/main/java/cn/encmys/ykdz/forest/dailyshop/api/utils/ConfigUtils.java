package cn.encmys.ykdz.forest.dailyshop.api.utils;

import cn.encmys.ykdz.forest.dailyshop.api.config.record.misc.IconRecord;
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
import org.bukkit.inventory.ItemFlag;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import xyz.xenondevs.invui.gui.structure.Marker;

import java.util.*;
import java.util.regex.Matcher;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class ConfigUtils {
    private static final List<String> nonInheritableKeys = new ArrayList<>() {{
        add("icons");
    }};

    public static ConfigurationSection inheritPriceSection(@Nullable ConfigurationSection section, @Nullable ConfigurationSection defaultSection) {
        if (section == null) {
            return defaultSection;
        } else if (defaultSection == null) {
            return section;
        }

        if (section.contains("formula") && !section.contains("vars")) {
            section.set("vars", defaultSection.getStringList("vars"));
        } else if (!section.contains("formula") && section.contains("vars")) {
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

    @Nullable
    public static IconRecord getIconRecord(@NotNull ConfigurationSection iconsSection, char iconKey) {
        ConfigurationSection iconSection = iconsSection.getConfigurationSection("icons." + iconKey);
        if (iconSection == null) {
            return null;
        }
        return getIconRecord(iconKey, iconSection);
    }

    @NotNull
    public static List<IconRecord> getIconRecords(@Nullable ConfigurationSection iconsSection) {
        if (iconsSection == null) {
            throw new RuntimeException("Attempted to read gui information, but the icons configuration section is empty.");
        }
        List<IconRecord> icons = new ArrayList<>();
        for (String key : iconsSection.getKeys(false)) {
            char iconKey = key.charAt(0);
            ConfigurationSection iconSection = iconsSection.getConfigurationSection(key);

            if (iconSection == null) {
                continue;
            }

            icons.add(getIconRecord(iconKey, iconSection));
        }
        return icons;
    }

    @Nullable
    public static IconRecord getIconRecord(char iconKey, @Nullable ConfigurationSection iconSection) {
        if (iconSection == null) {
            return null;
        }
        return new IconRecord(
                iconKey,
                iconSection.getString("base", "DIRT"),
                iconSection.getString("name", null),
                iconSection.getStringList("lore"),
                iconSection.getString("amount", "1"),
                TextUtils.parseTimeToTicks(iconSection.getString("update-period", "0s")),
                iconSection.getInt("custom-model-data"),
                iconSection.getConfigurationSection("commands"),
                iconSection.getStringList("item-flags"),
                iconSection.getStringList("banner-patterns"),
                iconSection.getStringList("firework-effects"),
                iconSection.getStringList("potion-effects"),
                iconSection.getStringList("enchantments"),
                iconSection.getConfigurationSection("features"),
                getConditionIconRecords(iconKey, iconSection.getMapList("icons"), iconSection)
        );
    }

    @NotNull
    public static Map<String, IconRecord> getConditionIconRecords(char parentKey, List<Map<?, ?>> conditionIconsList, ConfigurationSection parentIconSection) {
        Map<String, IconRecord> conditionIcons = new HashMap<>();
        for (Map<?, ?> map : conditionIconsList) {
            YamlConfiguration conditionIconSection = new YamlConfiguration();
            HyphaConfigUtils.loadMapIntoConfiguration(conditionIconSection, map, "");
            if (conditionIconSection.getBoolean("inherit", true)) {
                inheritIconSection(conditionIconSection.getConfigurationSection("icon"), parentIconSection);
            }
            conditionIcons.put(conditionIconSection.getString("condition"), new IconRecord(
                    parentKey,
                    // base 必须被继承以保证图标可用性
                    conditionIconSection.getString("icon.base", parentIconSection.getString("base", "DIRT")),
                    conditionIconSection.getString("icon.name", null),
                    conditionIconSection.getStringList("lore"),
                    conditionIconSection.getString("icon.amount", "1"),
                    // update-period 必须被继承以保证更新不是单向的
                    TextUtils.parseTimeToTicks(conditionIconSection.getString("icon.update-period", parentIconSection.getString("update-period", "0s"))),
                    conditionIconSection.getInt("icon.custom-model-data"),
                    conditionIconSection.getConfigurationSection("icon.commands"),
                    conditionIconSection.getStringList("icon.item-flags"),
                    conditionIconSection.getStringList("icon.banner-patterns"),
                    conditionIconSection.getStringList("icon.firework-effects"),
                    conditionIconSection.getStringList("icon.potion-effects"),
                    conditionIconSection.getStringList("icon.enchantments"),
                    conditionIconSection.getConfigurationSection("icon.features"),
                    new HashMap<>()
            ));
        }
        return conditionIcons;
    }

    private static void inheritIconSection(ConfigurationSection iconSection, ConfigurationSection parentSection) {
        // 获取默认配置节中的键值对
        for (String key : parentSection.getKeys(true)) {
            // 如果目标配置节中没有此键，设置默认值
            if (!iconSection.contains(key) && !nonInheritableKeys.contains(key)) {
                Object value = parentSection.get(key);
                if (value instanceof ConfigurationSection defaultSection) {
                    // 如果是嵌套的配置节，递归调用
                    ConfigurationSection configSection = iconSection.createSection(key);
                    inheritIconSection(configSection, defaultSection);
                } else {
                    // 直接设置值
                    iconSection.set(key, value);
                }
            }
        }
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

    public static int getLastLineMarkerAmount(List<String> layout, char markerIdentifier, Marker marker) {
        if (marker.isHorizontal()) {
            Optional<String> lastMatchingLine = layout.stream()
                    .filter(line -> line.indexOf(markerIdentifier) != -1) // 过滤包含符号的行
                    .reduce((first, second) -> second);         // 获取最后一个匹配的行
            return lastMatchingLine
                    .map(line -> (int) line.chars().filter(ch -> ch == markerIdentifier).count())
                    .orElse(0);
        } else {
            // 获取行数和列数（假设所有行长度相等）
            int columnCount = layout.isEmpty() ? 0 : layout.getFirst().length();
            // 遍历列
            return IntStream.range(0, columnCount)
                    .mapToObj(col -> {
                        // 构建列数据，将每行中对应的字符拼接成一个字符串
                        // 过滤掉列数不足的行
                        return layout.stream()
                                .filter(line -> line.length() > col) // 过滤掉列数不足的行
                                .map(line -> String.valueOf(line.charAt(col)))
                                .collect(Collectors.joining());
                    })
                    .filter(colData -> colData.indexOf(markerIdentifier) != -1) // 过滤包含符号的列
                    .reduce((first, second) -> second) // 获取最后一个匹配的列
                    .map(colData -> (int) colData.chars().filter(ch -> ch == markerIdentifier).count()) // 统计符号个数
                    .orElse(0); // 如果没有匹配的列，返回 0
        }
    }

    /**
     * @param data Enchantment data format like "sharpness:5" or "knockback"
     * @return Enchantment and Level
     */
    public static Map.Entry<Enchantment, Integer> parseEnchantmentData(@NotNull String data) {
        String[] parsed = data.split(":");

        if (parsed.length == 0) throw new IllegalArgumentException("Invalid enchantment data: " + data);

        Enchantment enchantment;

        try {
            Registry<Enchantment> enchantmentRegistry = RegistryAccess.registryAccess().getRegistry(RegistryKey.ENCHANTMENT);
            enchantment = enchantmentRegistry.getOrThrow(Key.key(parsed[0]));
        } catch (NoSuchElementException | InvalidKeyException e) {
            throw new IllegalArgumentException("Invalid enchantment data: " + data);
        }

        int level = 1;
        if (parsed.length == 2) {
            level = Integer.parseInt(parsed[1]);
        }

        return Map.entry(enchantment, level);
    }

    /**
     * @param data Banner pattern data format like "YELLOW:bricks"
     * @return Pattern type and its color
     */
    public static Map.Entry<PatternType, DyeColor> parseBannerPatternData(@NotNull String data) {
        String[] parsed = data.split(":");

        if (parsed.length == 0) throw new IllegalArgumentException("Invalid banner pattern data: " + data);

        DyeColor color = DyeColor.valueOf(parsed[0]);
        PatternType type;

        try {
            Registry<PatternType> bannerPatternRegistry = RegistryAccess.registryAccess().getRegistry(RegistryKey.BANNER_PATTERN);
            type = bannerPatternRegistry.getOrThrow(Key.key(parsed[1]));
        } catch (NoSuchElementException | InvalidKeyException e) {
            throw new IllegalArgumentException("Invalid banner pattern data: " + data);
        }

        return Map.entry(type, color);
    }

    /**
     * @param data Item flag data format like "HIDE_POTION_EFFECTS" or "-HIDE_ATTRIBUTES"
     * @return ItemFlag: isAdd
     */
    public static Map.Entry<ItemFlag, Boolean> parseItemFlagData(@NotNull String data) {
        ItemFlag flag;

        try {
            flag = ItemFlag.valueOf(data.replaceAll("-", ""));
        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException("Invalid item flag data: " + data);
        }

        boolean isAdd = !data.startsWith("-");

        return Map.entry(flag, !isAdd);
    }

    /**
     * @param data Firework effect data format like "-t:BALL -c:[#FFFFFF, #123456] -fc:[#FFFFFF, #123456] -trail:true -flicker:true"
     * @return Firework effect
     */
    public static FireworkEffect parseFireworkEffectData(@NotNull String data) {
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
}
