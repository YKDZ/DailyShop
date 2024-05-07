package cn.encmys.ykdz.forest.dailyshop.api.gui;

import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

public abstract class PlayerRelatedGUI {
    protected final Player player;

    protected PlayerRelatedGUI(@NotNull Player player) {
        this.player = player;
    }
}
