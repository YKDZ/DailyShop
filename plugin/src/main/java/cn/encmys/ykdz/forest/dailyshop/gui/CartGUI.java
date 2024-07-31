package cn.encmys.ykdz.forest.dailyshop.gui;

import cn.encmys.ykdz.forest.dailyshop.api.DailyShop;
import cn.encmys.ykdz.forest.dailyshop.api.config.ShopConfig;
import cn.encmys.ykdz.forest.dailyshop.api.config.record.shop.CartGUIRecord;
import cn.encmys.ykdz.forest.dailyshop.api.config.record.shop.IconRecord;
import cn.encmys.ykdz.forest.dailyshop.api.gui.ShopRelatedGUI;
import cn.encmys.ykdz.forest.dailyshop.api.item.decorator.BaseItemDecorator;
import cn.encmys.ykdz.forest.dailyshop.api.profile.Profile;
import cn.encmys.ykdz.forest.dailyshop.api.shop.Shop;
import cn.encmys.ykdz.forest.dailyshop.api.shop.order.ShopOrder;
import cn.encmys.ykdz.forest.dailyshop.api.utils.LogUtils;
import cn.encmys.ykdz.forest.dailyshop.api.utils.OrderUtils;
import cn.encmys.ykdz.forest.dailyshop.api.utils.TextUtils;
import cn.encmys.ykdz.forest.dailyshop.item.builder.NormalIconBuilder;
import cn.encmys.ykdz.forest.dailyshop.item.decorator.BaseItemDecoratorImpl;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import xyz.xenondevs.invui.gui.ScrollGui;
import xyz.xenondevs.invui.gui.structure.Markers;
import xyz.xenondevs.invui.item.Item;
import xyz.xenondevs.invui.window.Window;

import java.util.ArrayList;
import java.util.HashMap;

public class CartGUI extends ShopRelatedGUI {
    public CartGUI(Shop shop) {
        super(shop);
    }

    @Override
    public void open(@NotNull Player player) {
        CartGUIRecord record = ShopConfig.getCartGUIRecord(shop.getId());

        Window window = Window.single()
                .setGui(buildGUIBuilder(player))
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

        window.open();

        getWindows().put(player.getUniqueId(), window);
    }

    @Override
    @NotNull
    public ScrollGui.Builder<Item> buildGUIBuilder(@NotNull Player player) {
        Profile profile = DailyShop.PROFILE_FACTORY.getProfile(player);
        if (profile == null) {
            throw new RuntimeException("Try to open cart GUI without profile");
        }
        ShopOrder cart = profile.getCart(shop.getId());
        String shopId = shop.getId();
        CartGUIRecord record = ShopConfig.getCartGUIRecord(shopId);

        ScrollGui.Builder<Item> guiBuilder = ScrollGui.items()
                .setStructure(record.layout().toArray(new String[0]));

        if (record.scrollMode().isHorizontal()) {
            guiBuilder.addIngredient(markerIdentifier, Markers.CONTENT_LIST_SLOT_HORIZONTAL);
        } else {
            guiBuilder.addIngredient(markerIdentifier, Markers.CONTENT_LIST_SLOT_VERTICAL);
        }

        // 普通图标
        if (record.icons() != null) {
            for (IconRecord icon : record.icons()) {
                guiBuilder.addIngredient(icon.key(), buildNormalIcon(icon, player));
            }
        }

        // 商品图标
        for (String productId : cart.getOrderedProducts().keySet()) {
            Item content = OrderUtils.toCartGUIItem(shop, cart, productId);
            guiBuilder.addContent(content);
        }

        return guiBuilder;
    }

    @Override
    public Item buildNormalIcon(IconRecord record, Player player) {
        BaseItemDecorator decorator = BaseItemDecoratorImpl.get(record, true);
        if (decorator == null) {
            LogUtils.warn("Icon cart-gui.icons." + record + " in shop " + shop.getId() + " has invalid base setting. Please check it.");
            return null;
        }
        return NormalIconBuilder.build(decorator, shop, player);
    }
}
