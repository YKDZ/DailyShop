package cn.encmys.ykdz.forest.hyphashop.listener;

import cn.encmys.ykdz.forest.hyphashop.api.HyphaShop;
import dev.lone.itemsadder.api.Events.ItemsAdderLoadDataEvent;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

public class ItemsAdderListener implements Listener {
    @EventHandler
    public void onItemsAdderLoad(ItemsAdderLoadDataEvent e) {
        HyphaShop.INSTANCE.init();
    }
}
