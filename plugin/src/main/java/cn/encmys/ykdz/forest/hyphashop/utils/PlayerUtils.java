package cn.encmys.ykdz.forest.hyphashop.utils;

import cn.encmys.ykdz.forest.hyphashop.config.record.misc.SoundRecord;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class PlayerUtils {
    public static ItemStack getItemInMainHand(@NotNull Player player) {
        return player.getInventory().getItemInMainHand();
    }

    public static boolean hasInventorySpace(@NotNull Inventory inv, @Nullable ItemStack item, int stack) {
        if (item == null) {
            return true;
        }

        int neededSpace = item.getAmount() * stack;
        for (ItemStack check : inv.getStorageContents()) {
            if (check == null || check.getType().isAir()) {
                neededSpace -= item.getMaxStackSize();
            } else if (check.isSimilar(item)) {
                neededSpace -= item.getMaxStackSize() - check.getAmount();
            }
            if (neededSpace <= 0) {
                return true;
            }
        }
        return false;
    }

    public static void playSound(@NotNull SoundRecord soundRecord, @NotNull Player player) {
        if (soundRecord.disabled()) return;
        player.playSound(player.getLocation(), soundRecord.sound(), soundRecord.volume(), soundRecord.pitch());
    }
}
