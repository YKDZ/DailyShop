package cn.encmys.ykdz.forest.dailyshop.api.config;

import cn.encmys.ykdz.forest.dailyshop.api.DailyShop;
import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

public class ProductConfig {
    private static String path = DailyShop.INSTANCE.getDataFolder() + "/product";
    private static final HashMap<String, YamlConfiguration> packs = new HashMap<>();

    public static void load() {
        File directory = new File(path);

        if (!directory.exists() || !directory.isDirectory()) {
            directory.getParentFile().mkdirs();
        }

        File[] files = directory.listFiles();

        if (files != null) {
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