package cn.encmys.ykdz.forest.dailyshop.util;

import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class PlayerUtils {
    public static void giveItem(Player player, ItemStack item) {
        HashMap<Integer, ItemStack> left = player.getInventory().addItem(item);
        if (!left.isEmpty()) {
            for (Map.Entry<Integer, ItemStack> entry : left.entrySet()) {
                player.getWorld().dropItem(player.getLocation().add(0, 0.5, 0), entry.getValue());
            }
        }
    }

    public static void giveItem(Player player, List<ItemStack> items) {
        for (ItemStack item : items) {
            giveItem(player, item);
        }
    }

    public static boolean takeItem(Player player, ItemStack item) {
        Inventory inventory = player.getInventory();
        if (!inventory.contains(item)) {
            return false;
        }
        inventory.removeItem(item);
        return true;
    }
}
