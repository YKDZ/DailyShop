package cn.encmys.ykdz.forest.dailyshop.item;

import cn.encmys.ykdz.forest.dailyshop.api.item.BaseItem;
import cn.encmys.ykdz.forest.dailyshop.api.item.enums.BaseItemType;
import cn.encmys.ykdz.forest.hyphautils.HyphaAdventureUtils;
import dev.lone.itemsadder.api.CustomStack;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

public class ItemsAdderItem implements BaseItem {
    private final String namespacedId;

    public ItemsAdderItem(@NotNull String namespacedId) {
        this.namespacedId = namespacedId;
    }

    @Override
    public String getDisplayName() {
        if (isExist()) {
            return HyphaAdventureUtils.legacyToMiniMessage(CustomStack.getInstance(namespacedId).getDisplayName());
        } else {
            return null;
        }
    }

    @Override
    public boolean isSimilar(ItemStack item) {
        return false;
    }

    @Override
    public boolean isExist() {
        return CustomStack.isInRegistry(namespacedId);
    }

    @Override
    public BaseItemType getItemType() {
        return BaseItemType.ITEMS_ADDER;
    }

    @Override
    public ItemStack build(Player player) {
        CustomStack stack = CustomStack.getInstance(getNamespacedId());
        if (stack != null) {
            return stack.getItemStack().clone();
        } else {
            return new ItemStack(Material.AIR);
        }
    }

    public String getNamespacedId() {
        return namespacedId;
    }
}
