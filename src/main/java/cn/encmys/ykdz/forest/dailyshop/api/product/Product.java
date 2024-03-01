package cn.encmys.ykdz.forest.dailyshop.api.product;

import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

public interface Product {
    String getId();
    ItemStack getDisplayedItem();
    void deliver(Player player);
}
