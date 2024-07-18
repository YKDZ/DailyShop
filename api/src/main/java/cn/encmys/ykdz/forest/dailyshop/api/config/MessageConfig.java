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
    public static String messages_command_shop_open_success;
    public static String messages_command_shop_open_failure_invalidShop;
    public static String messages_command_shop_history_success;
    public static String messages_command_shop_history_failure_invalidShop;
    public static String messages_command_shop_restock_success;
    public static String messages_command_shop_restock_failure_invalidShop;
    public static String messages_command_shop_save_success;
    public static String messages_command_product_check_success;
    public static String messages_command_product_check_failure_nullMeta;
    public static String messages_action_buy_success;
    public static String messages_action_buy_failure_disable;
    public static String messages_action_buy_failure_money;
    public static String messages_action_buy_failure_stock_global;
    public static String messages_action_buy_failure_stock_player;
    public static String messages_action_buy_failure_inventory_space;
    public static String messages_action_sell_success;
    public static String messages_action_sell_failure_disable;
    public static String messages_action_sell_failure_notEnough;
    public static String messages_action_sellAll_success;
    public static String messages_action_sellAll_failure_disable;
    public static String messages_action_sellAll_failure_notEnough;
    public static int version;
    private static final YamlConfiguration config = new YamlConfiguration();

    public static void load() {
        File file = new File(DailyShop.INSTANCE.getDataFolder(), "lang/" + Config.language + ".yml");

        if (!file.exists()) {
            file.getParentFile().mkdirs();
            DailyShop.INSTANCE.saveResource("lang/" + Config.language + ".yml", false);
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
        messages_command_shop_open_success = getMessage("messages.command.shop.open.success");
        messages_command_shop_open_failure_invalidShop = getMessage("messages.command.shop.open.failure.invalid-shop");
        messages_command_shop_history_success = getMessage("messages.command.shop.history.success");
        messages_command_shop_history_failure_invalidShop = getMessage("messages.command.shop.history.failure.invalid-shop");
        messages_command_shop_restock_success = getMessage("messages.command.shop.restock.success");
        messages_command_shop_restock_failure_invalidShop = getMessage("messages.command.shop.restock.failure.invalid-shop");
        messages_command_shop_save_success = getMessage("messages.command.shop.save");
        messages_command_product_check_success = getMessage("messages.command.product.check.success");
        messages_command_product_check_failure_nullMeta = getMessage("messages.command.product.check.failure.null-meta");
        messages_action_buy_success = getMessage("messages.action.buy.success");
        messages_action_buy_failure_disable = getMessage("messages.action.buy.failure.disable");
        messages_action_buy_failure_money = getMessage("messages.action.buy.failure.money");
        messages_action_buy_failure_stock_global = getMessage("messages.action.buy.failure.stock-global");
        messages_action_buy_failure_stock_player = getMessage("messages.action.buy.failure.stock-player");
        messages_action_buy_failure_inventory_space = getMessage("messages.action.buy.failure.inventory-space");
        messages_action_sell_success = getMessage("messages.action.sell.success");
        messages_action_sell_failure_disable = getMessage("messages.action.sell.failure.disable");
        messages_action_sell_failure_notEnough = getMessage("messages.action.sell.failure.not-enough");
        messages_action_sellAll_success = getMessage("messages.action.sell-all.success");
        messages_action_sellAll_failure_disable = getMessage("messages.action.sell-all.failure.disable");
        messages_action_sellAll_failure_notEnough = getMessage("messages.action.sell-all.failure.not-enough");
        version = config.getInt("version");
    }

    public static YamlConfiguration getConfig() {
        return config;
    }

    private static String getMessage(String path) {
        return config.getString(path, "<red>There may be an error in your language file. The related key is: " + path);
    }
}
