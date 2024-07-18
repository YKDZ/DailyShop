package cn.encmys.ykdz.forest.dailyshop.api.config;

import cn.encmys.ykdz.forest.dailyshop.api.DailyShop;
import cn.encmys.ykdz.forest.dailyshop.api.utils.TextUtils;
import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.io.IOException;

public class Config {
    protected static String path = DailyShop.INSTANCE.getDataFolder() + "/config.yml";
    protected static YamlConfiguration config = new YamlConfiguration();
    public static String language;
    public static int logDataLimit_entryAmount;
    public static double logDataLimit_timeRange;
    public static boolean priceCorrectByDisableSellOrBuy;
    public static long period_saveData;
    public static long period_updateProductIcon;
    public static long period_checkRestocking;
    public static int version;

    public static void load() {
        File file = new File(path);

        // Initialize Data folder when config.yml not exists
        if (!file.exists()) {
            file.getParentFile().mkdirs();
            DailyShop.INSTANCE.saveResource("config.yml", false);
            DailyShop.INSTANCE.saveResource("product/ores.yml", false);
            DailyShop.INSTANCE.saveResource("product/wools.yml", false);
            DailyShop.INSTANCE.saveResource("product/misc.yml", false);
            DailyShop.INSTANCE.saveResource("shop/black_market.yml", false);
            DailyShop.INSTANCE.saveResource("shop/blocks.yml", false);
            DailyShop.INSTANCE.saveResource("lang/en_US.yml", false);
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
        period_saveData = TextUtils.parseTimeToTicks(config.getString("period.save-data", "5m"));
        period_updateProductIcon = TextUtils.parseTimeToTicks(config.getString("period.update-product-icon", "3s"));
        period_checkRestocking = TextUtils.parseTimeToTicks(config.getString("period.check-restocking", "3s"));
        priceCorrectByDisableSellOrBuy = config.getBoolean("price-correct-by-disable-sell-or-buy", true);
        logDataLimit_entryAmount = config.getInt("log-data-limit.entry-amount", 500);
        logDataLimit_timeRange = TextUtils.parseTimeToTicks(config.getString("log-data-limit.time-range", "7d"));
        version = config.getInt("version");
    }

    public static YamlConfiguration getConfig() {
        return config;
    }
}
