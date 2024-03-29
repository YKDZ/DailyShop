package cn.encmys.ykdz.forest.dailyshop.hook;

import cn.encmys.ykdz.forest.dailyshop.util.LogUtils;
import org.bukkit.Bukkit;

public class NeigeItemsHook {
    public NeigeItemsHook() {
        if (isHooked()) {
            LogUtils.info("Hooked into NeigeItemsItem.");
        }
    }

    public static String getIdentifier() {
        return "NI:";
    }

    public static boolean isHooked() {
        return Bukkit.getPluginManager().getPlugin("NeigeItemsItem") != null;
    }
}
