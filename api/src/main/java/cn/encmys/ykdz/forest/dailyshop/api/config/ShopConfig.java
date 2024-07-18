package cn.encmys.ykdz.forest.dailyshop.api.config;

import cn.encmys.ykdz.forest.dailyshop.api.DailyShop;
import cn.encmys.ykdz.forest.dailyshop.api.utils.TextUtils;
import org.bukkit.Sound;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.configuration.file.YamlConfiguration;
import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class ShopConfig {
    private static String path = DailyShop.INSTANCE.getDataFolder() + "/shop";
    private static final HashMap<String, YamlConfiguration> configs = new HashMap<>();

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
                        configs.put(file.getName().replace(".yml", ""), config);
                    } catch (IOException | InvalidConfigurationException error) {
                        error.printStackTrace();
                    }
                }
            }
        }
    }

    public static YamlConfiguration getConfig(String shopId) {
        return configs.get(shopId);
    }

    @NotNull
    public static List<String> getAllId() {
        return new ArrayList<>(configs.keySet());
    }

    public static int getSize(String shopId) {
        return getConfig(shopId).getInt("settings.size");
    }

    public static String getName(String shopId) {
        return getConfig(shopId).getString("settings.name");
    }

    public static long getRestockPeriod(String shopId) {
        return TextUtils.parseTimeToTicks(getConfig(shopId).getString("settings.restock-period"));
    }

    public static String getShopGUITitle(String shopId) {
        return getShopGUISection(shopId).getString("title");
    }

    public static String getHistoryGUITitle(String shopId) {
        return getHistoryGuiSection(shopId).getString("title");
    }

    public static String getProductNameFormat(String shopId) {
        return getShopGUISection(shopId).getString("product-icon.format.name", "{name}");
    }

    public static String getBundleContentsLineFormat(String shopId) {
        return getShopGUISection(shopId).getString("product-icon.format.bundle-contents-line", "<dark_gray>- {name} x {amount}");
    }

    @NotNull
    public static List<String> getProductLoreFormat(String shopId) {
        return getShopGUISection(shopId).getStringList("product-icon.format.lore");
    }

    public static String getDisabledPrice(String shopId) {
        return getShopGUISection(shopId).getString("product-icon.misc.disabled-price");
    }

    public static ConfigurationSection getShopGUISection(String shopId) {
        return getConfig(shopId).getConfigurationSection("shop-gui");
    }

    @NotNull
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

    public static ConfigurationSection getHistoryGuiSection(String shopId) {
        return getConfig(shopId).getConfigurationSection("history-gui");
    }
}
