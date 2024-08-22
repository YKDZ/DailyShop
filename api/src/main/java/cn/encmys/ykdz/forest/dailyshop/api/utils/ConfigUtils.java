package cn.encmys.ykdz.forest.dailyshop.api.utils;

import cn.encmys.ykdz.forest.dailyshop.api.DailyShop;
import cn.encmys.ykdz.forest.dailyshop.api.config.record.misc.IconRecord;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.configuration.file.YamlConfiguration;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import xyz.xenondevs.invui.gui.structure.Marker;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class ConfigUtils {
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

    private static final List<String> nonInheritableKeys = new ArrayList<>() {{
        add("icons");
    }};

    @NotNull
    public static IconRecord getIconRecord(char iconKey, ConfigurationSection iconSection) {
        return new IconRecord(
                iconKey,
                iconSection.getString("base", "DIRT"),
                iconSection.getString("name", null),
                iconSection.getStringList("lore"),
                iconSection.getInt("amount", 1),
                TextUtils.parseTimeToTicks(iconSection.getString("update-period", "0s")),
                iconSection.getInt("custom-model-data"),
                iconSection.getConfigurationSection("commands"),
                iconSection.getStringList("item-flags"),
                iconSection.getStringList("banner-patterns"),
                iconSection.getStringList("firework-effects"),
                iconSection.getStringList("potion-effects"),
                iconSection.getConfigurationSection("features"),
                getConditionIconRecords(iconKey, iconSection.getMapList("icons"), iconSection)
        );
    }

    @NotNull
    public static Map<String, IconRecord> getConditionIconRecords(char parentKey, List<Map<?, ?>> conditionIconsList, ConfigurationSection parentIconSection) {
        Map<String, IconRecord> conditionIcons = new HashMap<>();
        for (Map<?, ?> map : conditionIconsList) {
            YamlConfiguration conditionIconSection = new YamlConfiguration();
            loadMapIntoConfiguration(conditionIconSection, map, "");
            if (conditionIconSection.getBoolean("inherit", true)) {
                inheritIconSection(conditionIconSection.getConfigurationSection("icon"), parentIconSection);
            }
            conditionIcons.put(conditionIconSection.getString("condition"), new IconRecord(
                    parentKey,
                    // base 必须被继承以保证图标可用性
                    conditionIconSection.getString("icon.base", parentIconSection.getString("base", "DIRT")),
                    conditionIconSection.getString("icon.name", null),
                    conditionIconSection.getStringList("icon.lore"),
                    conditionIconSection.getInt("icon.amount", 1),
                    // update-period 必须被继承以保证更新不是单向的
                    TextUtils.parseTimeToTicks(conditionIconSection.getString("icon.update-period", parentIconSection.getString("update-period", "0s"))),
                    conditionIconSection.getInt("icon.custom-model-data"),
                    conditionIconSection.getConfigurationSection("icon.commands"),
                    conditionIconSection.getStringList("icon.item-flags"),
                    conditionIconSection.getStringList("icon.banner-patterns"),
                    conditionIconSection.getStringList("icon.firework-effects"),
                    conditionIconSection.getStringList("icon.potion-effects"),
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

    private static void loadMapIntoConfiguration(ConfigurationSection section, Map<?, ?> map, String path) {
        for (Map.Entry<?, ?> entry : map.entrySet()) {
            String key = (String) entry.getKey();
            Object value = entry.getValue();
            String fullPath = path.isEmpty() ? key : path + "." + key;
            if (value instanceof Map<?, ?>) {
                loadMapIntoConfiguration(section.createSection(key), (Map<?, ?>) value, fullPath);
            } else {
                section.set(key, value);
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
            int columnCount = layout.isEmpty() ? 0 : layout.get(0).length();
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

    public static Locale getLocale(String locale) {
        String[] parts = locale.split("_", -1);
        return switch (parts.length) {
            case 1 -> new Locale(parts[0]);  // 仅语言代码
            case 2 -> new Locale(parts[0], parts[1]);  // 语言代码 + 国家代码
            case 3 -> new Locale(parts[0], parts[1], parts[2]);  // 语言代码 + 国家代码 + 变体
            default -> throw new IllegalArgumentException("Invalid locale format: " + locale);
        };
    }

    public static YamlConfiguration loadYamlFromResource(String path) {
        InputStream inputStream = DailyShop.INSTANCE.getResource(path);
        if (inputStream == null) {
            throw new IllegalArgumentException("Resource not found: " + path);
        }
        YamlConfiguration config = new YamlConfiguration();
        try {
            config.load(new InputStreamReader(inputStream, StandardCharsets.UTF_8));
        } catch (IOException | InvalidConfigurationException e) {
            LogUtils.error(e.getMessage());
        }
        return config;
    }

    public static YamlConfiguration merge(YamlConfiguration config, String resourcePath, String path) throws IOException {
        YamlConfiguration newConfig = ConfigUtils.loadYamlFromResource(resourcePath);
        if (newConfig.getInt("version") != config.getInt("version")) {
            for (String key : config.getKeys(true)) {
                if (key.equals("version")) {
                    continue;
                }
                if (newConfig.contains(key) && !(newConfig.get(key) instanceof ConfigurationSection)) {
                    newConfig.set(key, config.get(key));
                }
            }
            newConfig.save(path);
            LogUtils.info("Successfully merged " + resourcePath + " from version " + config.getInt("version") + " to version " + newConfig.getInt("version") + ".");
            return newConfig;
        }
        return config;
    }
}
