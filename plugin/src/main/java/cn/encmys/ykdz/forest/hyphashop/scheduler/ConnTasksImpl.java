package cn.encmys.ykdz.forest.hyphashop.scheduler;

import cn.encmys.ykdz.forest.hyphashop.api.HyphaShop;
import cn.encmys.ykdz.forest.hyphashop.api.scheduler.ConnTasks;
import cn.encmys.ykdz.forest.hyphashop.api.shop.Shop;
import cn.encmys.ykdz.forest.hyphashop.config.Config;
import cn.encmys.ykdz.forest.hyphashop.config.MessageConfig;
import cn.encmys.ykdz.forest.hyphashop.utils.LogUtils;
import cn.encmys.ykdz.forest.hyphashop.utils.TextUtils;
import cn.encmys.ykdz.forest.hyphashop.utils.VarUtils;
import cn.encmys.ykdz.forest.hyphautils.HyphaAdventureUtils;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

public class ConnTasksImpl implements ConnTasks {
    public ConnTasksImpl() {
        runRestockTimer();
        runDataSaver();
    }

    @Override
    public void runRestockTimer() {
        Scheduler.runAsyncTaskAtFixedRate(task -> {
            long now = System.currentTimeMillis();
            for (Shop shop : HyphaShop.SHOP_FACTORY.getShops().values()) {
                if (shop.getShopStocker().needAutoRestock() && shop.getShopStocker().getLastRestocking() + shop.getShopStocker().getAutoRestockPeriod() * 50 <= now) {
                    shop.getShopStocker().stock();
                    for (Player player : Bukkit.getOnlinePlayers()) {
                        HyphaAdventureUtils.sendPlayerMessage(player, TextUtils.decorateText(MessageConfig.getShopOverrideableString(shop.getId(), "messages.notification.restock"), player, VarUtils.extractVars(player, shop)));
                    }
                    LogUtils.info("Successfully restock shop " + shop.getId() + " automatically.");
                }
            }
        }, 0, Config.period_checkRestocking);
    }

    @Override
    public void runDataSaver() {
        Scheduler.runAsyncTaskAtFixedRate(task -> {
            HyphaShop.PROFILE_FACTORY.save();
            HyphaShop.PRODUCT_FACTORY.save();
            HyphaShop.SHOP_FACTORY.save();
            LogUtils.info("Successfully save all plugin data.");
        }, 0, Config.period_saveData);
    }
}
