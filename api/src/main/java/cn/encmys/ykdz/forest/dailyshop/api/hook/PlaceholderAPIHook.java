package cn.encmys.ykdz.forest.dailyshop.api.hook;

import org.bukkit.Bukkit;

public class PlaceholderAPIHook {
    public static boolean isHooked() {
        return Bukkit.getPluginManager().getPlugin("PlaceholderAPI") != null;
    }
}
