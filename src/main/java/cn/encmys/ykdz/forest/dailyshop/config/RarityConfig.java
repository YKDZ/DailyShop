package cn.encmys.ykdz.forest.dailyshop.config;

import cn.encmys.ykdz.forest.dailyshop.DailyShop;
import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;

public class RarityConfig {
    private static final DailyShop plugin = DailyShop.getInstance();
    private static YamlConfiguration config;

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

    public static List<String> getAllId() {
        return Arrays.asList(config.getConfigurationSection("rarities").getKeys(false).toArray(new String[0]));
    }

    public static YamlConfiguration getConfig() {
        return config;
    }
}
