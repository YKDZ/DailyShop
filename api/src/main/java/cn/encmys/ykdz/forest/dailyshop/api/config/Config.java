package cn.encmys.ykdz.forest.dailyshop.api.config;

import cn.encmys.ykdz.forest.dailyshop.api.DailyShop;
import cn.encmys.ykdz.forest.dailyshop.api.utils.TextUtils;
import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.io.IOException;

public class Config {
    private static final String path = DailyShop.INSTANCE.getDataFolder() + "/config.yml";
    private static final YamlConfiguration config = new YamlConfiguration();
    public static String language_message;
    public static String language_minecraftLang;
    public static int logUsageLimit_entryAmount;
    public static double logUsageLimit_timeRange;
    public static boolean priceCorrectByDisableSellOrBuy;
    public static long period_saveData;
    public static long period_checkRestocking;
    public static int version;

    public static void load() {
        File file = new File(path);

        // 当 config.yml 不存在时尝试初始化所有配置文件
        if (!file.exists()) {
            DailyShop.INSTANCE.saveResource("config.yml", false);
            DailyShop.INSTANCE.saveResource("product/ores.yml", false);
            DailyShop.INSTANCE.saveResource("product/wools.yml", false);
            DailyShop.INSTANCE.saveResource("product/misc.yml", false);
            DailyShop.INSTANCE.saveResource("shop/black_market.yml", false);
            DailyShop.INSTANCE.saveResource("shop/blocks.yml", false);
            DailyShop.INSTANCE.saveResource("gui/cart.yml", false);
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
        language_message = config.getString("language.message", "en_US");
        language_minecraftLang = config.getString("language.minecraft-lang", "en_us").toLowerCase();
        period_saveData = TextUtils.parseTimeToTicks(config.getString("period.save-data", "5m"));
        period_checkRestocking = TextUtils.parseTimeToTicks(config.getString("period.check-restocking", "3s"));
        priceCorrectByDisableSellOrBuy = config.getBoolean("price-correct-by-disable-sell-or-buy", true);
        logUsageLimit_entryAmount = config.getInt("log-usage-limit.entry-amount", 500);
        logUsageLimit_timeRange = TextUtils.parseTimeToTicks(config.getString("log-usage-limit.time-range", "7d"));
        version = config.getInt("version");
    }

    public static YamlConfiguration getConfig() {
        return config;
    }
}
