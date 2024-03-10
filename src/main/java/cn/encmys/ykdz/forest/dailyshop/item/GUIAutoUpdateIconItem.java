package cn.encmys.ykdz.forest.dailyshop.item;

import cn.encmys.ykdz.forest.dailyshop.DailyShop;
import cn.encmys.ykdz.forest.dailyshop.adventure.AdventureManager;
import me.clip.placeholderapi.PlaceholderAPI;
import org.bukkit.Material;
import org.bukkit.configuration.ConfigurationSection;
import xyz.xenondevs.invui.item.builder.ItemBuilder;
import xyz.xenondevs.invui.item.impl.AutoUpdateItem;

public class GUIAutoUpdateIconItem extends AutoUpdateItem {
    private static final AdventureManager adventuremanager = DailyShop.getAdventureManager();

    public GUIAutoUpdateIconItem(ConfigurationSection section) {
        super(section.getInt("update-timer", 20), () -> {
            Material material = Material.valueOf(section.getString("item", "DIRT").toUpperCase());
            return new ItemBuilder(material)
                    .setAmount(section.getInt("amount", 1))
                    .setDisplayName(adventuremanager.componentToLegacy(adventuremanager.getComponentFromMiniMessage(PlaceholderAPI.setPlaceholders(null, section.getString("name", " ")))));
        });
        start();
    }
}
