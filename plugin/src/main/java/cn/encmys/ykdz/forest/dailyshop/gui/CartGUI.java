package cn.encmys.ykdz.forest.dailyshop.gui;

import cn.encmys.ykdz.forest.dailyshop.api.DailyShop;
import cn.encmys.ykdz.forest.dailyshop.api.config.record.gui.CartGUIRecord;
import cn.encmys.ykdz.forest.dailyshop.api.config.record.misc.IconRecord;
import cn.encmys.ykdz.forest.dailyshop.api.gui.enums.GUIContentType;
import cn.encmys.ykdz.forest.dailyshop.api.profile.Profile;
import cn.encmys.ykdz.forest.dailyshop.api.profile.enums.GUIType;
import cn.encmys.ykdz.forest.dailyshop.api.shop.Shop;
import cn.encmys.ykdz.forest.dailyshop.api.shop.order.ShopOrder;
import cn.encmys.ykdz.forest.dailyshop.api.utils.OrderUtils;
import cn.encmys.ykdz.forest.dailyshop.api.utils.TextUtils;
import cn.encmys.ykdz.forest.dailyshop.item.builder.NormalIconBuilder;
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
import java.util.List;
import java.util.Map;

public class CartGUI extends PlayerRelatedGUI {
    private final CartGUIRecord guiRecord;

    public CartGUI(Player player, CartGUIRecord guiRecord) {
        super(player);
        this.guiRecord = guiRecord;
        this.guiContentType = guiRecord.scrollMode() != null ? GUIContentType.SCROLL : GUIContentType.PAGED;
    }

    @Override
    public void open() {
        loadContent(player);

        Window window = Window.single()
                .setGui(build(player))
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
            for (IconRecord icon : guiRecord.icons()) {
                guiBuilder.addIngredient(icon.key(), NormalIconBuilder.build(icon, null, this, player, null, null));
            }
        }

        ScrollGui<Item> gui = guiBuilder.build();
        this.gui = gui;

        return gui;
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
            for (IconRecord icon : guiRecord.icons()) {
                guiBuilder.addIngredient(icon.key(), NormalIconBuilder.build(icon, null, this, player, null, null));
            }
        }

        PagedGui<Item> gui = guiBuilder.build();
        this.gui = gui;

        return gui;
    }

    @Override
    @SuppressWarnings("unchecked")
    public void loadContent(@Nullable Player player) {
        DailyShop.INSTANCE.getServer().getScheduler().runTaskAsynchronously(
                DailyShop.INSTANCE,
                () -> {
                    Profile profile = DailyShop.PROFILE_FACTORY.getProfile(this.player);
                    Map<String, ShopOrder> cart = profile.getCart().getOrders();
                    List<Item> contents = new ArrayList<>();

                    for (Map.Entry<String, ShopOrder> entry : cart.entrySet()) {
                        Shop shop = DailyShop.SHOP_FACTORY.getShop(entry.getKey());
                        if (shop == null) {
                            continue;
                        }
                        ShopOrder cartOrder = entry.getValue();
                        for (String productId : cartOrder.getOrderedProducts().keySet()) {
                            Item content = OrderUtils.toCartGUIItem(shop, cartOrder, productId);
                            contents.add(content);
                        }
                    }
                    if (gui instanceof PagedGui) {
                        ((PagedGui<Item>) gui).setContent(contents);
                    } else if (gui instanceof ScrollGui) {
                        ((ScrollGui<Item>) gui).setContent(contents);
                    }
                }
        );
    }
}
