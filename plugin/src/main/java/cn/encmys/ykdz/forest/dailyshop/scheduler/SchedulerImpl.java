package cn.encmys.ykdz.forest.dailyshop.scheduler;

import cn.encmys.ykdz.forest.dailyshop.api.DailyShop;
import cn.encmys.ykdz.forest.dailyshop.api.config.Config;
import cn.encmys.ykdz.forest.dailyshop.api.config.MessageConfig;
import cn.encmys.ykdz.forest.dailyshop.api.scheduler.Scheduler;
import cn.encmys.ykdz.forest.dailyshop.api.shop.Shop;
import cn.encmys.ykdz.forest.dailyshop.api.utils.LogUtils;
import cn.encmys.ykdz.forest.dailyshop.api.utils.TextUtils;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitScheduler;

import java.util.HashMap;

public class SchedulerImpl implements Scheduler {
    public SchedulerImpl() {
        runRestockTimer();
        runDataSaver();
    }

    @Override
    public void runRestockTimer() {
        BukkitScheduler scheduler = Bukkit.getScheduler();
        scheduler.runTaskTimer(DailyShop.INSTANCE, task -> {
            long now = System.currentTimeMillis();
            for (Shop shop : DailyShop.SHOP_FACTORY.getShops().values()) {
                if (shop.getShopStocker().needRestock() && shop.getShopStocker().getLastRestocking() + shop.getShopStocker().getRestockPeriod() * 50 <= now) {
                    shop.getShopStocker().stock();
                    for (Player player : Bukkit.getOnlinePlayers()) {
                        DailyShop.ADVENTURE_MANAGER.sendPlayerMessage(player, TextUtils.decorateTextKeepMiniMessage(MessageConfig.getShopOverrideableMessage(shop.getId(), "messages.notification.restock"), player, new HashMap<>() {{
                            put("shop", shop.getName());
                        }}));
                    }
                    LogUtils.info("Successfully restock shop " + shop.getId() + " automatically.");
                }
            }
        }, 0, Config.period_checkRestocking);
    }

    @Override
    public void runDataSaver() {
        BukkitScheduler scheduler = Bukkit.getScheduler();
        scheduler.runTaskTimerAsynchronously(DailyShop.INSTANCE, task -> {
            DailyShop.PROFILE_FACTORY.save();
            DailyShop.PRODUCT_FACTORY.save();
            DailyShop.SHOP_FACTORY.save();
            LogUtils.info("Successfully save shop and product data.");
        }, 0, Config.period_saveData);
    }
}
