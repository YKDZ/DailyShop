package cn.encmys.ykdz.forest.dailyshop.scheduler;

import cn.encmys.ykdz.forest.dailyshop.DailyShop;
import cn.encmys.ykdz.forest.dailyshop.config.Config;
import cn.encmys.ykdz.forest.dailyshop.shop.Shop;
import org.bukkit.Bukkit;
import org.bukkit.scheduler.BukkitScheduler;

import java.util.logging.Level;

public class Scheduler {
    private final DailyShop plugin;

    public Scheduler(DailyShop plugin) {
        this.plugin = plugin;
        runRestockTimer();
        runDataSaver();
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

    private void runDataSaver() {
        BukkitScheduler scheduler = Bukkit.getScheduler();
        scheduler.runTaskTimer(plugin, task -> {
            for(Shop shop : DailyShop.getShopFactory().getAllShops().values()) {
                shop.saveData();
            }
            DailyShop.getInstance().getLogger().log(Level.INFO, "Successfully saved store product data.");
        }, 0, Config.dataSaveTimer * 60L * 20L);
    }
}
