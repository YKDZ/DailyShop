package cn.encmys.ykdz.forest.dailyshop.shop;

import cn.encmys.ykdz.forest.dailyshop.api.product.Product;
import cn.encmys.ykdz.forest.dailyshop.config.ShopConfig;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;
import xyz.xenondevs.invui.gui.Gui;
import xyz.xenondevs.invui.window.Window;

import java.util.List;
import java.util.Random;

public class Shop {
    private String id;
    private List<Product> products;
    private Gui gui;

    public Shop(String id, int restockTimer, List<Product> products, ConfigurationSection guiSection) {
        this.id = id;
        this.products = products;
        buildGUI(guiSection);
    }

    public void buildGUI(ConfigurationSection guiSection) {
        Gui.Builder.Normal builder = Gui.normal()
                .setStructure(guiSection.getStringList("layout").toArray(new String[0]));
        for(String iconKey : ShopConfig.getIcons(id)) {
            char key = iconKey.charAt(0);
            if(key == '.') {
                continue;
            }
            builder.addIngredient(key, ShopConfig.getIcon(id, key));
        }
        gui = builder
                .addIngredient('.', products.get(new Random().nextInt()).getDisplayedItem())
                .addIngredient('.', products.get(new Random().nextInt()).getDisplayedItem())
                .build();
    }

    public void openGUI(Player player) {
        Window.single()
                .setViewer(player)
                .setTitle(ShopConfig.getTitle(id))
                .setGui(gui)
                .build()
                .open();
    }
}
