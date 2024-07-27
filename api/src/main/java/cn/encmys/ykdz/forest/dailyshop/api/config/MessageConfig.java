package cn.encmys.ykdz.forest.dailyshop.api.config;

import cn.encmys.ykdz.forest.dailyshop.api.DailyShop;
import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.io.IOException;
import java.text.DecimalFormat;

public class MessageConfig {
    public static DecimalFormat format_decimal;
    public static String format_time;
    public static String messages_prefix;
    public static String messages_noPermission;
    public static String messages_command_reload_success;
    public static String messages_command_save_success;
    public static String messages_command_shop_open_success;
    public static String messages_command_shop_open_failure_invalidShop;
    public static String messages_command_shop_open_failure_invalidPlayer;
    public static String messages_command_shop_history_success;
    public static String messages_command_shop_history_failure_invalidShop;
    public static String messages_command_shop_history_failure_invalidPlayer;
    public static String messages_command_shop_cart_success;
    public static String messages_command_shop_cart_failure_invalidPlayer;
    public static String messages_command_shop_cart_failure_invalidShop;
    public static String messages_command_shop_restock_success;
    public static String messages_command_shop_restock_failure_invalidShop;
    public static String messages_command_product_check_success;
    public static String messages_command_product_check_failure_nullMeta;
    public static String messages_command_shop_cache_clear_success;
    public static String messages_command_shop_cache_clear_failure_invalidShop;
    public static int version;
    private static final YamlConfiguration config = new YamlConfiguration();

    public static void load() {
        File file = new File(DailyShop.INSTANCE.getDataFolder(), "lang/" + Config.language_message + ".yml");

        if (!file.exists()) {
            file.getParentFile().mkdirs();
            DailyShop.INSTANCE.saveResource("lang/" + Config.language_message + ".yml", false);
        }

        try {
            config.load(file);
            setUp();
        } catch (IOException | InvalidConfigurationException error) {
            error.printStackTrace();
        }
    }

    private static void setUp() {
        format_decimal = new DecimalFormat(config.getString("format.decimal", "###,###.##"));
        format_time = config.getString("format.time", "%02dh:%02dm:%02ds");

        messages_prefix = config.getString("messages.prefix", "<gold>DailyShop <gray>-");
        messages_noPermission = getMessage("messages.no-permission");
        messages_command_reload_success = getMessage("messages.command.reload.success");
        messages_command_save_success = getMessage("messages.command.save.success");
        messages_command_shop_open_success = getMessage("messages.command.shop.open.success");
        messages_command_shop_open_failure_invalidShop = getMessage("messages.command.shop.open.failure.invalid-shop");
        messages_command_shop_open_failure_invalidPlayer = getMessage("messages.command.shop.open.failure.invalid-player");
        messages_command_shop_history_success = getMessage("messages.command.shop.history.success");
        messages_command_shop_history_failure_invalidShop = getMessage("messages.command.shop.history.failure.invalid-shop");
        messages_command_shop_history_failure_invalidPlayer = getMessage("messages.command.shop.history.failure.invalid-player");
        messages_command_shop_cart_success = getMessage("messages.command.shop.cart.success");
        messages_command_shop_cart_failure_invalidShop = getMessage("messages.command.shop.cart.failure.invalid-shop");
        messages_command_shop_cart_failure_invalidPlayer = getMessage("messages.command.shop.cart.failure.invalid-player");
        messages_command_shop_restock_success = getMessage("messages.command.shop.restock.success");
        messages_command_shop_restock_failure_invalidShop = getMessage("messages.command.shop.restock.failure.invalid-shop");
        messages_command_shop_cache_clear_success = getMessage("messages.command.shop.cache.clear.success");
        messages_command_shop_cache_clear_failure_invalidShop = getMessage("messages.command.shop.cache.clear.failure.invalid-shop");
        messages_command_product_check_success = getMessage("messages.command.product.check.success");
        messages_command_product_check_failure_nullMeta = getMessage("messages.command.product.check.failure.null-meta");
        version = config.getInt("version");
    }

    public static YamlConfiguration getConfig() {
        return config;
    }

    private static String getMessage(String path) {
        return config.getString(path, "<red>There may be an error in your language file. The related key is: " + path);
    }

    public static String getActionMessage(String shopId, String path) {
        String shopMessage = ShopConfig.getConfig(shopId).getString("messages.action." + path);
        if (shopId == null || shopId.isEmpty() || shopMessage == null) {
            return getMessage("messages.action." + path);
        } else {
            return shopMessage;
        }
    }
}
