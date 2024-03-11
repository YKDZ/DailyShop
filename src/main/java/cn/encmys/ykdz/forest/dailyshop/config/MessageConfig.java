package cn.encmys.ykdz.forest.dailyshop.config;

import cn.encmys.ykdz.forest.dailyshop.DailyShop;
import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.io.IOException;

public class MessageConfig {
    private static final DailyShop plugin = DailyShop.getInstance();
    public static String messages_prefix;
    public static String messages_command_reload;
    public static String messages_command_restock;
    public static String messages_command_save;
    public static String messages_action_buy_success;
    public static String messages_action_buy_failure;
    public static String messages_action_sell_success;
    public static String messages_action_sell_failure;
    public static String messages_action_sellAll_success;
    public static String messages_action_sellAll_failure;
    public static int version;
    private static YamlConfiguration config;

    public static void load() {
        File file = new File(plugin.getDataFolder(), "lang/" + Config.language + ".yml");
        config = new YamlConfiguration();

        try {
            config.load(file);
            setUp();
        } catch (IOException | InvalidConfigurationException error) {
            error.printStackTrace();
        }
    }

    private static void setUp() {
        messages_prefix = config.getString("messages.prefix", "<gold>DailyShop <gray>-");
        messages_command_reload = config.getString("messages.command.reload", "<lime>Successfully reload the plugin!");
        messages_command_restock = config.getString("messages.command.restock", "<lime>Successfully restock shop {shop} <lime>manually!");
        messages_command_save = config.getString("messages.command.save", "<lime>Successfully save all shop data manually!");
        messages_action_buy_success = config.getString("messages.action.buy.success", "<green>Successfully buy <reset>{name} <gray>x <white>{amount} <green>from shop <reset>{shop}. The cost is {money}.");
        messages_action_buy_failure = config.getString("messages.action.buy.failure", "<gray>You do not have enough <red>money <gray>to buy this product!");
        messages_action_sell_success = config.getString("messages.action.sell.success", "<green>Successfully sell <reset>{name} <gray>x <white>{amount} <green>to shop <reset>{shop}. You earned {money}.");
        messages_action_sell_failure = config.getString("messages.action.sell.failure", "<gray>You do not have <reset>{name} <gray>x <white>{amount} <gray>in your inventory for sell.");
        messages_action_sellAll_success = config.getString("messages.action.sell-all.success", "<green>Successfully sell all of <reset>{name} <gray>x <white>{amount} <green> in your inventory to shop <reset>{shop}. You earned {money}.");
        messages_action_sellAll_failure = config.getString("messages.action.sell-all.failure", "<gray>You do not have <reset>{name} <gray>x <white>{amount} <gray>in your inventory for sell.");
        version = config.getInt("version");
    }

    public static YamlConfiguration getConfig() {
        return config;
    }
}
