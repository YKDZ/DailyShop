package cn.encmys.ykdz.forest.dailyshop.gui;

import cn.encmys.ykdz.forest.dailyshop.api.DailyShop;
import cn.encmys.ykdz.forest.dailyshop.api.builder.BaseItemDecorator;
import cn.encmys.ykdz.forest.dailyshop.api.config.ShopConfig;
import cn.encmys.ykdz.forest.dailyshop.api.gui.ShopRelatedGUI;
import cn.encmys.ykdz.forest.dailyshop.api.shop.Shop;
import cn.encmys.ykdz.forest.dailyshop.api.shop.cashier.log.SettlementLog;
import cn.encmys.ykdz.forest.dailyshop.api.shop.cashier.log.enums.SettlementLogType;
import cn.encmys.ykdz.forest.dailyshop.builder.BaseItemDecoratorImpl;
import cn.encmys.ykdz.forest.dailyshop.util.LogUtils;
import cn.encmys.ykdz.forest.dailyshop.util.SettlementLogUtils;
import me.clip.placeholderapi.PlaceholderAPI;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.jetbrains.annotations.NotNull;
import xyz.xenondevs.invui.gui.ScrollGui;
import xyz.xenondevs.invui.gui.structure.Markers;
import xyz.xenondevs.invui.item.Item;
import xyz.xenondevs.invui.window.Window;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class HistoryGUI extends ShopRelatedGUI {
    public HistoryGUI(Shop shop) {
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

    @Override
    public Item buildNormalIcon(char key) {
        String shopId = shop.getId();
        ConfigurationSection section = ShopConfig.getShopGUISection(shopId);
        ConfigurationSection iconsSection = section.getConfigurationSection("icons");

        if (iconsSection == null) {
            return null;
        }

        ConfigurationSection icon = iconsSection.getConfigurationSection(String.valueOf(key));

        if (icon == null) {
            return null;
        }

        int scrollShift;
        if (section.getString("scroll-mode", "HORIZONTAL").equalsIgnoreCase("HORIZONTAL")) {
            scrollShift = getRowsWithMarker();
        } else {
            scrollShift = getColsWithMarker();
        }

        String item = icon.getString("item", "DIRT");

        BaseItemDecorator iconBuilder = BaseItemDecoratorImpl.get(item, true);

        if (iconBuilder == null) {
            LogUtils.warn("Normal icon " + key + " in shop " + shopId + " has invalid base setting. Please check it.");
        } else {
            return iconBuilder
                    .setScrollShift(scrollShift)
                    .setAmount(icon.getInt("amount", 1))
                    .setName(icon.getString("name", null))
                    .setLore(icon.getStringList("lore"))
                    .setPeriod(icon.getLong("update-timer", 0L))
                    .setScroll(icon.getInt("scroll", 0))
                    .setCommands(new HashMap<>() {{
                        put(ClickType.LEFT, icon.getStringList("commands.left"));
                        put(ClickType.RIGHT, icon.getStringList("commands.right"));
                        put(ClickType.SHIFT_LEFT, icon.getStringList("commands.shift-left"));
                        put(ClickType.SHIFT_RIGHT, icon.getStringList("commands.shift-right"));
                        put(ClickType.DROP, icon.getStringList("commands.drop"));
                        put(ClickType.DOUBLE_CLICK, icon.getStringList("commands.double-click"));
                        put(ClickType.MIDDLE, icon.getStringList("commands.middle"));
                    }})
                    .setCustomModelData(icon.getInt("custom-model-data"))
                    .setItemFlags(icon.getStringList("item-flags"))
                    .setPatternsData(icon.getStringList("banner-patterns"))
                    .buildNormalIcon();
        }
        return null;
    }
}
