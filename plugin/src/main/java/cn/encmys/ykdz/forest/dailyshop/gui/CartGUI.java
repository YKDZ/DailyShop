package cn.encmys.ykdz.forest.dailyshop.gui;

import cn.encmys.ykdz.forest.dailyshop.api.DailyShop;
import cn.encmys.ykdz.forest.dailyshop.api.config.record.gui.CartGUIRecord;
import cn.encmys.ykdz.forest.dailyshop.api.config.record.shop.IconRecord;
import cn.encmys.ykdz.forest.dailyshop.api.gui.PlayerRelatedGUI;
import cn.encmys.ykdz.forest.dailyshop.api.item.decorator.BaseItemDecorator;
import cn.encmys.ykdz.forest.dailyshop.api.profile.Profile;
import cn.encmys.ykdz.forest.dailyshop.api.profile.enums.GUIType;
import cn.encmys.ykdz.forest.dailyshop.api.shop.Shop;
import cn.encmys.ykdz.forest.dailyshop.api.shop.order.ShopOrder;
import cn.encmys.ykdz.forest.dailyshop.api.utils.LogUtils;
import cn.encmys.ykdz.forest.dailyshop.api.utils.OrderUtils;
import cn.encmys.ykdz.forest.dailyshop.api.utils.TextUtils;
import cn.encmys.ykdz.forest.dailyshop.item.builder.NormalIconBuilder;
import cn.encmys.ykdz.forest.dailyshop.item.decorator.BaseItemDecoratorImpl;
import org.bukkit.entity.Player;
import xyz.xenondevs.invui.gui.Gui;
import xyz.xenondevs.invui.gui.ScrollGui;
import xyz.xenondevs.invui.gui.structure.Markers;
import xyz.xenondevs.invui.item.Item;
import xyz.xenondevs.invui.window.Window;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class CartGUI extends PlayerRelatedGUI {
    private final CartGUIRecord guiRecord;

    public CartGUI(Player player, CartGUIRecord guiRecord) {
        super(player);
        this.guiRecord = guiRecord;
    }

    @Override
    public void open() {
        Window window = Window.single()
                .setGui(buildGUI(player))
                .setTitle(TextUtils.decorateText(guiRecord.title(), player, new HashMap<>() {{
                    put("player-name", player.getName());
                    put("player-uuid", player.getUniqueId().toString());
                }}))
                .setCloseHandlers(new ArrayList<>() {{
                    add(() -> getWindows().remove(player.getUniqueId()));
                }})
                .build(player);

        DailyShop.PROFILE_FACTORY.getProfile(player).setViewingGuiType(GUIType.CART);

        getWindows().put(player.getUniqueId(), window);
        window.open();
    }

    @Override
    public Gui buildGUI(Player player) {
        Profile profile = DailyShop.PROFILE_FACTORY.getProfile(player);
        Map<String, ShopOrder> cart = profile.getCart().getOrders();

        ScrollGui.Builder<Item> guiBuilder = ScrollGui.items()
                .setStructure(guiRecord.layout().toArray(new String[0]));

        if (guiRecord.scrollMode().isHorizontal()) {
            guiBuilder.addIngredient(markerIdentifier, Markers.CONTENT_LIST_SLOT_HORIZONTAL);
        } else {
            guiBuilder.addIngredient(markerIdentifier, Markers.CONTENT_LIST_SLOT_VERTICAL);
        }

        // 普通图标
        if (guiRecord.icons() != null) {
            for (IconRecord icon : guiRecord.icons()) {
                guiBuilder.addIngredient(icon.key(), buildNormalIcon(icon, player));
            }
        }

        // 商品图标
        for (Map.Entry<String, ShopOrder> entry : cart.entrySet()) {
            Shop shop = DailyShop.SHOP_FACTORY.getShop(entry.getKey());
            if (shop == null) {
                continue;
            }
            ShopOrder cartOrder = entry.getValue();
            for (String productId : cartOrder.getOrderedProducts().keySet()) {
                Item content = OrderUtils.toCartGUIItem(shop, cartOrder, productId);
                guiBuilder.addContent(content);
            }
        }

        return guiBuilder.build();
    }

    @Override
    public Item buildNormalIcon(IconRecord record, Player player) {
        BaseItemDecorator decorator = BaseItemDecoratorImpl.get(record, true);
        if (decorator == null) {
            LogUtils.warn("Icon cart.icons." + record + " in cart gui has invalid base setting. Please check it.");
            return null;
        }
        return NormalIconBuilder.build(decorator, null, player);
    }
}
