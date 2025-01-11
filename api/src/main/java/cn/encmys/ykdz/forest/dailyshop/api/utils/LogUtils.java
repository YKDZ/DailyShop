package cn.encmys.ykdz.forest.dailyshop.api.utils;

import cn.encmys.ykdz.forest.hyphautils.HyphaLogUtils;
import org.jetbrains.annotations.NotNull;

public class LogUtils {
    public static void info(@NotNull String log) {
        HyphaLogUtils.info("[DailyShop]", log);
    }

    public static void warn(@NotNull String log) {
        HyphaLogUtils.warn("[DailyShop]", log);
    }

    public static void error(@NotNull String log) {
        HyphaLogUtils.error("[DailyShop]", log);
    }
}
