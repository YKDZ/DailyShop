package cn.encmys.ykdz.forest.dailyshop.config;

import cn.encmys.ykdz.forest.dailyshop.DailyShop;
import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class ProductConfig {
    private static final DailyShop plugin = DailyShop.getInstance();
    private static HashMap<String, YamlConfiguration> configs = new HashMap<>();

    public static void load() {
        File directory = new File(plugin.getDataFolder() + "/product");

        if (!directory.exists() || !directory.isDirectory()) {
            directory.getParentFile().mkdirs();
        }

        File[] files = directory.listFiles();
        if (files == null) {
            return;
        }

        for (File file : files) {
            if (file.isFile() && file.getName().endsWith(".yml")) {
                YamlConfiguration config = new YamlConfiguration();
                try {
                    config.load(file);
                    configs.put(file.getName().replace(".yml", ""), config);
                } catch (IOException | InvalidConfigurationException error) {
                    error.printStackTrace();
                }
            }
        }
    }

    public static YamlConfiguration getConfig(String id) {
        return configs.get(id);
    }

    public static List<String> getAllId() {
        return new ArrayList<>(configs.keySet());
    }
}