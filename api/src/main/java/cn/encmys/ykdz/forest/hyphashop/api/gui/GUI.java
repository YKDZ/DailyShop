package cn.encmys.ykdz.forest.hyphashop.api.gui;

import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

public abstract class GUI {
    protected static final char markerIdentifier = 'x';

    public abstract void open(@NotNull Player player);

    public abstract void closeAll();

    public abstract void close(@NotNull Player player);

    public void loadContent(@NotNull Player player) {
    }
}
