package cn.encmys.ykdz.forest.dailyshop.api.item;

import cn.encmys.ykdz.forest.dailyshop.api.item.enums.BaseItemType;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

public interface BaseItem {
    BaseItemType getItemType();
    String getDisplayName();

    ItemStack build(Player player);
    boolean isSimilar(ItemStack item);
    boolean isExist();
}
