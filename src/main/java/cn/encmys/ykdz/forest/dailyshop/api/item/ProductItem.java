package cn.encmys.ykdz.forest.dailyshop.api.item;

import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.Nullable;

public interface ProductItem {
    String getDisplayName();

    boolean isSimilar(ItemStack item);

    ItemStack buildItem(@Nullable Player player);
}
