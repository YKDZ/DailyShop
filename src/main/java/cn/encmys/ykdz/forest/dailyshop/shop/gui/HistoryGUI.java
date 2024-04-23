package cn.encmys.ykdz.forest.dailyshop.shop.gui;

import cn.encmys.ykdz.forest.dailyshop.DailyShop;
import cn.encmys.ykdz.forest.dailyshop.product.factory.ProductFactory;
import org.bukkit.configuration.ConfigurationSection;
import xyz.xenondevs.invui.gui.Gui;
import xyz.xenondevs.invui.window.Window;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class HistoryGUI {
    private static final ProductFactory productFactory = DailyShop.getProductFactory();
    private static final char productIdentifier = 'x';
    private final Map<UUID, Window> windows = new HashMap<>();
    private final String shopId;
    private final ConfigurationSection section;
    private Gui gui;

    public HistoryGUI(String shopId, ConfigurationSection section) {
        this.shopId = shopId;
        this.section = section;
    }
}
