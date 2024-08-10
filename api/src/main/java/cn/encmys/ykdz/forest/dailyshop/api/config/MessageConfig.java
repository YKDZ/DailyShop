package cn.encmys.ykdz.forest.dailyshop.api.config;

import cn.encmys.ykdz.forest.dailyshop.api.DailyShop;
import cn.encmys.ykdz.forest.dailyshop.api.profile.enums.ShoppingMode;
import cn.encmys.ykdz.forest.dailyshop.api.shop.order.enums.OrderType;
import cn.encmys.ykdz.forest.dailyshop.api.shop.order.enums.SettlementResult;
import cn.encmys.ykdz.forest.dailyshop.api.utils.EnumUtils;
import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.io.IOException;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.concurrent.TimeUnit;

public class MessageConfig {
    public static DecimalFormat format_decimal;
    public static String format_timer;
    public static String format_date_year;
    public static String format_date_month;
    public static String format_date_day;
    public static String format_date_hour;
    public static String format_date_minute;
    public static String format_date_second;
    public static String placeholderAPI_cartTotalPrice_notSellToMode;
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
    public static String messages_action_cart_openCart_success;
    public static String messages_action_cart_switchCartMode_success;
    public static String messages_action_cart_cleanCart_success;
    public static String messages_action_cart_clearCart_success;
    public static int version;
    private static final YamlConfiguration config = new YamlConfiguration();

    public static void load() {
        File file = new File(DailyShop.INSTANCE.getDataFolder(), "lang/" + Config.language_message + ".yml");

        if (!file.exists()) {
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
        format_timer = config.getString("format.timer", "%02dh:%02dm:%02ds");
        format_date_year = config.getString("format.date.year", " year(s) ");
        format_date_month = config.getString("format.date.month", " month(s) ");
        format_date_day = config.getString("format.date.day", " day(s) ");
        format_date_hour = config.getString("format.date.hour", " hour(s) ");
        format_date_minute = config.getString("format.date.minute", " minute(s) ");
        format_date_second = config.getString("format.date.second", " second(s) ");

        placeholderAPI_cartTotalPrice_notSellToMode = config.getString("placeholder-api.cart-total-price.not-sell-to-mode", "Not sell-to mode");

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

        messages_action_cart_openCart_success = getMessage("messages.action.cart.open-cart.success");
        messages_action_cart_switchCartMode_success = getMessage("messages.action.cart.switch-cart-mode.success");
        messages_action_cart_cleanCart_success = getMessage("messages.action.cart.clean-cart.success");
        messages_action_cart_clearCart_success = getMessage("messages.action.cart.clear-cart.success");

        version = config.getInt("version");
    }

    public static YamlConfiguration getConfig() {
        return config;
    }

    private static String getMessage(String path) {
        return config.getString(path, "<red>There may be an error in your language file. The related key is: " + path);
    }

    public static String getShopOverrideableString(String shopId, String path) {
        String shopMessage = ShopConfig.getConfig(shopId).getString(path);
        if (shopId == null || shopId.isEmpty() || shopMessage == null) {
            return getMessage(path);
        } else {
            return shopMessage;
        }
    }

    public static String getCartSettleMessage(OrderType type, SettlementResult result) {
        return getConfig().getString("messages.action.cart.settle-cart." + type.getConfigKey() + "." + result.getConfigKey());
    }

    public static String getTerm(OrderType orderType) {
        return config.getString("terms." + EnumUtils.toConfigName(OrderType.class) + "." + EnumUtils.toConfigName(orderType));
    }

    public static String getTerm(ShoppingMode shoppingMode) {
        return config.getString("terms." + EnumUtils.toConfigName(ShoppingMode.class) + "." + EnumUtils.toConfigName(shoppingMode));
    }

    public static String formatTime(Date date, int precision) {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy MM dd HH mm ss");
        String[] dateParts = sdf.format(date).split(" ");
        StringBuilder formattedDate = new StringBuilder();

        if (precision >= 1 && Integer.parseInt(dateParts[0]) != 0)
            formattedDate.append(dateParts[0]).append(format_date_year);
        if (precision >= 2 && Integer.parseInt(dateParts[1]) != 0)
            formattedDate.append(dateParts[1]).append(format_date_month);
        if (precision >= 3 && Integer.parseInt(dateParts[2]) != 0)
            formattedDate.append(dateParts[2]).append(format_date_day);
        if (precision >= 4 && Integer.parseInt(dateParts[3]) != 0)
            formattedDate.append(dateParts[3]).append(format_date_hour);
        if (precision >= 5 && Integer.parseInt(dateParts[4]) != 0)
            formattedDate.append(dateParts[4]).append(format_date_minute);
        if (precision >= 6 && Integer.parseInt(dateParts[5]) != 0)
            formattedDate.append(dateParts[5]).append(format_date_second);

        return formattedDate.toString();
    }

    public static String formatTime(long futureMillis, int precision) {
        long days = TimeUnit.MILLISECONDS.toDays(futureMillis);
        futureMillis -= TimeUnit.DAYS.toMillis(days);
        long hours = TimeUnit.MILLISECONDS.toHours(futureMillis);
        futureMillis -= TimeUnit.HOURS.toMillis(hours);
        long minutes = TimeUnit.MILLISECONDS.toMinutes(futureMillis);
        futureMillis -= TimeUnit.MINUTES.toMillis(minutes);
        long seconds = TimeUnit.MILLISECONDS.toSeconds(futureMillis);

        StringBuilder formattedTime = new StringBuilder();
        if (precision >= 3 && days != 0) formattedTime.append(days).append(days);
        if (precision >= 4 && hours != 0) formattedTime.append(hours).append(hours);
        if (precision >= 5 && minutes != 0) formattedTime.append(minutes).append(minutes);
        if (precision >= 6 && seconds != 0) formattedTime.append(seconds).append(seconds);

        return formattedTime.toString();
    }
}
