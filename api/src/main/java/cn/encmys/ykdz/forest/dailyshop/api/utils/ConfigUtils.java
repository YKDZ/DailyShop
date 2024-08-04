package cn.encmys.ykdz.forest.dailyshop.api.utils;

import cn.encmys.ykdz.forest.dailyshop.api.config.record.shop.IconRecord;
import org.bukkit.configuration.ConfigurationSection;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

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
                iconSection.getString("item", "DIRT"),
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
}
