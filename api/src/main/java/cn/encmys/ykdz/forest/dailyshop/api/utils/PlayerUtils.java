package cn.encmys.ykdz.forest.dailyshop.api.utils;

import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class PlayerUtils {
    public static ItemStack getItemInMainHand(Player player) {
        return player.getInventory().getItemInMainHand();
    }

    public static boolean hasInventorySpace(@NotNull Inventory inv, @Nullable ItemStack item, int stack) {
        if (item == null) {
            return true;
        }

        int neededSpace = item.getAmount() * stack;
        for (ItemStack check : inv) {
            if (check == null || check.getType().isAir()) {
                neededSpace -= item.getMaxStackSize();
            } else if (check.isSimilar(item)) {
                int remain = check.getMaxStackSize() - check.getAmount();
                neededSpace -= remain;
            }
            if (neededSpace <= 0) {
                return true;
            }
        }
        return false;
    }
}
