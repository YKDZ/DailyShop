package cn.encmys.ykdz.forest.dailyshop.shop;

import cn.encmys.ykdz.forest.dailyshop.DailyShop;
import cn.encmys.ykdz.forest.dailyshop.api.product.Product;
import cn.encmys.ykdz.forest.dailyshop.config.ShopConfig;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import xyz.xenondevs.invui.gui.Gui;
import xyz.xenondevs.invui.gui.ScrollGui;
import xyz.xenondevs.invui.gui.structure.Markers;
import xyz.xenondevs.invui.item.Item;
import xyz.xenondevs.invui.window.Window;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Shop {
    private final String id;
    private final String name;
    private final List<Product> products;
    private final ConfigurationSection guiSection;
    private Gui gui;
    private final int size;
    private final List<Product> listedProducts = new ArrayList<>();
    private long lastRestocking;

    /**
     * @param id Shop id
     * @param restockTimer Shop restock time in second
     * @param products Shop products list
     * @param size Maximum number of items in the shop at the same time
     * @param guiSection Shop gui configuration section
     */
    public Shop(String id, String name, int restockTimer, List<Product> products, int size, ConfigurationSection guiSection) {
        this.id = id;
        this.name = name;
        this.products = products;
        this.size = size;
        this.guiSection = guiSection;
        loadData();
        buildGUI(guiSection);
    }

    public void buildGUI(@NotNull ConfigurationSection guiSection) {
        ScrollGui.Builder<@NotNull Item> builder = ScrollGui.items()
                .setStructure(guiSection.getStringList("layout").toArray(new String[0]))
                .addIngredient('.', Markers.CONTENT_LIST_SLOT_HORIZONTAL);

        for (String iconKey : ShopConfig.getGUIIcons(id)) {
            char key = iconKey.charAt(0);
            if (key == '.') {
                continue;
            }
            builder.addIngredient(key, ShopConfig.getGUIIcon(id, key));
        }

        for(Product product : listedProducts) {
            builder.addContent(product.getGUIItem());
        }

        gui = builder.build();
    }

    public void openGUI(Player player) {
        Window.single()
                .setViewer(player)
                .setTitle(ShopConfig.getGUITitle(id))
                .setGui(gui)
                .build()
                .open();
    }

    public void restock() {
        double[] randoms = new double[size];
        for (int i = 0; i < size; i++) {
            randoms[i] = Math.random() * getTotalWeight();
        }
        listedProducts.clear();
        for (int i = 0; i < size; i++) {
            int tempWeight = 0;
            for (Product product : products) {
                tempWeight += product.getRarity().getWeight();
                if (tempWeight >= randoms[i]) {
                    listedProducts.add(product);
                    break;
                }
            }
        }
        buildGUI(guiSection);
        lastRestocking = System.currentTimeMillis();
    }

    public int getTotalWeight() {
        int sumWeight = 0;
        for(Product product : products) {
            sumWeight += product.getRarity().getWeight();
        }
        return sumWeight;
    }

    public void loadData() {
        List<String> listedProductsId = DailyShop.getDatabase().loadShopData().get(id);
        if(listedProductsId == null) {
            restock();
        } else {
            for(String productId : listedProductsId) {
                listedProducts.add(DailyShop.getProductFactory().getProduct(productId));
            }
        }
    }

    public void saveData() {
        Map<String, List<String>> dataMap = new HashMap<>();
        List<String> listedProductsId = new ArrayList<>();
        for(Product product : listedProducts) {
            listedProductsId.add(product.getId());
        }
        dataMap.put(id, listedProductsId);
        DailyShop.getDatabase().saveShopData(dataMap);
    }

    public long getLastRestocking() {
        return lastRestocking;
    }

    public String getName() {
        return name;
    }
}
