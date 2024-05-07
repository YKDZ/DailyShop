package cn.encmys.ykdz.forest.dailyshop.hook;

import cn.encmys.ykdz.forest.dailyshop.util.LogUtils;
import org.bukkit.Bukkit;

public class CustomFishingHook {
    public CustomFishingHook() {
        if (isHooked()) {
            LogUtils.info("Hooked into CustomFishing.");
        }
    }

    public static String getIdentifier() {
        return "CF:";
    }

    public static boolean isHooked() {
        return Bukkit.getPluginManager().getPlugin("CustomFishing") != null;
    }
}
