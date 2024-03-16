package cn.encmys.ykdz.forest.dailyshop.util;

import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class PlayerUtils {
    public static void giveItem(@NotNull Player player, ItemStack item) {
        HashMap<Integer, ItemStack> left = player.getInventory().addItem(item);
        if (!left.isEmpty()) {
            for (Map.Entry<Integer, ItemStack> entry : left.entrySet()) {
                player.getWorld().dropItem(player.getLocation().add(0, 0.5, 0), entry.getValue());
            }
        }
    }

    public static void giveItem(Player player, @NotNull List<ItemStack> items) {
        for (ItemStack item : items) {
            giveItem(player, item);
        }
    }

    public static void takeItem(@NotNull Player player, ItemStack item, int amount) {
        takeItem(player.getInventory(), item, amount);
    }

    public static void takeItem(Inventory inventory, ItemStack item, int needed) {
        if (item == null || inventory == null) {
            return;
        }

        if (getItemAmount(inventory, item) < needed) {
            return;
        }

        for (ItemStack check : inventory) {
            if (check != null && check.isSimilar(item)) {
                int has = check.getAmount();
                if (needed <= has) {
                    check.setAmount(has - needed);
                } else {
                    check.setAmount(0);
                    needed -= has;
                }
            }
        }
    }

    public static int takeAllItems(Player player, ItemStack item) {
        return takeAllItems(player.getInventory(), item);
    }

    public static int takeAllItems(Inventory inventory, ItemStack item) {
        int unit = item.getAmount();
        int stack = getItemAmount(inventory, item) / unit;
        takeItem(inventory, item, unit * stack);
        return stack;
    }

    public static boolean hasItem(@NotNull Inventory inventory, @NotNull ItemStack item) {
        int needed = item.getAmount();
        for (ItemStack check : inventory) {
            if (check != null && check.isSimilar(item)) {
                int has = check.getAmount();
                if (needed <= has) {
                    return true;
                } else {
                    needed -= has;
                }
            }
        }
        return false;
    }

    public static boolean hasItem(@NotNull Player player, @NotNull ItemStack item) {
        return hasItem(player.getInventory(), item);
    }

    public static int getItemAmount(@NotNull Inventory inventory, @NotNull ItemStack item) {
        int total = 0;
        for (ItemStack check : inventory) {
            if (check != null && item.isSimilar(check)) {
                total += check.getAmount();
            }
        }
        return total;
    }
}
