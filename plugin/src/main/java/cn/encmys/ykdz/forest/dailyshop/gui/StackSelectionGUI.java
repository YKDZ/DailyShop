package cn.encmys.ykdz.forest.dailyshop.gui;

import cn.encmys.ykdz.forest.dailyshop.api.gui.GUI;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import xyz.xenondevs.invui.gui.ScrollGui;
import xyz.xenondevs.invui.item.Item;

public class StackSelectionGUI extends GUI {
    @Override
    public Item buildNormalIcon(char key, ConfigurationSection iconSection) {
        return null;
    }

    @Override
    public void open(@NotNull Player player) {

    }

    @Override
    public ScrollGui.Builder<Item> buildGUIBuilder(Player player) {
        return null;
    }
}
