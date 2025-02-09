package cn.encmys.ykdz.forest.dailyshop.api.gui;

import cn.encmys.ykdz.forest.dailyshop.api.config.ShopConfig;
import cn.encmys.ykdz.forest.dailyshop.api.config.record.gui.ShopGUIRecord;
import cn.encmys.ykdz.forest.dailyshop.api.utils.TextUtils;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import xyz.xenondevs.invui.window.Window;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public abstract class GUI {
    protected static final char markerIdentifier = 'x';
    protected final static Map<UUID, Window> windows = new HashMap<>();

    public abstract void open(@NotNull Player player);

    public static Map<UUID, Window> getWindows() {
        return Collections.unmodifiableMap(windows);
    }

    public static void closeAll() {
        windows.values().forEach(Window::close);
        windows.clear();
    }

    public abstract void loadContent(@Nullable Player player);
}
