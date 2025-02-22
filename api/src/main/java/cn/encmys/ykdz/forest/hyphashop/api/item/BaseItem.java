package cn.encmys.ykdz.forest.hyphashop.api.item;

import cn.encmys.ykdz.forest.hyphashop.api.item.decorator.BaseItemDecorator;
import cn.encmys.ykdz.forest.hyphashop.api.item.enums.BaseItemType;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public interface BaseItem {
    @NotNull BaseItemType getItemType();
    @Nullable String getDisplayName(@NotNull BaseItemDecorator decorator);
    @NotNull ItemStack build(@Nullable Player player);
    boolean isSimilar(@NotNull ItemStack item);
    boolean isExist();
}
