package cn.encmys.ykdz.forest.dailyshop.config;

import cn.encmys.ykdz.forest.dailyshop.DailyShop;
import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.io.IOException;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;

public class Config {
    private static final DailyShop plugin = DailyShop.getInstance();
    public static String language;
    private static String decimalFormat;
    private static String timeFormat;
    public static int dataSaveTimer;
    public static int version;
    private static YamlConfiguration config;

    public static void load() {
        File file = new File(plugin.getDataFolder(), "config.yml");
        config = new YamlConfiguration();

        // Initialize Data folder when config.yml not exists
        if (!file.exists()) {
            file.getParentFile().mkdirs();
            plugin.saveResource("config.yml", false);
            plugin.saveResource("product/concretes.yml", false);
            plugin.saveResource("product/wools.yml", false);
            plugin.saveResource("product/misc.yml", false);
            plugin.saveResource("shop/blocks.yml", false);
            plugin.saveResource("lang/en_US.yml", false);
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
        decimalFormat = config.getString("decimal-format", "###,###.##");
        timeFormat = config.getString("time-format", "hh:mm a");
        dataSaveTimer = config.getInt("data-save-timer", 5);
        version = config.getInt("version");
    }

    public static YamlConfiguration getConfig() {
        return config;
    }

    public static DecimalFormat getDecimalFormat() {
        return new DecimalFormat(decimalFormat);
    }

    public static SimpleDateFormat getSimpleDateFormat() {
        return new SimpleDateFormat(timeFormat);
    }
}
