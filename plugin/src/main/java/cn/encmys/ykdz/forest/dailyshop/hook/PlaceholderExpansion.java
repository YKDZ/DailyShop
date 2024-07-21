package cn.encmys.ykdz.forest.dailyshop.hook;

import cn.encmys.ykdz.forest.dailyshop.DailyShopImpl;
import cn.encmys.ykdz.forest.dailyshop.api.config.MessageConfig;
import cn.encmys.ykdz.forest.dailyshop.api.shop.Shop;
import org.bukkit.OfflinePlayer;
import org.jetbrains.annotations.NotNull;

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

    @Override
    public String onRequest(OfflinePlayer player, String params) {
        if (params.contains("restock_timer_")) {
            return restockTimer(player, params);
        } else if (params.contains("merchant_balance_")) {
            return merchantBalance(player, params);
        }
        return null;
    }

    private static String restockTimer(OfflinePlayer player, String params) {
        String shopId = params.replace("restock_timer_", "");
        Shop shop = DailyShopImpl.SHOP_FACTORY.getShop(shopId);
        if (shop == null) {
            return "Shop " + shopId + " do not exist.";
        }
        long timeRemaining = (shop.getShopStocker().getLastRestocking() + shop.getShopStocker().getRestockPeriod() * 50L) - System.currentTimeMillis();
        if (timeRemaining > 0) {
            long hours = timeRemaining / (60 * 60 * 1000);
            long minutes = (timeRemaining % (60 * 60 * 1000)) / (60 * 1000);
            long seconds = (timeRemaining % (60 * 1000)) / 1000;
            return String.format(MessageConfig.format_time, hours, minutes, seconds);
        }
        return String.format(MessageConfig.format_time, 0, 0, 0);
    }

    private static String merchantBalance(OfflinePlayer player, String params) {
        String shopId = params.replace("merchant_balance_", "");
        Shop shop = DailyShopImpl.SHOP_FACTORY.getShop(shopId);
        if (shop == null) {
            return "Shop " + shopId + " do not exist.";
        }
        return MessageConfig.format_decimal.format(shop.getShopCashier().getBalance());
    }
}
