package cn.encmys.ykdz.forest.dailyshop.item;

import cn.encmys.ykdz.forest.dailyshop.api.item.ProductItem;
import dev.lone.itemsadder.api.CustomStack;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class ItemsAdderProductItem implements ProductItem {
    private final String namespacedId;

    public ItemsAdderProductItem(@NotNull String namespacedId) {
        this.namespacedId = namespacedId;
    }

    @Override
    public String getDisplayName() {
        if (CustomStack.isInRegistry(namespacedId)) {
            return CustomStack.getInstance(namespacedId).getDisplayName();
        } else {
            return null;
        }
    }

    @Override
    public boolean isSimilar(ItemStack item) {
        return false;
    }

    @Override
    public ItemStack buildItem(@Nullable Player player) {
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
