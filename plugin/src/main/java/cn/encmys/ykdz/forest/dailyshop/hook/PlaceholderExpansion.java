package cn.encmys.ykdz.forest.dailyshop.hook;

import cn.encmys.ykdz.forest.dailyshop.DailyShopImpl;
import cn.encmys.ykdz.forest.dailyshop.api.DailyShop;
import cn.encmys.ykdz.forest.dailyshop.api.config.MessageConfig;
import cn.encmys.ykdz.forest.dailyshop.api.profile.Profile;
import cn.encmys.ykdz.forest.dailyshop.api.shop.Shop;
import cn.encmys.ykdz.forest.dailyshop.api.shop.order.ShopOrder;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;
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

    @NotNull
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

    @NotNull
    private static String merchantBalance(OfflinePlayer player, String params) {
        String shopId = params.replace("merchant_balance_", "");
        Shop shop = DailyShopImpl.SHOP_FACTORY.getShop(shopId);
        if (shop == null) {
            return "Shop " + shopId + " do not exist.";
        }
        return MessageConfig.format_decimal.format(shop.getShopCashier().getBalance());
    }

    @NotNull
    private static String cartTotalPrice(OfflinePlayer player, String params) {
        String shopId = params.replace("cart_total_price_", "");
        Shop shop = DailyShopImpl.SHOP_FACTORY.getShop(shopId);
        Profile profile = DailyShop.PROFILE_FACTORY.getProfile((Player) player);
        if (shop == null) {
            return "Shop " + shopId + " do not exist.";
        }
        if (profile == null) {
            return "Profile " + shopId + " do not exist.";
        }
        ShopOrder cart = profile.getCart(shopId);
        if (!cart.isBilled()) {
            shop.getShopCashier().billOrder(cart);
        }
        return String.valueOf(cart.getTotalPrice());
    }

    @NotNull
    private static String shoppingMode(OfflinePlayer player, String params) {
        String shopId = params.replace("shopping_mode_", "");
        Shop shop = DailyShopImpl.SHOP_FACTORY.getShop(shopId);
        if (shop == null) {
            return "Shop " + shopId + " do not exist.";
        }
        Profile profile = DailyShop.PROFILE_FACTORY.getProfile((Player) player);
        if (profile == null) {
            return "Player " + player.getName() + " do not have profile.";
        }
        return profile.getShoppingMode(shopId).name();
    }

    @Override
    public String onRequest(OfflinePlayer player, String params) {
        if (params.contains("restock_timer_")) {
            return restockTimer(player, params);
        } else if (params.contains("merchant_balance_")) {
            return merchantBalance(player, params);
        } else if (params.contains("cart_total_price_")) {
            return cartTotalPrice(player, params);
        } else if (params.contains("shopping_mode_")) {
            return shoppingMode(player, params);
        }
        return null;
    }
}
