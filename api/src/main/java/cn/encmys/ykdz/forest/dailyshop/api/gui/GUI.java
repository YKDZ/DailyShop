package cn.encmys.ykdz.forest.dailyshop.api.gui;

import cn.encmys.ykdz.forest.dailyshop.api.config.record.shop.IconRecord;
import org.bukkit.entity.Player;
import xyz.xenondevs.invui.gui.Gui;
import xyz.xenondevs.invui.item.Item;
import xyz.xenondevs.invui.window.Window;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public abstract class GUI {
    protected static final char markerIdentifier = 'x';

    protected final Map<UUID, Window> windows = new HashMap<>();

    public abstract Item buildNormalIcon(IconRecord record, Player player);

    public abstract Gui buildGUI(Player player);

    public void closeAll() {
        for (Window window : getWindows().values()) {
            window.close();
        }
        windows.clear();
    }

    public Map<UUID, Window> getWindows() {
        return windows;
    }

    public abstract int getLayoutContentSlotAmount();

    public abstract int getLayoutContentSlotLineAmount();
}
