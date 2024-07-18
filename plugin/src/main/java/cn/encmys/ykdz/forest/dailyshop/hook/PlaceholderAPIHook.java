package cn.encmys.ykdz.forest.dailyshop.hook;

import cn.encmys.ykdz.forest.dailyshop.util.LogUtils;
import org.bukkit.Bukkit;

public class PlaceholderAPIHook {
    public PlaceholderAPIHook() {
        if (isHooked()) {
            (new PlaceholderExpansion()).register();
            LogUtils.info("Hooked into PlaceholderAPI.");
        }
    }

    public static boolean isHooked() {
        return Bukkit.getPluginManager().getPlugin("PlaceholderAPI") != null;
    }
}
