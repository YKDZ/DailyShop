package cn.encmys.ykdz.forest.dailyshop.api.utils;

import cn.encmys.ykdz.forest.dailyshop.api.config.MessageConfig;
import cn.encmys.ykdz.forest.dailyshop.api.config.record.misc.SoundRecord;
import cn.encmys.ykdz.forest.hyphautils.HyphaAdventureUtils;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Map;

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

    public static void playSound(SoundRecord soundRecord, Player player) {
        if (soundRecord != null) {
            player.playSound(player.getLocation(), soundRecord.sound(), soundRecord.volume(), soundRecord.pitch());
        }
    }

    public static void sendMessage(String message, Player player, Map<String, String> vars) {
        HyphaAdventureUtils.sendMessage(player, MessageConfig.messages_prefix + TextUtils.decorateText(message, player, vars));
    }
}
