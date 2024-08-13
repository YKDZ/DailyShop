package cn.encmys.ykdz.forest.dailyshop.api.gui;

import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

public abstract class PlayerRelatedGUI extends GUI {
    @NotNull
    protected final Player player;

    public PlayerRelatedGUI(Player player) {
        this.player = player;
    }

    public Player getPlayer() {
        return player;
    }

    public abstract void open();

    public void close() {
        getWindows().get(player.getUniqueId()).close();
    }
}
