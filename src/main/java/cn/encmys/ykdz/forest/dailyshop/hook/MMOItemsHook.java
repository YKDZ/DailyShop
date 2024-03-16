package cn.encmys.ykdz.forest.dailyshop.hook;

import cn.encmys.ykdz.forest.dailyshop.util.LogUtils;
import net.Indyuce.mmoitems.MMOItems;
import net.Indyuce.mmoitems.api.Type;
import net.Indyuce.mmoitems.api.item.mmoitem.MMOItem;
import net.Indyuce.mmoitems.api.player.PlayerData;
import net.Indyuce.mmoitems.stat.type.ItemStat;
import net.Indyuce.mmoitems.stat.type.NameData;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.Locale;

public class MMOItemsHook {
    public MMOItemsHook() {
        if (isHooked()) {
            LogUtils.info("Hooked into MMOItems.");
        }
    }

    public static boolean isHooked() {
        return Bukkit.getPluginManager().getPlugin("MMOItems") != null;
    }

    public static ItemStack buildItem(String type, String id) {
        MMOItem mmoItem = MMOItems.plugin.getMMOItem(Type.get(type), id.toUpperCase(Locale.ENGLISH));
        return mmoItem == null ? new ItemStack(Material.AIR) : mmoItem.newBuilder().build();
    }

    public static ItemStack buildItem(Player player, String type, String id) {
        return MMOItems.plugin.getItem(Type.get(type), id, PlayerData.get(player));
    }

    public static boolean hasItem(Player player, String type, String id, int amount) {
        int hasAmount = 0;
        for (ItemStack item : player.getInventory()) {
            String itemType = MMOItems.getTypeName(item);
            String itemId = MMOItems.getID(item);
            if (itemType != null && itemId != null && itemType.equalsIgnoreCase(type) && itemId.equalsIgnoreCase(id)) {
                if (item.getAmount() >= amount || hasAmount > amount) {
                    return true;
                }
                hasAmount += item.getAmount();
            }
        }
        return false;
    }

    public static void takeItem(Player player, String type, String id, int amount) {
        for (ItemStack item : player.getInventory()) {
            String itemType = MMOItems.getTypeName(item);
            String itemId = MMOItems.getID(item);
            if (amount > 0 && itemType != null && itemId != null && itemType.equalsIgnoreCase(type) && itemId.equalsIgnoreCase(id)) {
                int stackAmount = item.getAmount();
                if (stackAmount < amount) {
                    item.setAmount(0);
                    amount -= stackAmount;
                } else {
                    item.setAmount(stackAmount - amount);
                }
                amount -= stackAmount;
            }
        }
    }

    public static String getDisplayName(String type, String id) {
        MMOItem mmoItem = MMOItems.plugin.getMMOItem(Type.get(type), id.toUpperCase(Locale.ENGLISH));

        if (mmoItem == null) {
            return null;
        }

        for (ItemStat stat : mmoItem.getStats()) {
            if (stat.getId().equals("NAME")) {
                return ((NameData) mmoItem.getData(stat)).getMainName();
            }
        }

        return null;
    }
}
