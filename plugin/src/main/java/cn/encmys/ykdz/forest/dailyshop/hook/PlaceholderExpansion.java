package cn.encmys.ykdz.forest.dailyshop.hook;

import cn.encmys.ykdz.forest.dailyshop.api.DailyShop;
import cn.encmys.ykdz.forest.dailyshop.api.config.Config;
import cn.encmys.ykdz.forest.dailyshop.api.config.MessageConfig;
import cn.encmys.ykdz.forest.dailyshop.api.product.Product;
import cn.encmys.ykdz.forest.dailyshop.api.profile.Profile;
import cn.encmys.ykdz.forest.dailyshop.api.shop.Shop;
import cn.encmys.ykdz.forest.dailyshop.api.shop.order.enums.OrderType;
import cn.encmys.ykdz.forest.dailyshop.api.utils.SettlementLogUtils;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Arrays;

public class PlaceholderExpansion extends me.clip.placeholderapi.expansion.PlaceholderExpansion {
    @Override
    public @NotNull String getAuthor() {
        return "YK_DZ";
    }

    @Override
    public @NotNull String getIdentifier() {
        return "dailyshop";
    }

    @Override
    public @NotNull String getVersion() {
        return "1.0.0";
    }

    @Override
    public boolean persist() {
        return true;
    }

    @NotNull
    private static String restockTimer(String params) {
        String shopId = params.replace("restock_timer_", "");
        Shop shop = DailyShop.SHOP_FACTORY.getShop(shopId);
        if (shop == null) return "Shop " + shopId + " do not exist.";

        long timeRemaining = (shop.getShopStocker().getLastRestocking() + shop.getShopStocker().getAutoRestockPeriod() * 50L) - System.currentTimeMillis();
        if (timeRemaining > 0) {
            long hours = timeRemaining / (60 * 60 * 1000);
            long minutes = (timeRemaining % (60 * 60 * 1000)) / (60 * 1000);
            long seconds = (timeRemaining % (60 * 1000)) / 1000;
            return String.format(MessageConfig.format_timer, hours, minutes, seconds);
        }
        return String.format(MessageConfig.format_timer, 0, 0, 0);
    }

    @NotNull
    private static String merchantBalance(String params) {
        String shopId = params.replace("merchant_balance_", "");
        Shop shop = DailyShop.SHOP_FACTORY.getShop(shopId);
        if (shop == null) return "Shop " + shopId + " do not exist.";

        return MessageConfig.format_decimal.format(shop.getShopCashier().getBalance());
    }

    @NotNull
    private static String shoppingMode(@Nullable OfflinePlayer player, String params) {
        Player target = player == null ? null : player.getPlayer();
        if (target == null) return "Need a player to work.";

        String shopId = params.replace("shopping_mode_", "");
        Shop shop = DailyShop.SHOP_FACTORY.getShop(shopId);
        if (shop == null) return "Shop " + shopId + " do not exist.";

        Profile profile = DailyShop.PROFILE_FACTORY.getProfile(target);
        return MessageConfig.getTerm(profile.getShoppingMode(shopId));
    }

    @NotNull
    private static String cartMode(@Nullable OfflinePlayer player) {
        Player target = player == null ? null : player.getPlayer();
        if (target == null) return "Need a player to work.";

        Profile profile = DailyShop.PROFILE_FACTORY.getProfile(target);
        return MessageConfig.getTerm(profile.getCart().getMode());
    }

    @NotNull
    private static String shopHistoryBuy(@NotNull String params) {
        // %dailyshop_shop_black_market_history_buy_COAL_ORE%
        String[] data = Arrays.stream(params.split("shop_|_history_buy_")).filter(s -> !s.isEmpty()).toArray(String[]::new);
        if (data.length != 2) return "Invalid params.";

        String shopId = data[0];
        Shop shop = DailyShop.SHOP_FACTORY.getShop(shopId);
        if (shop == null) return "Shop " + shopId + " do not exist.";

        String productId = data[1];
        Product product = DailyShop.PRODUCT_FACTORY.getProduct(productId);
        if (product == null) return "Product " + productId + " do not exist.";

        int historyBuy = SettlementLogUtils.getHistoryAmountFromLogs(shop.getId(), productId, Config.logUsageLimit_timeRange, Config.logUsageLimit_entryAmount, OrderType.SELL_TO);
        return String.valueOf(historyBuy);
    }

    @NotNull
    private static String shopHistorySell(@NotNull String params) {
        // %dailyshop_shop_black_market_history_sell_COAL_ORE%
        String[] data = Arrays.stream(params.split("shop_|_history_sell_")).filter(s -> !s.isEmpty()).toArray(String[]::new);
        if (data.length != 2) return "Invalid params.";

        String shopId = data[0];
        Shop shop = DailyShop.SHOP_FACTORY.getShop(shopId);
        if (shop == null) return "Shop " + shopId + " do not exist.";

        String productId = data[1];
        Product product = DailyShop.PRODUCT_FACTORY.getProduct(productId);
        if (product == null) return "Product " + productId + " do not exist.";

        int historySell = SettlementLogUtils.getHistoryAmountFromLogs(shop.getId(), productId, Config.logUsageLimit_timeRange, Config.logUsageLimit_entryAmount, OrderType.BUY_FROM, OrderType.BUY_ALL_FROM);
        return String.valueOf(historySell);
    }

    @Override
    public String onRequest(@Nullable OfflinePlayer player, @NotNull String params) {
        if (params.contains("restock_timer_")) {
            return restockTimer(params);
        } else if (params.contains("merchant_balance_")) {
            return merchantBalance(params);
        } else if (params.contains("shopping_mode_")) {
            return shoppingMode(player, params);
        } else if (params.contains("cart_mode")) {
            return cartMode(player);
        } else if (params.contains("shop_") && params.contains("_history_buy_")) {
            return shopHistoryBuy(params);
        } else if (params.contains("shop_") && params.contains("_history_sell_")) {
            return shopHistorySell(params);
        }
        return null;
    }
}
