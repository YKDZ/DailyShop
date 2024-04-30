package cn.encmys.ykdz.forest.dailyshop.scheduler;

import cn.encmys.ykdz.forest.dailyshop.DailyShop;
import cn.encmys.ykdz.forest.dailyshop.config.Config;
import cn.encmys.ykdz.forest.dailyshop.config.ShopConfig;
import cn.encmys.ykdz.forest.dailyshop.shop.Shop;
import cn.encmys.ykdz.forest.dailyshop.util.LogUtils;
import cn.encmys.ykdz.forest.dailyshop.util.TextUtils;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitScheduler;

import java.util.HashMap;

public class Scheduler {
    public Scheduler() {
        runRestockTimer();
        runDataSaver();
    }

    private void runRestockTimer() {
        BukkitScheduler scheduler = Bukkit.getScheduler();
        scheduler.runTaskTimer(DailyShop.INSTANCE, task -> {
            long now = System.currentTimeMillis();
            for (Shop shop : DailyShop.SHOP_FACTORY.getAllShops().values()) {
                if (shop.getLastRestocking() + (long) shop.getRestockTime() * 60 * 1000 <= now) {
                    shop.restock();
                    for (Player player : Bukkit.getOnlinePlayers()) {
                        DailyShop.ADVENTURE_MANAGER.sendPlayerMessage(player, TextUtils.parseInternalVariables(ShopConfig.getRestockNotification(shop.getId()), new HashMap<>() {{
                            put("shop", shop.getName());
                        }}));
                    }
                    LogUtils.info("Successfully restock shop " + shop.getId() + " automatically.");
                }
            }
        }, 0, 10);
    }

    private void runDataSaver() {
        BukkitScheduler scheduler = Bukkit.getScheduler();
        scheduler.runTaskTimerAsynchronously(DailyShop.INSTANCE, task -> {
            DailyShop.SHOP_FACTORY.save();
            LogUtils.info("Successfully save shop data.");
        }, 0, Config.dataSaveTimer * 60L * 20L);
    }
}
