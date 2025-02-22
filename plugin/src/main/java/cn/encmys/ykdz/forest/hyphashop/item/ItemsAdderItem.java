package cn.encmys.ykdz.forest.hyphashop.item;

import cn.encmys.ykdz.forest.hyphashop.api.item.BaseItem;
import cn.encmys.ykdz.forest.hyphashop.api.item.decorator.BaseItemDecorator;
import cn.encmys.ykdz.forest.hyphashop.api.item.enums.BaseItemType;
import cn.encmys.ykdz.forest.hyphautils.HyphaAdventureUtils;
import dev.lone.itemsadder.api.CustomStack;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class ItemsAdderItem implements BaseItem {
    private final @NotNull String namespacedId;

    public ItemsAdderItem(@NotNull String namespacedId) {
        this.namespacedId = namespacedId;
    }

    @Override
    public @Nullable String getDisplayName(@NotNull BaseItemDecorator decorator) {
        if (isExist()) {
            return HyphaAdventureUtils.legacyToMiniMessage(CustomStack.getInstance(namespacedId).getDisplayName());
        } else {
            return null;
        }
    }

    @Override
    public boolean isSimilar(@NotNull ItemStack item) {
        return false;
    }

    @Override
    public boolean isExist() {
        return CustomStack.isInRegistry(namespacedId);
    }

    @Override
    public @NotNull BaseItemType getItemType() {
        return BaseItemType.ITEMS_ADDER;
    }

    @Override
    public @NotNull ItemStack build(@Nullable Player player) {
        CustomStack stack = CustomStack.getInstance(getNamespacedId());
        if (stack != null) {
            return stack.getItemStack().clone();
        } else {
            return new ItemStack(Material.AIR);
        }
    }

    public @NotNull String getNamespacedId() {
        return namespacedId;
    }
}
