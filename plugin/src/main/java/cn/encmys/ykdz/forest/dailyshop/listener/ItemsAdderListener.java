package cn.encmys.ykdz.forest.dailyshop.listener;

import cn.encmys.ykdz.forest.dailyshop.api.DailyShop;
import dev.lone.itemsadder.api.Events.ItemsAdderLoadDataEvent;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

public class ItemsAdderListener implements Listener {
    @EventHandler
    public void onItemsAdderLoad(ItemsAdderLoadDataEvent e) {
        DailyShop.INSTANCE.init();
    }
}
