package cn.encmys.ykdz.forest.dailyshop.hook;

import cn.encmys.ykdz.forest.dailyshop.DailyShopImpl;
import cn.encmys.ykdz.forest.dailyshop.api.DailyShop;
import cn.encmys.ykdz.forest.dailyshop.api.config.MessageConfig;
import cn.encmys.ykdz.forest.dailyshop.api.profile.Profile;
import cn.encmys.ykdz.forest.dailyshop.api.shop.Shop;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

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
        Shop shop = getShop(params, "restock_timer_");
        if (shop == null) return "Shop " + extractShopId(params, "restock_timer_") + " do not exist.";

        long timeRemaining = (shop.getShopStocker().getLastRestocking() + shop.getShopStocker().getRestockPeriod() * 50L) - System.currentTimeMillis();
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
        Shop shop = getShop(params, "merchant_balance_");
        if (shop == null) return "Shop " + extractShopId(params, "merchant_balance_") + " do not exist.";

        return MessageConfig.format_decimal.format(shop.getShopCashier().getBalance());
    }

    @NotNull
    private static String shoppingMode(@Nullable OfflinePlayer player, String params) {
        Player target = validatePlayer(player);
        if (target == null) return "Need a player to work.";

        Shop shop = getShop(params, "shopping_mode_");
        if (shop == null) return "Shop " + extractShopId(params, "shopping_mode_") + " do not exist.";

        Profile profile = DailyShop.PROFILE_FACTORY.getProfile(target);
        return MessageConfig.getTerm(profile.getShoppingMode(extractShopId(params, "shopping_mode_")));
    }

    @NotNull
    private static String cartMode(@Nullable OfflinePlayer player) {
        Player target = validatePlayer(player);
        if (target == null) return "Need a player to work.";

        Profile profile = DailyShop.PROFILE_FACTORY.getProfile(target);
        return MessageConfig.getTerm(profile.getCartMode());
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
        }
        return null;
    }

    @Nullable
    private static Shop getShop(String params, String prefix) {
        String shopId = extractShopId(params, prefix);
        return DailyShopImpl.SHOP_FACTORY.getShop(shopId);
    }

    @NotNull
    private static String extractShopId(String params, String prefix) {
        return params.replace(prefix, "");
    }

    private static Player validatePlayer(@Nullable OfflinePlayer player) {
        return player == null ? null : player.getPlayer();
    }
}
