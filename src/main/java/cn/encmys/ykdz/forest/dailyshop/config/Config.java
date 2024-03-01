package cn.encmys.ykdz.forest.dailyshop.config;

import cn.encmys.ykdz.forest.dailyshop.DailyShop;
import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.io.IOException;

public class Config {
    private static final DailyShop plugin = DailyShop.getInstance();
    private static YamlConfiguration config;

    private static String language;
    private static double shopDefault_buyPrice;
    private static double shopDefault_sellPrice;
    private static int shopDefault_restockTimer;
    private static String shopDefault_rarity;
    private static String decimalFormat;
    private static String timeFormat;
    private static int version;

    public static void load() {
        File file = new File(plugin.getDataFolder(), "config.yml");
        config = new YamlConfiguration();

        if (!file.exists()) {
            file.getParentFile().mkdirs();
            plugin.saveResource("config.yml", false);
        }

        try {
            config.load(file);
            setUp();
        } catch (IOException | InvalidConfigurationException error) {
            error.printStackTrace();
        }
    }

    private static void setUp() {
        language = config.getString("language", "en_US");
        shopDefault_buyPrice = config.getDouble("shop-default.buy-price", 500d);
        shopDefault_sellPrice = config.getDouble("shop-default.sell-price", 20d);
        shopDefault_restockTimer = config.getInt("shop-default.restock-timer", 86400);
        shopDefault_rarity = config.getString("shop-default.rarity");
        decimalFormat = config.getString("decimal-format" ,"###,###.##");
        timeFormat = config.getString("time-format" ,"hh:mm a");
        version = config.getInt("version");
    }

    public static YamlConfiguration getConfig() {
        return config;
    }
}
