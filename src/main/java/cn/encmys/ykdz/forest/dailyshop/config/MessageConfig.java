package cn.encmys.ykdz.forest.dailyshop.config;

import cn.encmys.ykdz.forest.dailyshop.DailyShop;
import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.io.IOException;

public class MessageConfig {
    private static final DailyShop plugin = DailyShop.getInstance();
    public static String messages_prefix;
    public static String messages_noPermission;
    public static String messages_command_reload_success;
    public static String messages_command_shop_open_success;
    public static String messages_command_shop_open_failure_invalidShop;
    public static String messages_command_shop_restock_success;
    public static String messages_command_shop_restock_failure_invalidShop;
    public static String messages_command_shop_save_success;
    public static String messages_command_product_check_success;
    public static String messages_command_product_check_failure_nullMeta;
    public static String messages_action_buy_success;
    public static String messages_action_buy_failure_disable;
    public static String messages_action_buy_failure_money;
    public static String messages_action_sell_success;
    public static String messages_action_sell_failure_disable;
    public static String messages_action_sell_failure_notEnough;
    public static String messages_action_sellAll_success;
    public static String messages_action_sellAll_failure_disable;
    public static String messages_action_sellAll_failure_notEnough;
    public static int version;
    private static YamlConfiguration config;

    public static void load() {
        File file = new File(plugin.getDataFolder(), "lang/" + Config.language + ".yml");
        config = new YamlConfiguration();

        if (!file.exists()) {
            file.getParentFile().mkdirs();
            plugin.saveResource("lang/" + Config.language + ".yml", false);
        }

        try {
            config.load(file);
            setUp();
        } catch (IOException | InvalidConfigurationException error) {
            error.printStackTrace();
        }
    }

    private static void setUp() {
        String error = "<red>There may be an error in your language file.";
        messages_prefix = config.getString("messages.prefix", "<gold>DailyShop <gray>-");
        messages_noPermission = config.getString("messages.no-permission", error);
        messages_command_reload_success = config.getString("messages.command.reload.success", error);
        messages_command_shop_open_success = config.getString("messages.command.shop.open.success", error);
        messages_command_shop_open_failure_invalidShop = config.getString("messages.command.shop.open.failure.invalid-shop", error);
        messages_command_shop_restock_success = config.getString("messages.command.shop.restock.success", error);
        messages_command_shop_restock_failure_invalidShop = config.getString("messages.command.shop.restock.failure.invalid-shop", error);
        messages_command_shop_save_success = config.getString("messages.command.shop.save", error);
        messages_command_product_check_success = config.getString("messages.command.product.check.success", error);
        messages_command_product_check_failure_nullMeta = config.getString("messages.command.product.check.failure.null-meta", error);
        messages_action_buy_success = config.getString("messages.action.buy.success", error);
        messages_action_buy_failure_disable = config.getString("messages.action.buy.failure.disable", error);
        messages_action_buy_failure_money = config.getString("messages.action.buy.failure.money", error);
        messages_action_sell_success = config.getString("messages.action.sell.success", error);
        messages_action_sell_failure_disable = config.getString("messages.action.sell.failure.disable", error);
        messages_action_sell_failure_notEnough = config.getString("messages.action.sell.failure.not-enough", error);
        messages_action_sellAll_success = config.getString("messages.action.sell-all.success", error);
        messages_action_sellAll_failure_disable = config.getString("messages.action.sell-all.failure.disable", error);
        messages_action_sellAll_failure_notEnough = config.getString("messages.action.sell-all.failure.not-enough", error);
        version = config.getInt("version");
    }

    public static YamlConfiguration getConfig() {
        return config;
    }
}
