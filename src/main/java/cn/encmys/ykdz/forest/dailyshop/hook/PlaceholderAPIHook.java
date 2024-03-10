package cn.encmys.ykdz.forest.dailyshop.hook;

import cn.encmys.ykdz.forest.dailyshop.DailyShop;
import cn.encmys.ykdz.forest.dailyshop.shop.Shop;
import me.clip.placeholderapi.expansion.PlaceholderExpansion;
import org.bukkit.OfflinePlayer;
import org.jetbrains.annotations.NotNull;

import java.util.concurrent.TimeUnit;

public class PlaceholderAPIHook extends PlaceholderExpansion {
    private final DailyShop plugin;

    public PlaceholderAPIHook(DailyShop plugin) {
        this.plugin = plugin;
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
            Shop shop = DailyShop.getShopFactory().getShop(shopId);
            if (shop == null) {
                return "Shop " + shopId + " not exist.";
            }
            long remain = shop.getRestockTime() * 60L * 1000L - (System.currentTimeMillis() - shop.getLastRestocking());
            return String.format("%02d:%02d:%02d",
                    TimeUnit.MILLISECONDS.toHours(remain),
                    TimeUnit.MILLISECONDS.toMinutes(remain),
                    TimeUnit.MILLISECONDS.toSeconds(remain)
            );
        }

        if(params.contains("product_amount_")){
            Shop shop = DailyShop.getShopFactory().getShop(params.replace("product_amount_", ""));
            return String.valueOf(shop.getListedProducts().size());
        }

        return null;
    }
}
