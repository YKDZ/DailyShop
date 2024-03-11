package cn.encmys.ykdz.forest.dailyshop.config;

import cn.encmys.ykdz.forest.dailyshop.DailyShop;
import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

public class ProductConfig {
    private static final DailyShop plugin = DailyShop.getInstance();
    private static final HashMap<String, YamlConfiguration> packs = new HashMap<>();

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
                    packs.put(file.getName().replace(".yml", ""), config);
                } catch (IOException | InvalidConfigurationException error) {
                    error.printStackTrace();
                }
            }
        }
    }

    public static YamlConfiguration getConfig(String packId) {
        return packs.get(packId);
    }

    public static List<String> getAllPacksId() {
        return new ArrayList<>(packs.keySet());
    }

    public static List<String> getAllProductId(String packId) {
        return Arrays.asList(getConfig(packId).getConfigurationSection("products").getKeys(false).toArray(new String[0]));
    }
}