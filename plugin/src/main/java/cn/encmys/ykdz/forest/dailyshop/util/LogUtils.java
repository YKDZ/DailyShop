package cn.encmys.ykdz.forest.dailyshop.util;

import cn.encmys.ykdz.forest.dailyshop.DailyShop;

public class LogUtils {
    public static void info(String log) {
        DailyShop.ADVENTURE_MANAGER.sendConsoleMessage("[DailyShop] " + log);
    }

    public static void warn(String log) {
        DailyShop.ADVENTURE_MANAGER.sendConsoleMessage("[DailyShop] <yellow>" + log);
    }

    public static void error(String log) {
        DailyShop.ADVENTURE_MANAGER.sendConsoleMessage("[DailyShop] <red>" + log);
    }
}
