package cn.encmys.ykdz.forest.dailyshop.gui;

import cn.encmys.ykdz.forest.dailyshop.api.DailyShop;
import cn.encmys.ykdz.forest.dailyshop.api.config.ShopConfig;
import cn.encmys.ykdz.forest.dailyshop.api.config.record.gui.ShopGUIRecord;
import cn.encmys.ykdz.forest.dailyshop.api.config.record.misc.IconRecord;
import cn.encmys.ykdz.forest.dailyshop.api.gui.ShopRelatedGUI;
import cn.encmys.ykdz.forest.dailyshop.api.gui.enums.GUIContentType;
import cn.encmys.ykdz.forest.dailyshop.api.product.Product;
import cn.encmys.ykdz.forest.dailyshop.api.profile.enums.GUIType;
import cn.encmys.ykdz.forest.dailyshop.api.shop.Shop;
import cn.encmys.ykdz.forest.dailyshop.api.utils.TextUtils;
import cn.encmys.ykdz.forest.dailyshop.item.builder.NormalIconBuilder;
import cn.encmys.ykdz.forest.dailyshop.item.builder.ProductIconBuilder;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.Nullable;
import xyz.xenondevs.invui.gui.Gui;
import xyz.xenondevs.invui.gui.PagedGui;
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
        this.guiContentType = guiRecord.scrollMode() != null ? GUIContentType.SCROLL : GUIContentType.PAGED;
    }

    @Override
    protected Gui buildScrollGUI(Player player) {
        if (guiRecord.scrollMode() == null) {
            throw new IllegalStateException();
        }

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
                guiBuilder.addIngredient(iconRecord.key(), NormalIconBuilder.build(iconRecord, shop, this, player));
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
    protected Gui buildPagedGUI(Player player) {
        if (guiRecord.pageMode() == null) {
            throw new IllegalStateException();
        }

        PagedGui.Builder<Item> guiBuilder = PagedGui.items()
                .setStructure(guiRecord.layout().toArray(new String[0]));

        if (guiRecord.pageMode().isHorizontal()) {
            guiBuilder.addIngredient(markerIdentifier, Markers.CONTENT_LIST_SLOT_HORIZONTAL);
        } else {
            guiBuilder.addIngredient(markerIdentifier, Markers.CONTENT_LIST_SLOT_VERTICAL);
        }

        // 普通图标
        if (guiRecord.icons() != null) {
            for (IconRecord iconRecord : guiRecord.icons()) {
                guiBuilder.addIngredient(iconRecord.key(), NormalIconBuilder.build(iconRecord, shop, this, player));
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
    }

    @Override
    public void open(Player player) {
        ShopGUIRecord record = ShopConfig.getShopGUIRecord(shop.getId());
        Window window = Window.single()
                .setGui(build(player))
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
}
