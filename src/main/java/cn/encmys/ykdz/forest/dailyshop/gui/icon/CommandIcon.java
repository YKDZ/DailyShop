package cn.encmys.ykdz.forest.dailyshop.gui.icon;

import cn.encmys.ykdz.forest.dailyshop.DailyShop;
import cn.encmys.ykdz.forest.dailyshop.adventure.AdventureManager;
import cn.encmys.ykdz.forest.dailyshop.api.gui.icon.Icon;
import cn.encmys.ykdz.forest.dailyshop.util.TextUtils;
import org.bukkit.Material;
import org.bukkit.configuration.ConfigurationSection;
import xyz.xenondevs.invui.item.builder.ItemBuilder;
import xyz.xenondevs.invui.item.impl.CommandItem;

public class CommandIcon extends CommandItem implements Icon {
    private static final AdventureManager adventuremanager = DailyShop.getAdventureManager();

    public CommandIcon(ConfigurationSection section) {
        super(new ItemBuilder(Material.valueOf(section.getString("item", "DIRT").toUpperCase()))
                .setAmount(section.getInt("amount", 1))
                .setDisplayName(TextUtils.decorateText(section.getString("name", " "), null))
                , section.getString("command", "/"));
    }
}
