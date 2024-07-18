package cn.encmys.ykdz.forest.dailyshop.api.utils;

import cn.encmys.ykdz.forest.dailyshop.api.DailyShop;

import java.util.logging.Logger;

public class LogUtils {
    private final static Logger logger = Logger.getLogger(DailyShop.class.getName());

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
