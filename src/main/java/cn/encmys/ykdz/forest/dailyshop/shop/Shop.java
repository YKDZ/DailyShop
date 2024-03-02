package cn.encmys.ykdz.forest.dailyshop.shop;

import cn.encmys.ykdz.forest.dailyshop.api.product.Product;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;
import xyz.xenondevs.invui.gui.Gui;

import java.util.List;

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

    }
}
