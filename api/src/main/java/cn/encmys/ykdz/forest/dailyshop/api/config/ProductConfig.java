package cn.encmys.ykdz.forest.dailyshop.api.config;

import cn.encmys.ykdz.forest.dailyshop.api.DailyShop;
import cn.encmys.ykdz.forest.dailyshop.api.utils.LogUtils;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.configuration.file.YamlConfiguration;
import org.jetbrains.annotations.Nullable;

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
                        LogUtils.error(error.getMessage());
                    }
                }
            }
        }
    }

    @Nullable
    public static YamlConfiguration getConfig(String packId) {
        return packs.get(packId);
    }

    public static List<String> getAllPacksId() {
        return new ArrayList<>(packs.keySet());
    }

    @Nullable
    public static List<String> getAllProductId(String packId) {
        YamlConfiguration config = getConfig(packId);
        if (config == null) {
            return null;
        }
        ConfigurationSection section = config.getConfigurationSection("products");
        if (section == null) {
            return null;
        }
        return Arrays.asList(section.getKeys(false).toArray(new String[0]));
    }
}