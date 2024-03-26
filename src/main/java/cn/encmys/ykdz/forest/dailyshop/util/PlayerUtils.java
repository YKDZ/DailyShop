package cn.encmys.ykdz.forest.dailyshop.util;

import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

public class PlayerUtils {
    public static ItemStack getItemInMainHand(Player player) {
        return player.getInventory().getItemInMainHand();
    }
}
