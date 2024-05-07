package cn.encmys.ykdz.forest.dailyshop.hook;

import cn.encmys.ykdz.forest.dailyshop.util.LogUtils;
import org.bukkit.Bukkit;

public class OraxenHook {
    public OraxenHook() {
        if (isHooked()) {
            LogUtils.info("Hooked into Oraxen.");
        }
    }

    public static String getIdentifier() {
        return "OXN:";
    }

    public static boolean isHooked() {
        return Bukkit.getPluginManager().getPlugin("Oraxen") != null;
    }
}
