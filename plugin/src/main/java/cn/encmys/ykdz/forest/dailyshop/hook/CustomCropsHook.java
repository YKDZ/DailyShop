package cn.encmys.ykdz.forest.dailyshop.hook;

import cn.encmys.ykdz.forest.dailyshop.util.LogUtils;
import org.bukkit.Bukkit;

public class CustomCropsHook {
    public CustomCropsHook() {
        if (isHooked()) {
            LogUtils.info("Hooked into CustomCrops.");
        }
    }

    public static String getIdentifier() {
        return "CC:";
    }

    public static boolean isHooked() {
        return Bukkit.getPluginManager().getPlugin("CustomCrops") != null;
    }
}
