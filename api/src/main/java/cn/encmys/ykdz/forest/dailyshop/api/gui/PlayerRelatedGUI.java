package cn.encmys.ykdz.forest.dailyshop.api.gui;

import org.bukkit.entity.Player;

public abstract class PlayerRelatedGUI extends GUI {
    protected final Player player;

    public PlayerRelatedGUI(Player player) {
        this.player = player;
    }

    public Player getPlayer() {
        return player;
    }

    public abstract void open();

    public abstract void close();
}
