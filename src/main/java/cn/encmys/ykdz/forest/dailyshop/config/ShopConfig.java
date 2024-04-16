package cn.encmys.ykdz.forest.dailyshop.config;

import cn.encmys.ykdz.forest.dailyshop.DailyShop;
import org.bukkit.Sound;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.configuration.file.YamlConfiguration;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Set;

public class ShopConfig {
    private static final DailyShop plugin = DailyShop.getInstance();
    private static final HashMap<String, YamlConfiguration> configs = new HashMap<>();

    public static void load() {
        File directory = new File(plugin.getDataFolder() + "/shop");

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

    public static YamlConfiguration getConfig(String shopId) {
        return configs.get(shopId);
    }

    @Contract(" -> new")
    public static @NotNull List<String> getAllId() {
        return new ArrayList<>(configs.keySet());
    }

    public static int getSize(String shopId) {
        return getConfig(shopId).getInt("settings.size");
    }

    public static String getName(String shopId) {
        return getConfig(shopId).getString("settings.name");
    }

    public static int getRestockTimerSection(String shopId) {
        return getConfig(shopId).getInt("settings.restock-timer");
    }

    public static String getGUITitle(String shopId) {
        return getConfig(shopId).getString("shop-gui.title");
    }

    public static String[] getGUILayout(String shopId) {
        return getConfig(shopId).getStringList("shop-gui.layout").toArray(new String[0]);
    }

    public static @NotNull Set<String> getGUIIcons(String shopId) {
        return getConfig(shopId).getConfigurationSection("shop-gui.icons").getKeys(false);
    }

    public static ConfigurationSection getGUIIconSection(String shopId, char iconId) {
        return getGUISection(shopId).getConfigurationSection("icons." + iconId);
    }

    public static String getProductNameFormat(String shopId) {
        return getGUISection(shopId).getString("product-icon.name-format", "{name}");
    }

    public static String getBundleContentsLineFormat(String shopId) {
        return getGUISection(shopId).getString("product-icon.bundle-contents-line-format", "<dark_gray>- {name} x {amount}");
    }

    public static List<String> getProductLoreFormat(String shopId) {
        return getGUISection(shopId).getStringList("product-icon.lore-format");
    }

    public static ConfigurationSection getGUISection(String shopId) {
        return getConfig(shopId).getConfigurationSection("shop-gui");
    }

    public static List<String> getAllProductsId(String shopId) {
        return getConfig(shopId).getStringList("products");
    }

    public static String getRestockNotification(String shopId) {
        return getConfig(shopId).getString("messages.notification");
    }

    public static Sound getBuySound(String shopId) {
        String sound = getConfig(shopId).getString("sounds.buy", "ENTITY_VILLAGER_YES").toUpperCase();
        return Sound.valueOf(sound);
    }

    public static Sound getSellSound(String shopId) {
        String sound = getConfig(shopId).getString("sounds.sell", "ENTITY_VILLAGER_YES").toUpperCase();
        return Sound.valueOf(sound);
    }
}
