package cn.encmys.ykdz.forest.dailyshop.gui;

import cn.encmys.ykdz.forest.dailyshop.api.DailyShop;
import cn.encmys.ykdz.forest.dailyshop.api.config.ShopConfig;
import cn.encmys.ykdz.forest.dailyshop.api.gui.ShopRelatedGUI;
import cn.encmys.ykdz.forest.dailyshop.api.item.decorator.BaseItemDecorator;
import cn.encmys.ykdz.forest.dailyshop.api.product.Product;
import cn.encmys.ykdz.forest.dailyshop.api.shop.Shop;
import cn.encmys.ykdz.forest.dailyshop.api.utils.LogUtils;
import cn.encmys.ykdz.forest.dailyshop.builder.NormalIconBuilder;
import cn.encmys.ykdz.forest.dailyshop.builder.ProductIconBuilder;
import cn.encmys.ykdz.forest.dailyshop.hook.PlaceholderAPIHook;
import cn.encmys.ykdz.forest.dailyshop.item.decorator.BaseItemDecoratorImpl;
import me.clip.placeholderapi.PlaceholderAPI;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
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
    public ScrollGui.Builder<Item> buildGUIBuilder(@Nullable Player player) {
        String shopId = shop.getId();
        List<String> listedProduct = shop.getShopStocker().getListedProducts();
        ConfigurationSection section = ShopConfig.getShopGUISection(shopId);

        ScrollGui.Builder<Item> guiBuilder = ScrollGui.items()
                .setStructure(section.getStringList("layout").toArray(new String[0]));

        if (section.getString("scroll-mode", "HORIZONTAL").equalsIgnoreCase("HORIZONTAL")) {
            guiBuilder.addIngredient(markerIdentifier, Markers.CONTENT_LIST_SLOT_HORIZONTAL);
        } else {
            guiBuilder.addIngredient(markerIdentifier, Markers.CONTENT_LIST_SLOT_VERTICAL);
        }

        // 普通图标
        ConfigurationSection iconsSection = section.getConfigurationSection("icons");
        if (iconsSection != null) {
            for (String key : iconsSection.getKeys(false)) {
                char iconKey = key.charAt(0);
                ConfigurationSection iconSection = iconsSection.getConfigurationSection(key);
                guiBuilder.addIngredient(iconKey, buildNormalIcon(iconKey, iconSection));
            }
        }

        // 商品图标
        for (String productId : listedProduct) {
            Product product = DailyShop.PRODUCT_FACTORY.getProduct(productId);
            guiBuilder.addContent(ProductIconBuilder.build(product.getIconDecorator(), player, shopId, product));
        }

        return guiBuilder;
    }

    @Override
    public void open(@NotNull Player player) {
        if (getGui() == null) {
            setGui(buildGUIBuilder(player).build());
        }

        String title = PlaceholderAPIHook.isHooked() ? PlaceholderAPI.setPlaceholders(player, ShopConfig.getShopGUITitle(shop.getId())) : ShopConfig.getShopGUITitle(shop.getId());
        Window window = Window.single()
                .setGui(getGui())
                .setTitle(title)
                .setCloseHandlers(new ArrayList<>() {{
                    add(() -> getWindows().remove(player.getUniqueId()));
                }})
                .build(player);

        window.open();

        getWindows().put(player.getUniqueId(), window);
    }

    @Override
    public Item buildNormalIcon(char key, ConfigurationSection iconSection) {
        BaseItemDecorator decorator = BaseItemDecoratorImpl.get(ShopConfig.getIconRecord(key, iconSection), false);
        if (decorator == null) {
            LogUtils.warn("Icon shop-gui.icons." + key + " in shop " + shop.getId() + " has invalid base setting. Please check it.");
            return null;
        }
        return NormalIconBuilder.build(decorator);
    }
}
