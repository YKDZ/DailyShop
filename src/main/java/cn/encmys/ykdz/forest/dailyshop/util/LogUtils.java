package cn.encmys.ykdz.forest.dailyshop.util;

import cn.encmys.ykdz.forest.dailyshop.DailyShop;
import cn.encmys.ykdz.forest.dailyshop.adventure.AdventureManager;

public class LogUtils {
    private final static AdventureManager adventureManager = DailyShop.getAdventureManager();
    private final static DailyShop plugin = DailyShop.getInstance();

    public static void info(String log) {
        adventureManager.sendConsoleMessage("[DailyShop] " + log);
    }

    public static void warn(String log) {
        adventureManager.sendConsoleMessage("[DailyShop] <yellow>" + log);
    }

    public static void error(String log) {
        adventureManager.sendConsoleMessage("[DailyShop] <red>" + log);
    }
}
