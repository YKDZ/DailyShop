package cn.encmys.ykdz.forest.dailyshop.api.utils;

import cn.encmys.ykdz.forest.dailyshop.api.DailyShop;
import cn.encmys.ykdz.forest.dailyshop.api.config.CartGUIConfig;
import cn.encmys.ykdz.forest.dailyshop.api.config.ShopConfig;
import cn.encmys.ykdz.forest.dailyshop.api.config.record.misc.SoundRecord;
import cn.encmys.ykdz.forest.dailyshop.api.shop.Shop;
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

    public static void playSound(Shop shop, Player player, String soundKey) {
        SoundRecord sound = ShopConfig.getSoundRecord(shop.getId(), soundKey);
        if (sound != null) {
            player.playSound(player.getLocation(), sound.sound(), sound.volume(), sound.pitch());
        }
    }

    public static void playCartGUISound(Player player, String soundKey) {
        SoundRecord sound = CartGUIConfig.getSoundRecord(soundKey);
        if (sound != null) {
            player.playSound(player.getLocation(), sound.sound(), sound.volume(), sound.pitch());
        }
    }

    public static void sendMessage(String message, Player player, Map<String, String> vars) {
        DailyShop.ADVENTURE_MANAGER.sendMessageWithPrefix(player, TextUtils.decorateTextKeepMiniMessage(message, player, vars));
    }
}
