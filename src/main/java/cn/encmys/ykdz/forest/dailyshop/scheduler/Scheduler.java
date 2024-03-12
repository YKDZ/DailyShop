package cn.encmys.ykdz.forest.dailyshop.scheduler;

import cn.encmys.ykdz.forest.dailyshop.DailyShop;
import cn.encmys.ykdz.forest.dailyshop.adventure.AdventureManager;
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
    private final DailyShop plugin;
    private final AdventureManager adventuremanager = DailyShop.getAdventureManager();

    public Scheduler(DailyShop plugin) {
        this.plugin = plugin;
        runRestockTimer();
        runDataSaver();
    }

    private void runRestockTimer() {
        BukkitScheduler scheduler = Bukkit.getScheduler();
        scheduler.runTaskTimer(plugin, task -> {
            long currentTime = System.currentTimeMillis();
            for (Shop shop : DailyShop.getShopFactory().getAllShops().values()) {
                if (currentTime - shop.getLastRestocking() >= shop.getRestockTime() * 60L * 1000L) {
                    shop.restock();
                    for (Player player : Bukkit.getOnlinePlayers()) {
                        adventuremanager.sendPlayerMessage(player, TextUtils.parseInternalVariables(ShopConfig.getRestockNotification(shop.getId()), new HashMap<>() {{
                            put("shop", shop.getName());
                        }}));
                    }
                }
            }
        }, 0, 10);
    }

    private void runDataSaver() {
        BukkitScheduler scheduler = Bukkit.getScheduler();
        scheduler.runTaskTimer(plugin, task -> {
            for (Shop shop : DailyShop.getShopFactory().getAllShops().values()) {
                shop.saveData();
            }
            LogUtils.info("Successfully save shop data.");
        }, 0, Config.dataSaveTimer * 60L * 20L);
    }
}
