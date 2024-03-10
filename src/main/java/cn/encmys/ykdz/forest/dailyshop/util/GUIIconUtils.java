package cn.encmys.ykdz.forest.dailyshop.util;

import cn.encmys.ykdz.forest.dailyshop.item.GUIAutoUpdateIconItem;
import cn.encmys.ykdz.forest.dailyshop.item.GUICommandIconItem;
import cn.encmys.ykdz.forest.dailyshop.item.GUISimpleIconItem;
import org.bukkit.configuration.ConfigurationSection;
import xyz.xenondevs.invui.item.Item;

public class GUIIconUtils {
    public static Item getGUIIcon(ConfigurationSection section) {
        if (section.contains("update-timer")) {
            return new GUIAutoUpdateIconItem(section);
        } else if (section.contains("command")) {
            return new GUICommandIconItem(section);
        } else {
            return new GUISimpleIconItem(section);
        }
    }
}
