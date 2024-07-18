package cn.encmys.ykdz.forest.dailyshop.hook;

import cn.encmys.ykdz.forest.dailyshop.api.utils.LogUtils;
import org.bukkit.Bukkit;

public class ItemsAdderHook {
    public ItemsAdderHook() {
        if (isHooked()) {
            LogUtils.info("Hooked into ItemsAdder.");
        }
    }

    public static String getIdentifier() {
        return "IA:";
    }

    public static boolean isHooked() {
        return Bukkit.getPluginManager().getPlugin("ItemsAdder") != null;
    }
}
