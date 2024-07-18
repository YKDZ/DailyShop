package cn.encmys.ykdz.forest.dailyshop.hook;

import cn.encmys.ykdz.forest.dailyshop.api.utils.LogUtils;
import org.bukkit.Bukkit;

public class MythicMobsHook {

    public MythicMobsHook() {
        if (isHooked()) {
            LogUtils.info("Hooked into MythicMobs.");
        }
    }

    public static String getIdentifier() {
        return "MM:";
    }

    public static boolean isHooked() {
        return Bukkit.getPluginManager().getPlugin("MythicMobs") != null;
    }
}
