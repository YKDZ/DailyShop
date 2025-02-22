package cn.encmys.ykdz.forest.hyphashop.hook;

import cn.encmys.ykdz.forest.hyphashop.utils.LogUtils;
import cn.encmys.ykdz.forest.hyphautils.HyphaPluginUtils;

public class PlaceholderAPIHook {
    public static void load() {
        if (isHooked()) {
            (new PlaceholderExpansion()).register();
            LogUtils.info("Hooked into PlaceholderAPI.");
        }
    }

    public static boolean isHooked() {
        return HyphaPluginUtils.isExist("PlaceholderAPI");
    }
}
