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
    protected final Map<UUID, Window> windows = new HashMap<>();

    public abstract Item buildNormalIcon(IconRecord record);

    public abstract void open(@NotNull Player player);

    @NotNull
    public abstract ScrollGui.Builder<Item> buildGUIBuilder(Player player);

    public void closeAll() {
        for (Window window : getWindows().values()) {
            window.close();
        }
        windows.clear();
    }

    public void close(Player player) {
        UUID uuid = player.getUniqueId();
        getWindows().get(uuid).close();
        getWindows().remove(uuid);
    }

    public Map<UUID, Window> getWindows() {
        return windows;
    }
}
