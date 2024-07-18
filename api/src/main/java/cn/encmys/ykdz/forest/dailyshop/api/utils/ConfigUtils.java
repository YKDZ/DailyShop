package cn.encmys.ykdz.forest.dailyshop.api.utils;

import org.bukkit.configuration.ConfigurationSection;
import org.jetbrains.annotations.Nullable;

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
}
