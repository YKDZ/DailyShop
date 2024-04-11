package cn.encmys.ykdz.forest.dailyshop.item;

import cn.encmys.ykdz.forest.dailyshop.DailyShop;
import cn.encmys.ykdz.forest.dailyshop.adventure.AdventureManager;
import cn.encmys.ykdz.forest.dailyshop.api.item.BaseItem;
import cn.encmys.ykdz.forest.dailyshop.item.enums.BaseItemType;
import dev.lone.itemsadder.api.CustomStack;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class ItemsAdderItem implements BaseItem {
    private static final AdventureManager adventureManager = DailyShop.getAdventureManager();
    private final String namespacedId;

    public ItemsAdderItem(@NotNull String namespacedId) {
        this.namespacedId = namespacedId;
    }

    @Override
    public String getDisplayName() {
        if (isExist()) {
            return adventureManager.legacyToMiniMessage(CustomStack.getInstance(namespacedId).getDisplayName());
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
        return BaseItemType.ITEMSADDER;
    }

    @Override
    public ItemStack build(@Nullable Player player) {
        CustomStack stack = CustomStack.getInstance(getNamespacedId());
        if(stack != null) {
            return stack.getItemStack();
        }
        else {
            return new ItemStack(Material.AIR);
        }
    }

    public String getNamespacedId() {
        return namespacedId;
    }
}
