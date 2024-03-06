package cn.encmys.ykdz.forest.dailyshop.shop;

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

import java.util.List;

public class Shop {
    private final String id;
    private final List<Product> products;
    private Gui gui;
    private int size;
    private List<Product> listedProducts;

    /**
     * @param id Shop id
     * @param restockTimer Shop restock time in second
     * @param products Shop products list
     * @param size Maximum number of items in the shop at the same time
     * @param guiSection Shop gui configuration section
     */
    public Shop(String id, int restockTimer, List<Product> products, int size, ConfigurationSection guiSection) {
        this.id = id;
        this.products = products;
        this.size = size;
        restock();
        buildGUI(guiSection);
    }

    public void buildGUI(@NotNull ConfigurationSection guiSection) {
        ScrollGui.Builder<@NotNull Item> builder = ScrollGui.items()
                .setStructure(guiSection.getStringList("layout").toArray(new String[0]))
                .addIngredient('.', Markers.CONTENT_LIST_SLOT_HORIZONTAL);

        for (String iconKey : ShopConfig.getIcons(id)) {
            char key = iconKey.charAt(0);
            if (key == '.') {
                continue;
            }
            builder.addIngredient(key, ShopConfig.getIcon(id, key));
        }

        for(Product product : listedProducts) {
            builder.addContent(product.getGUIItem());
        }

        gui = builder.build();
    }

    public void openGUI(Player player) {
        Window.single()
                .setViewer(player)
                .setTitle(ShopConfig.getTitle(id))
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
    }

    public int getTotalWeight() {
        int sumWeight = 0;
        for(Product product : products) {
            sumWeight += product.getRarity().getWeight();
        }
        return sumWeight;
    }
}
