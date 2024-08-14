package cn.encmys.ykdz.forest.dailyshop.api.config;

import cn.encmys.ykdz.forest.dailyshop.api.DailyShop;
import cn.encmys.ykdz.forest.dailyshop.api.utils.LogUtils;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class RarityConfig {
    private static final String path = DailyShop.INSTANCE.getDataFolder() + "/rarities.yml";
    private static final YamlConfiguration config = new YamlConfiguration();

    public static void load() {
        File file = new File(path);

        if (!file.exists()) {
            DailyShop.INSTANCE.saveResource("rarities.yml", false);
        }

        try {
            config.load(file);
        } catch (IOException | InvalidConfigurationException error) {
            LogUtils.error(error.getMessage());
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
