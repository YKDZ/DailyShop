package cn.encmys.ykdz.forest.dailyshop.api.utils;

import cn.encmys.ykdz.forest.dailyshop.api.DailyShop;
import cn.encmys.ykdz.forest.dailyshop.api.config.record.misc.IconRecord;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Marker;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Objects;
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
                iconSection.getConfigurationSection("features")
        );
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
        return 0;
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
            LogUtils.error("Error when load yaml from resource: " + e.getMessage());
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
            LogUtils.info("Successfully merged " + resourcePath + " to new version.");
            return newConfig;
        }
        return config;
    }
}
