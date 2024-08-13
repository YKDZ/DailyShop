package cn.encmys.ykdz.forest.dailyshop.gui;

import cn.encmys.ykdz.forest.dailyshop.api.DailyShop;
import cn.encmys.ykdz.forest.dailyshop.api.config.ShopConfig;
import cn.encmys.ykdz.forest.dailyshop.api.config.record.gui.ShopGUIRecord;
import cn.encmys.ykdz.forest.dailyshop.api.config.record.misc.IconRecord;
import cn.encmys.ykdz.forest.dailyshop.api.gui.ShopRelatedGUI;
import cn.encmys.ykdz.forest.dailyshop.api.item.decorator.BaseItemDecorator;
import cn.encmys.ykdz.forest.dailyshop.api.product.Product;
import cn.encmys.ykdz.forest.dailyshop.api.profile.enums.GUIType;
import cn.encmys.ykdz.forest.dailyshop.api.shop.Shop;
import cn.encmys.ykdz.forest.dailyshop.api.utils.LogUtils;
import cn.encmys.ykdz.forest.dailyshop.api.utils.TextUtils;
import cn.encmys.ykdz.forest.dailyshop.item.builder.NormalIconBuilder;
import cn.encmys.ykdz.forest.dailyshop.item.builder.ProductIconBuilder;
import cn.encmys.ykdz.forest.dailyshop.item.decorator.BaseItemDecoratorImpl;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.Nullable;
import xyz.xenondevs.invui.gui.Gui;
import xyz.xenondevs.invui.gui.ScrollGui;
import xyz.xenondevs.invui.gui.structure.Markers;
import xyz.xenondevs.invui.item.Item;
import xyz.xenondevs.invui.window.Window;

import java.util.ArrayList;
import java.util.HashMap;

public class ShopGUI extends ShopRelatedGUI {
    private final ShopGUIRecord guiRecord;

    public ShopGUI(Shop shop, ShopGUIRecord guiRecord) {
        super(shop);
        this.guiRecord = guiRecord;
    }

    @Override
    public Gui buildGUI(Player player) {
        ScrollGui.Builder<Item> guiBuilder = ScrollGui.items()
                .setStructure(guiRecord.layout().toArray(new String[0]));

        if (guiRecord.scrollMode().isHorizontal()) {
            guiBuilder.addIngredient(markerIdentifier, Markers.CONTENT_LIST_SLOT_HORIZONTAL);
        } else {
            guiBuilder.addIngredient(markerIdentifier, Markers.CONTENT_LIST_SLOT_VERTICAL);
        }

        // 普通图标
        if (guiRecord.icons() != null) {
            for (IconRecord iconRecord : guiRecord.icons()) {
                guiBuilder.addIngredient(iconRecord.key(), buildNormalIcon(iconRecord, player));
            }
        }

        // 商品图标
        for (String productId : shop.getShopStocker().getListedProducts()) {
            Product product = DailyShop.PRODUCT_FACTORY.getProduct(productId);
            if (product == null) {
                continue;
            }
            guiBuilder.addContent(ProductIconBuilder.build(product.getIconDecorator(), player, shop.getId(), product));
        }

        return guiBuilder.build();
    }

    @Override
    public void loadContent(@Nullable Player player) {
        return;
    }

    @Override
    public void open(Player player) {
        ShopGUIRecord record = ShopConfig.getShopGUIRecord(shop.getId());
        Window window = Window.single()
                .setGui(buildGUI(player))
                .setTitle(TextUtils.decorateText(record.title(), player, new HashMap<>() {{
                    put("shop-name", shop.getName());
                    put("shop-id", shop.getId());
                    put("player-name", player.getName());
                    put("player-uuid", player.getUniqueId().toString());
                }}))
                .setCloseHandlers(new ArrayList<>() {{
                    add(() -> getWindows().remove(player.getUniqueId()));
                }})
                .build(player);

        DailyShop.PROFILE_FACTORY.getProfile(player).setViewingGuiType(GUIType.SHOP);

        getWindows().put(player.getUniqueId(), window);
        window.open();
    }

    @Override
    public Item buildNormalIcon(IconRecord record, Player player) {
        BaseItemDecorator decorator = BaseItemDecoratorImpl.get(record, true);
        if (decorator == null) {
            LogUtils.warn("Icon shop-gui.icons." + record + " in shop " + shop.getId() + " has invalid base setting. Please check it.");
            return null;
        }
        return NormalIconBuilder.build(decorator, shop, this, player);
    }
}
