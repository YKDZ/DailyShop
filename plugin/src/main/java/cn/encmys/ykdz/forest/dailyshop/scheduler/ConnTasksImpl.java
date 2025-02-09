package cn.encmys.ykdz.forest.dailyshop.scheduler;

import cn.encmys.ykdz.forest.dailyshop.api.DailyShop;
import cn.encmys.ykdz.forest.dailyshop.api.config.Config;
import cn.encmys.ykdz.forest.dailyshop.api.config.MessageConfig;
import cn.encmys.ykdz.forest.dailyshop.api.scheduler.ConnTasks;
import cn.encmys.ykdz.forest.dailyshop.api.shop.Shop;
import cn.encmys.ykdz.forest.dailyshop.api.utils.LogUtils;
import cn.encmys.ykdz.forest.dailyshop.api.utils.TextUtils;
import cn.encmys.ykdz.forest.hyphautils.HyphaAdventureUtils;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.util.HashMap;

public class ConnTasksImpl implements ConnTasks {
    public ConnTasksImpl() {
        runRestockTimer();
        runDataSaver();
    }

    @Override
    public void runRestockTimer() {
        Scheduler.runTaskAtFixedRate(task -> {
            long now = System.currentTimeMillis();
            for (Shop shop : DailyShop.SHOP_FACTORY.getShops().values()) {
                if (shop.getShopStocker().needAutoRestock() && shop.getShopStocker().getLastRestocking() + shop.getShopStocker().getAutoRestockPeriod() * 50 <= now) {
                    shop.getShopStocker().stock();
                    for (Player player : Bukkit.getOnlinePlayers()) {
                        HyphaAdventureUtils.sendPlayerMessage(player, TextUtils.decorateText(MessageConfig.getShopOverrideableString(shop.getId(), "messages.notification.restock"), player, new HashMap<>() {{
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
        Scheduler.runAsyncTaskAtFixedRate(task -> {
            DailyShop.PROFILE_FACTORY.save();
            DailyShop.PRODUCT_FACTORY.save();
            DailyShop.SHOP_FACTORY.save();
            LogUtils.info("Successfully save all plugin data.");
        }, 0, Config.period_saveData);
    }
}
