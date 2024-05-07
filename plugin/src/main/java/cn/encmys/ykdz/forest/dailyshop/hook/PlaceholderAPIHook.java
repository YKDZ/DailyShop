package cn.encmys.ykdz.forest.dailyshop.hook;

import cn.encmys.ykdz.forest.dailyshop.DailyShopImpl;
import cn.encmys.ykdz.forest.dailyshop.api.config.MessageConfig;
import cn.encmys.ykdz.forest.dailyshop.api.shop.Shop;
import cn.encmys.ykdz.forest.dailyshop.util.LogUtils;
import me.clip.placeholderapi.expansion.PlaceholderExpansion;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.jetbrains.annotations.NotNull;

public class PlaceholderAPIHook extends PlaceholderExpansion {
    public PlaceholderAPIHook() {
        if (isHooked()) {
            this.register();
            LogUtils.info("Hooked into PlaceholderAPI.");
        }
    }

    public static boolean isHooked() {
        return Bukkit.getPluginManager().getPlugin("PlaceholderAPI") != null;
    }

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
        if(params.contains("restock_timer_")){
            String shopId = params.replace("restock_timer_", "");
            Shop shop = DailyShopImpl.SHOP_FACTORY.getShop(shopId);
            if (shop == null) {
                return "Shop " + shopId + " not exist.";
            }
            long timeRemaining = (shop.getLastRestocking() + shop.getRestockTime() * 60L * 1000L) - System.currentTimeMillis();
            if (timeRemaining > 0) {
                long hours = timeRemaining / (60 * 60 * 1000);
                long minutes = (timeRemaining % (60 * 60 * 1000)) / (60 * 1000);
                long seconds = (timeRemaining % (60 * 1000)) / 1000;
                return String.format(MessageConfig.format_time, hours, minutes, seconds);
            }
        }
        return null;
    }
}
