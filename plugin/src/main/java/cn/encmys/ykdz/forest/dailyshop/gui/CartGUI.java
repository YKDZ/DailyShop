package cn.encmys.ykdz.forest.dailyshop.gui;

import cn.encmys.ykdz.forest.dailyshop.api.DailyShop;
import cn.encmys.ykdz.forest.dailyshop.api.config.record.gui.CartGUIRecord;
import cn.encmys.ykdz.forest.dailyshop.api.gui.GUI;
import cn.encmys.ykdz.forest.dailyshop.api.profile.Profile;
import cn.encmys.ykdz.forest.dailyshop.api.shop.Shop;
import cn.encmys.ykdz.forest.dailyshop.api.shop.order.ShopOrder;
import cn.encmys.ykdz.forest.dailyshop.api.utils.OrderUtils;
import cn.encmys.ykdz.forest.dailyshop.api.utils.TextUtils;
import cn.encmys.ykdz.forest.dailyshop.item.builder.NormalIconBuilder;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import xyz.xenondevs.invui.gui.Gui;
import xyz.xenondevs.invui.gui.IngredientPreset;
import xyz.xenondevs.invui.gui.PagedGui;
import xyz.xenondevs.invui.gui.ScrollGui;
import xyz.xenondevs.invui.item.Item;
import xyz.xenondevs.invui.window.Window;

import java.util.*;
import java.util.stream.Stream;

public class CartGUI extends GUI {
    @NotNull
    private final CartGUIRecord guiRecord;
    @NotNull
    private final IngredientPreset iconPreset = buildIconPreset();
    @NotNull
    private final List<Item> contents = new ArrayList<>();

    public CartGUI(@NotNull CartGUIRecord guiRecord) {
        this.guiRecord = guiRecord;
    }

    @Override
    public void open(@NotNull Player player) {
        loadContent(player);

        Window window = Window.single()
                .setGui(build())
                .setTitle(TextUtils.decorateText(guiRecord.title(), player, new HashMap<>() {{
                    put("player-name", player.getName());
                    put("player-uuid", player.getUniqueId().toString());
                }}))
                .setCloseHandlers(new ArrayList<>() {{
                    add(() -> windows.remove(player.getUniqueId()));
                }})
                .build(player);

//        DailyShop.PROFILE_FACTORY.getProfile(player).setViewingGuiType(GUIType.CART);

        windows.put(player.getUniqueId(), window);
        window.open();
    }

    protected IngredientPreset buildIconPreset() {
        IngredientPreset.Builder builder = IngredientPreset.builder();
        Stream.ofNullable(guiRecord.icons())
                .flatMap(Collection::stream)
                .forEach(iconRecord ->
                        builder.addIngredient(
                                iconRecord.key(),
                                NormalIconBuilder.build(iconRecord, null, null, null)
                        )
                );
        return builder.build();
    }

    protected Gui build() {
        if (guiRecord.pageMode() != null) return buildPagedGUI();
        else return buildScrollGUI();
    }

    protected Gui buildScrollGUI() {
        if (guiRecord.scrollMode() == null) throw new IllegalStateException("Try to build ScrollGui with a null scrollMode");

        ScrollGui.Builder<Item> guiBuilder = ScrollGui.items()
                .setStructure(guiRecord.layout().toArray(new String[0]));
        guiBuilder.addIngredient(markerIdentifier, guiRecord.scrollMode());

        guiBuilder.applyPreset(iconPreset);
        contents.forEach(guiBuilder::addContent);

        return guiBuilder.build();
    }

    protected Gui buildPagedGUI() {
        if (guiRecord.pageMode() == null) throw new IllegalStateException("Try to build PagedGUI with a null pageMode");

        PagedGui.Builder<Item> guiBuilder = PagedGui.items()
                .setStructure(guiRecord.layout().toArray(new String[0]));
        guiBuilder.addIngredient(markerIdentifier, guiRecord.pageMode());
        guiBuilder.applyPreset(buildIconPreset());

        guiBuilder.applyPreset(iconPreset);
        contents.forEach(guiBuilder::addContent);

        return guiBuilder.build();
    }

    @Override
    public void loadContent(@Nullable Player player) {
        Profile profile = DailyShop.PROFILE_FACTORY.getProfile(player);
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

        this.contents.clear();
        this.contents.addAll(contents);
    }
}
