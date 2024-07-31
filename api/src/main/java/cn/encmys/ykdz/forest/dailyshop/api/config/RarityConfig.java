package cn.encmys.ykdz.forest.dailyshop.api.config;

import cn.encmys.ykdz.forest.dailyshop.api.DailyShop;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class RarityConfig {
    private static String path = DailyShop.INSTANCE.getDataFolder() + "/rarities.yml";
    private static YamlConfiguration config = new YamlConfiguration();

    public static void load() {
        File file = new File(path);

        if (!file.exists()) {
            file.getParentFile().mkdirs();
            DailyShop.INSTANCE.saveResource("rarities.yml", false);
        }

        try {
            config.load(file);
        } catch (IOException | InvalidConfigurationException error) {
            error.printStackTrace();
        }
    }

    public static List<String> getAllId() {
        ConfigurationSection section = config.getConfigurationSection("rarities");
        if (section == null) {
            return new ArrayList<>();
        }
        return Arrays.asList(section.getKeys(false).toArray(new String[0]));
    }

    public static YamlConfiguration getConfig() {
        return config;
    }
}
