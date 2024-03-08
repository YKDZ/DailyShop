package cn.encmys.ykdz.forest.dailyshop.scheduler;

import cn.encmys.ykdz.forest.dailyshop.DailyShop;
import cn.encmys.ykdz.forest.dailyshop.shop.Shop;
import org.bukkit.Bukkit;
import org.bukkit.scheduler.BukkitScheduler;

public class RestockScheduler {
    private final DailyShop plugin;

    public RestockScheduler(DailyShop plugin) {
        this.plugin = plugin;
        runRestockTimer();
    }

    private void runRestockTimer() {
        BukkitScheduler scheduler = Bukkit.getScheduler();
        scheduler.runTaskTimer(plugin, task -> {
            long currentTime = System.currentTimeMillis();
            for(Shop shop : DailyShop.getShopFactory().getAllShops().values()) {
                if(currentTime - shop.getLastRestocking() >= shop.getRestockTime() * 60L * 1000L) {
                    shop.restock();
                }
            }
        }, 0, 100);
    }
}
