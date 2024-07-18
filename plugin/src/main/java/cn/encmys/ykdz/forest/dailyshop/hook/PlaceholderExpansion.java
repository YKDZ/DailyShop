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
        if(params.contains("restock_timer_")){
            String shopId = params.replace("restock_timer_", "");
            Shop shop = DailyShopImpl.SHOP_FACTORY.getShop(shopId);
            if (shop == null) {
                return "Shop " + shopId + " not exist.";
            }
            // 1 ticks == 50ms
            long timeRemaining = (shop.getShopStocker().getLastRestocking() + shop.getShopStocker().getRestockPeriod() * 50L) - System.currentTimeMillis();
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
