package cn.encmys.ykdz.forest.dailyshop.api.item;

import cn.encmys.ykdz.forest.dailyshop.item.enums.BaseItemType;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.Nullable;

public interface BaseItem {
    String getDisplayName();

    boolean isSimilar(ItemStack item);

    BaseItemType getItemType();

    ItemStack build(@Nullable Player player);
}
