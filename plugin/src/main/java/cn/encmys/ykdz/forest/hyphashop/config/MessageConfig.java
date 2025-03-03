package cn.encmys.ykdz.forest.hyphashop.config;

import cn.encmys.ykdz.forest.hyphashop.api.HyphaShop;
import cn.encmys.ykdz.forest.hyphashop.api.profile.enums.ShoppingMode;
import cn.encmys.ykdz.forest.hyphashop.api.shop.order.enums.OrderType;
import cn.encmys.ykdz.forest.hyphashop.api.shop.order.enums.SettlementResult;
import cn.encmys.ykdz.forest.hyphashop.utils.EnumUtils;
import cn.encmys.ykdz.forest.hyphashop.utils.LogUtils;
import cn.encmys.ykdz.forest.hyphautils.HyphaConfigUtils;
import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.configuration.file.YamlConfiguration;
import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;

public class MessageConfig {
    public static DecimalFormat format_decimal;
    public static String format_timer;
    public static SimpleDateFormat format_date;
    //
    public static String placeholderAPI_cartTotalPrice_notSellToMode;
    //
    public static String messages_prefix;
    public static String messages_noPermission;
    public static String messages_command_reload_success;
    public static String messages_command_save_success;
    public static String messages_command_shop_open_success;
    public static String messages_command_shop_open_failure_invalidShop;
    public static String messages_command_shop_open_failure_invalidPlayer;
    public static String messages_command_shop_restock_success;
    public static String messages_command_shop_restock_failure_invalidShop;
    public static String messages_command_product_check_success;
    public static String messages_command_product_check_failure_nullMeta;
    public static String messages_command_shop_cache_clear_success;
    public static String messages_command_shop_cache_clear_failure_invalidShop;
    public static String messages_command_cart_open_success;
    public static String messages_command_cart_open_failure_invalidPlayer;
    public static String messages_command_history_open_success;
    public static String messages_command_history_open_failure_invalidPlayer;
    public static String messages_command_history_clean_success;
    public static String messages_command_history_clean_failure_invalidPlayer;
    public static String messages_command_history_clean_failure_invalidDayLateThan;
    public static String messages_command_shop_misc_switchShoppingMode_success;
    public static String messages_command_shop_misc_switchShoppingMode_failure_invalidPlayer;
    public static String messages_command_shop_misc_switchShoppingMode_failure_invalidShop;
    //
    public static String messages_action_cart_openCart_success;
    public static String messages_action_cart_switchCartMode_success;
    public static String messages_action_cart_cleanCart_success;
    public static String messages_action_cart_clearCart_success;
    public static int version;
    private static YamlConfiguration config = new YamlConfiguration();

    public static void load() {
        String resourcePath = "lang/" + Config.language_message + ".yml";
        String path = HyphaShop.INSTANCE.getDataFolder() + "/" + resourcePath;

        File file = new File(path);

        if (!file.exists()) {
            HyphaShop.INSTANCE.saveResource(resourcePath, false);
        }

        try {
            config.load(file);
            InputStream newConfigStream = HyphaShop.INSTANCE.getResource(resourcePath);
            if (newConfigStream == null) {
                LogUtils.error("Resource " + resourcePath + " not found");
                return;
            }
            config = cn.encmys.ykdz.forest.hyphautils.HyphaConfigUtils.merge(config, HyphaConfigUtils.loadYamlFromResource(newConfigStream), path);
            setUp();
        } catch (IOException | InvalidConfigurationException error) {
            LogUtils.error(error.getMessage());
        }
    }

    private static void setUp() {
        format_decimal = new DecimalFormat(config.getString("format.decimal", "###,###.##"));
        format_timer = config.getString("format.timer", "%02dh:%02dm:%02ds");
        format_date = new SimpleDateFormat(config.getString("format.date.pattern", "MMMM dd, yyyy HH:mm:ss"), HyphaConfigUtils.getLocale(config.getString("format.date.locale", "en_US")));

        placeholderAPI_cartTotalPrice_notSellToMode = config.getString("placeholder-api.cart-total-price.not-sell-to-mode", "Not sell-to mode");

        messages_prefix = config.getString("messages.prefix", "<gold>HyphaShop <gray>-");
        messages_noPermission = getMessage("messages.no-permission");
        messages_command_reload_success = getMessage("messages.command.reload.success");
        messages_command_save_success = getMessage("messages.command.save.success");
        messages_command_shop_open_success = getMessage("messages.command.shop.open.success");
        messages_command_shop_open_failure_invalidShop = getMessage("messages.command.shop.open.failure.invalid-shop");
        messages_command_shop_open_failure_invalidPlayer = getMessage("messages.command.shop.open.failure.invalid-player");
        messages_command_history_open_success = getMessage("messages.command.history.open.success");
        messages_command_history_open_failure_invalidPlayer = getMessage("messages.command.history.open.failure.invalid-player");
        messages_command_history_clean_success = getMessage("messages.command.history.clean.success");
        messages_command_history_clean_failure_invalidPlayer = getMessage("messages.command.history.clean.failure.invalid-player");
        messages_command_history_clean_failure_invalidDayLateThan = getMessage("messages.command.history.clean.failure.invalid-day-late-than");
        messages_command_cart_open_success = getMessage("messages.command.cart.open.success");
        messages_command_cart_open_failure_invalidPlayer = getMessage("messages.command.cart.open.failure.invalid-player");
        messages_command_shop_restock_success = getMessage("messages.command.shop.restock.success");
        messages_command_shop_restock_failure_invalidShop = getMessage("messages.command.shop.restock.failure.invalid-shop");
        messages_command_shop_cache_clear_success = getMessage("messages.command.shop.cache.clear.success");
        messages_command_shop_cache_clear_failure_invalidShop = getMessage("messages.command.shop.cache.clear.failure.invalid-shop");
        messages_command_product_check_success = getMessage("messages.command.product.check.success");
        messages_command_product_check_failure_nullMeta = getMessage("messages.command.product.check.failure.null-meta");
        messages_command_shop_misc_switchShoppingMode_success = getMessage("messages.command.shop.misc.switch-shopping-mode.success");
        messages_command_shop_misc_switchShoppingMode_failure_invalidPlayer = getMessage("messages.command.shop.misc.switch-shopping-mode.failure.invalid-player");
        messages_command_shop_misc_switchShoppingMode_failure_invalidShop = getMessage("messages.command.shop.misc.switch-shopping-mode.failure.invalid-shop");
        //
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

    public static @NotNull String getCartSettleMessage(@NotNull OrderType type, @NotNull SettlementResult result) {
        String path = "messages.action.cart.settle-cart." + type.getConfigKey() + "." + result.getConfigKey();
        String msg = getConfig().getString(path);
        if (msg == null) return "<red>There may be an error in your language file. The related key is: " + path;
        return msg;
    }

    public static String getTerm(OrderType orderType) {
        return config.getString("terms." + EnumUtils.toConfigName(OrderType.class) + "." + EnumUtils.toConfigName(orderType));
    }

    public static String getTerm(ShoppingMode shoppingMode) {
        return config.getString("terms." + EnumUtils.toConfigName(ShoppingMode.class) + "." + EnumUtils.toConfigName(shoppingMode));
    }
}
