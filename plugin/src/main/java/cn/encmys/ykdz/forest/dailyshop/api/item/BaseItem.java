package cn.encmys.ykdz.forest.dailyshop.api.item;

import cn.encmys.ykdz.forest.dailyshop.api.item.enums.BaseItemType;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.Nullable;

public interface BaseItem {
    BaseItemType getItemType();
    String getDisplayName();
    ItemStack build(@Nullable Player player);
    boolean isSimilar(ItemStack item);
    boolean isExist();
}
