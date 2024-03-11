package cn.encmys.ykdz.forest.dailyshop.util;

import cn.encmys.ykdz.forest.dailyshop.gui.icon.AutoUpdateIcon;
import cn.encmys.ykdz.forest.dailyshop.gui.icon.CommandIcon;
import cn.encmys.ykdz.forest.dailyshop.gui.icon.SimpleIcon;
import org.bukkit.configuration.ConfigurationSection;
import xyz.xenondevs.invui.item.Item;

public class GUIIconUtils {
    public static Item getGUIIcon(ConfigurationSection section) {
        if (section.contains("update-timer")) {
            return new AutoUpdateIcon(section);
        } else if (section.contains("command")) {
            return new CommandIcon(section);
        } else {
            return new SimpleIcon(section);
        }
    }
}
