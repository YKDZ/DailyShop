package cn.encmys.ykdz.forest.dailyshop.gui;

import cn.encmys.ykdz.forest.dailyshop.DailyShop;
import cn.encmys.ykdz.forest.dailyshop.api.gui.ShopRelatedGUI;
import cn.encmys.ykdz.forest.dailyshop.config.ShopConfig;
import cn.encmys.ykdz.forest.dailyshop.shop.ShopImpl;
import cn.encmys.ykdz.forest.dailyshop.shop.cashier.log.SettlementLog;
import cn.encmys.ykdz.forest.dailyshop.shop.cashier.log.enums.SettlementLogType;
import cn.encmys.ykdz.forest.dailyshop.util.SettlementLogUtils;
import me.clip.placeholderapi.PlaceholderAPI;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import xyz.xenondevs.invui.gui.ScrollGui;
import xyz.xenondevs.invui.gui.structure.Markers;
import xyz.xenondevs.invui.item.Item;
import xyz.xenondevs.invui.window.Window;

import java.util.ArrayList;
import java.util.List;

public class HistoryGUI extends ShopRelatedGUI {
    public HistoryGUI(ShopImpl shop) {
        super(shop);
    }

    @Override
    public void open(@NotNull Player player) {
        List<SettlementLog> logs = DailyShop.DATABASE.queryLogInOrder(shop.getId(), player.getUniqueId(), 365, 100, SettlementLogType.BUY_ALL_FROM, SettlementLogType.BUY_FROM, SettlementLogType.SELL_TO);
        ScrollGui.Builder<Item> builder = buildGUIBuilder();

        for (SettlementLog log : logs) {
            builder.addContent(SettlementLogUtils.toHistoryGuiItem(shop, log, player));
        }

        Window window = Window.single()
                .setGui(builder.build())
                .setViewer(player)
                .setTitle(PlaceholderAPI.setPlaceholders(player, ShopConfig.getHistoryGUITitle(shop.getId())))
                .build();

        window.setCloseHandlers(new ArrayList<>() {{
            add(() -> getWindows().remove(player.getUniqueId()));
        }});

        getWindows().put(player.getUniqueId(), window);
        window.open();
    }

    @Override
    public ScrollGui.Builder<Item>  buildGUIBuilder() {
        String shopId = shop.getId();
        ConfigurationSection section = ShopConfig.getHistoryGuiSection(shopId);

        ScrollGui.Builder<Item>  guiBuilder = ScrollGui.items()
                .setStructure(section.getStringList("layout").toArray(new String[0]));

        if (section.getString("scroll-mode", "HORIZONTAL").equalsIgnoreCase("HORIZONTAL")) {
            guiBuilder.addIngredient(markerIdentifier, Markers.CONTENT_LIST_SLOT_HORIZONTAL);
        } else {
            guiBuilder.addIngredient(markerIdentifier, Markers.CONTENT_LIST_SLOT_VERTICAL);
        }

        // Normal Icon
        ConfigurationSection iconsSection = section.getConfigurationSection("icons");
        if (iconsSection != null) {
            for (String key : iconsSection.getKeys(false)) {
                char iconKey = key.charAt(0);

                guiBuilder.addIngredient(iconKey, buildNormalIcon(iconKey));
            }
        }

        return guiBuilder;
    }
}
