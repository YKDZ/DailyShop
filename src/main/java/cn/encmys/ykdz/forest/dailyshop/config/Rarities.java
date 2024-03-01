package cn.encmys.ykdz.forest.dailyshop.config;

import cn.encmys.ykdz.forest.dailyshop.DailyShop;
import org.bukkit.Material;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;

public class Rarities {
    private static final DailyShop plugin = DailyShop.getInstance();
    private static YamlConfiguration config;

    private static HashMap<String, ConfigurationSection> rarities = new HashMap<>();

    public static void load() {

        File file = new File(plugin.getDataFolder(), "rarities.yml");
        config = new YamlConfiguration();

        if (!file.exists()) {
            file.getParentFile().mkdirs();
            plugin.saveResource("rarities.yml", false);
        }

        try {
            config.load(file);
        } catch (IOException | InvalidConfigurationException error) {
            error.printStackTrace();
        }
    }

    public static String getName(String key) {
        return config.getString("rarities." + key + ".name", "Name invalid.");
    }

    public static Material getMaterial(String key) {
        return Material.matchMaterial(config.getString("rarities." + key + ".material", "DIRT"));
    }

    public static double getWeight(String key) {
        return config.getDouble("rarities." + key + ".material", 100);
    }

    public static YamlConfiguration getConfig() {
        return config;
    }
}
