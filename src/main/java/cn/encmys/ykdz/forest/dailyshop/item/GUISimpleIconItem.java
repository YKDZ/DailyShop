package cn.encmys.ykdz.forest.dailyshop.item;

import cn.encmys.ykdz.forest.dailyshop.DailyShop;
import cn.encmys.ykdz.forest.dailyshop.adventure.AdventureManager;
import me.clip.placeholderapi.PlaceholderAPI;
import org.bukkit.Material;
import org.bukkit.configuration.ConfigurationSection;
import xyz.xenondevs.invui.item.builder.ItemBuilder;
import xyz.xenondevs.invui.item.impl.SimpleItem;

public class GUISimpleIconItem extends SimpleItem {
    private static final AdventureManager adventuremanager = DailyShop.getAdventureManager();

    public GUISimpleIconItem(ConfigurationSection section) {
        super(new ItemBuilder(Material.valueOf(section.getString("item", "DIRT").toUpperCase()))
                .setAmount(section.getInt("amount", 1))
                .setDisplayName(adventuremanager.componentToLegacy(adventuremanager.getComponentFromMiniMessage(PlaceholderAPI.setPlaceholders(null, section.getString("name", " "))))));
    }
}
