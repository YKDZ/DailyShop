package cn.encmys.ykdz.forest.dailyshop.api.gui;

import cn.encmys.ykdz.forest.dailyshop.api.config.record.shop.IconRecord;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import xyz.xenondevs.invui.gui.ScrollGui;
import xyz.xenondevs.invui.item.Item;
import xyz.xenondevs.invui.window.Window;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public abstract class GUI {
    protected static final char markerIdentifier = 'x';

    protected final Map<UUID, Window> windows = new HashMap<>();

    public abstract Item buildNormalIcon(IconRecord record, Player player);

    @NotNull
    public abstract ScrollGui.Builder<Item> buildGUIBuilder(Player player);

    public void closeAll() {
        for (Window window : getWindows().values()) {
            window.close();
        }
        windows.clear();
    }

    public void close(Player player) {
        Window window = getWindows().get(player.getUniqueId());
        if (window != null) {
            window.close();
        }
    }

    public Map<UUID, Window> getWindows() {
        return windows;
    }
}
