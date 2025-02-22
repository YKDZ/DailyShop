package cn.encmys.ykdz.forest.hyphashop.utils;

import cn.encmys.ykdz.forest.hyphashop.config.Config;
import cn.encmys.ykdz.forest.hyphautils.HyphaLogUtils;
import org.jetbrains.annotations.NotNull;

public class LogUtils {
    public static void info(@NotNull String log) {
        HyphaLogUtils.info("[HyphaShop]", log);
    }

    public static void warn(@NotNull String log) {
        HyphaLogUtils.warn("[HyphaShop]", log);
    }

    public static void error(@NotNull String log) {
        HyphaLogUtils.error("[HyphaShop]", log);
    }

    public static void debug(@NotNull String log) {
        if (Config.debug) HyphaLogUtils.warn("[HyphaShop]", log);
    }
}
