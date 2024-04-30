package cn.encmys.ykdz.forest.dailyshop.api.gui;

import cn.encmys.ykdz.forest.dailyshop.builder.BaseItemDecorator;
import cn.encmys.ykdz.forest.dailyshop.config.ShopConfig;
import cn.encmys.ykdz.forest.dailyshop.shop.Shop;
import cn.encmys.ykdz.forest.dailyshop.util.LogUtils;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.event.inventory.ClickType;
import xyz.xenondevs.invui.item.Item;

import java.util.HashMap;
import java.util.List;

public abstract class ShopRelatedGUI extends GUI {
    protected static final char markerIdentifier = 'x';
    protected final Shop shop;

    public ShopRelatedGUI(Shop shop) {
        this.shop = shop;
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

        BaseItemDecorator iconBuilder = BaseItemDecorator.get(item, true);

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

    public int getRowsWithMarker() {
        ConfigurationSection section = ShopConfig.getShopGUISection(shop.getId());
        return (int) section.getStringList("layout").stream().filter(row -> row.contains(Character.toString(markerIdentifier))).count();
    }

    public int getColsWithMarker() {
        ConfigurationSection section = ShopConfig.getShopGUISection(shop.getId());
        List<String> layout = section.getStringList("layout");
        int maxCols = layout.get(0).split(" ").length;
        int[] colCounts = new int[maxCols];

        for (String row : layout) {
            String[] cells = row.split(" ");
            for (int i = 0; i < cells.length; i++) {
                if (cells[i].equals(Character.toString(markerIdentifier))) {
                    colCounts[i]++;
                }
            }
        }

        int colCount = 0;
        for (int count : colCounts) {
            if (count > 0) {
                colCount++;
            }
        }
        return colCount;
    }

    public Shop getShop() {
        return shop;
    }
}
