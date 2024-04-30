package cn.encmys.ykdz.forest.dailyshop.gui;

import cn.encmys.ykdz.forest.dailyshop.DailyShop;
import cn.encmys.ykdz.forest.dailyshop.api.gui.ShopRelatedGUI;
import cn.encmys.ykdz.forest.dailyshop.api.product.Product;
import cn.encmys.ykdz.forest.dailyshop.config.ShopConfig;
import cn.encmys.ykdz.forest.dailyshop.shop.Shop;
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

public class ShopGUI extends ShopRelatedGUI {
    public ShopGUI(Shop shop) {
        super(shop);
    }

    @Override
    public ScrollGui.Builder<Item> buildGUIBuilder() {
        String shopId = shop.getId();
        List<String> listedProduct = shop.getListedProducts();
        ConfigurationSection section = ShopConfig.getShopGUISection(shopId);

        ScrollGui.Builder<Item> guiBuilder = ScrollGui.items()
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

        // Product Icon
        for (String productId : listedProduct) {
            Product product = DailyShop.PRODUCT_FACTORY.getProduct(productId);
            guiBuilder.addContent(product.getIconBuilder().buildProductIcon(shopId, product));
        }

        return guiBuilder;
    }

    @Override
    public void open(@NotNull Player player) {
        Window window = Window.single()
                .setGui(buildGUIBuilder().build())
                .setTitle(PlaceholderAPI.setPlaceholders(player, ShopConfig.getShopGUITitle(shop.getId())))
                .setCloseHandlers(new ArrayList<>() {{
                    add(() -> getWindows().remove(player.getUniqueId()));
                }})
                .build(player);

        window.open();

        getWindows().put(player.getUniqueId(), window);
    }
}
