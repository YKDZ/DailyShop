package cn.encmys.ykdz.forest.dailyshop.gui.icon;

import cn.encmys.ykdz.forest.dailyshop.DailyShop;
import cn.encmys.ykdz.forest.dailyshop.adventure.AdventureManager;
import cn.encmys.ykdz.forest.dailyshop.api.gui.icon.Icon;
import cn.encmys.ykdz.forest.dailyshop.util.TextUtils;
import org.bukkit.Material;
import org.bukkit.configuration.ConfigurationSection;
import xyz.xenondevs.invui.item.builder.ItemBuilder;
import xyz.xenondevs.invui.item.impl.AutoUpdateItem;

public class AutoUpdateIcon extends AutoUpdateItem implements Icon {
    private static final AdventureManager adventuremanager = DailyShop.getAdventureManager();

    public AutoUpdateIcon(ConfigurationSection section) {
        super(section.getInt("update-timer", 20), () -> {
            Material material = Material.valueOf(section.getString("item", "DIRT").toUpperCase());
            return new ItemBuilder(material)
                    .setAmount(section.getInt("amount", 1))
                    .setDisplayName(TextUtils.decorateText(section.getString("name", " "), null));
        });
        start();
    }
}
