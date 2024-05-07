package cn.encmys.ykdz.forest.dailyshop.api.gui;

import cn.encmys.ykdz.forest.dailyshop.api.config.ShopConfig;
import cn.encmys.ykdz.forest.dailyshop.api.shop.Shop;
import org.bukkit.configuration.ConfigurationSection;
import xyz.xenondevs.invui.item.Item;

import java.util.List;

public abstract class ShopRelatedGUI extends GUI {
    protected static final char markerIdentifier = 'x';
    protected final Shop shop;

    public ShopRelatedGUI(Shop shop) {
        this.shop = shop;
    }

    @Override
    public abstract Item buildNormalIcon(char key);

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
