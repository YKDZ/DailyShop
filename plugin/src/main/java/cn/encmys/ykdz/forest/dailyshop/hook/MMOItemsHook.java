package cn.encmys.ykdz.forest.dailyshop.hook;

import cn.encmys.ykdz.forest.dailyshop.api.utils.LogUtils;
import org.bukkit.Bukkit;

public class MMOItemsHook {
    public static void load() {
        if (isHooked()) {
            LogUtils.info("Hooked into MMOItems.");
        }
    }

    public static String getIdentifier() {
        return "MI:";
    }

    public static boolean isHooked() {
        return Bukkit.getPluginManager().getPlugin("MMOItems") != null;
    }
}
