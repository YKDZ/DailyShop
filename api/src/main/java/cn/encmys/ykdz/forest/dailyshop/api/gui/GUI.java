package cn.encmys.ykdz.forest.dailyshop.api.gui;

import cn.encmys.ykdz.forest.dailyshop.api.config.record.misc.IconRecord;
import cn.encmys.ykdz.forest.dailyshop.api.gui.enums.GUIContentType;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.Nullable;
import xyz.xenondevs.invui.gui.Gui;
import xyz.xenondevs.invui.gui.PagedGui;
import xyz.xenondevs.invui.gui.ScrollGui;
import xyz.xenondevs.invui.item.Item;
import xyz.xenondevs.invui.window.Window;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public abstract class GUI {
    protected static final char markerIdentifier = 'x';
    protected final Map<UUID, Window> windows = new HashMap<>();
    protected GUIContentType guiContentType;
    protected Gui gui;

    public abstract Item buildNormalIcon(IconRecord record, Player player);

    public Gui build(Player player) {
        if (gui != null) {
            if (guiContentType == GUIContentType.PAGED) {
                ((PagedGui<?>) gui).setPage(0);
            } else if (guiContentType == GUIContentType.SCROLL) {
                ((ScrollGui<?>) gui).setCurrentLine(0);
            }
            return gui;
        } else {
            if (guiContentType == GUIContentType.PAGED) {
                return buildPagedGUI(player);
            } else {
                return buildScrollGUI(player);
            }
        }
    }

    protected abstract Gui buildScrollGUI(Player player);

    protected abstract Gui buildPagedGUI(Player player);

    public void closeAll() {
        for (Window window : getWindows().values()) {
            window.close();
        }
        windows.clear();
    }

    public Map<UUID, Window> getWindows() {
        return windows;
    }

    public abstract void loadContent(@Nullable Player player);

    public GUIContentType getGuiContentType() {
        return guiContentType;
    }

    public Gui getGui() {
        return gui;
    }
}
