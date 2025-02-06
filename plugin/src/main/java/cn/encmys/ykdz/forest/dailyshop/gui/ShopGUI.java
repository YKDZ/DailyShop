package cn.encmys.ykdz.forest.dailyshop.gui;

import cn.encmys.ykdz.forest.dailyshop.api.DailyShop;
import cn.encmys.ykdz.forest.dailyshop.api.config.ShopConfig;
import cn.encmys.ykdz.forest.dailyshop.api.config.record.gui.ShopGUIRecord;
import cn.encmys.ykdz.forest.dailyshop.api.gui.GUI;
import cn.encmys.ykdz.forest.dailyshop.api.shop.Shop;
import cn.encmys.ykdz.forest.dailyshop.api.utils.TextUtils;
import cn.encmys.ykdz.forest.dailyshop.item.builder.NormalIconBuilder;
import cn.encmys.ykdz.forest.dailyshop.item.builder.ProductIconBuilder;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import xyz.xenondevs.invui.gui.Gui;
import xyz.xenondevs.invui.gui.IngredientPreset;
import xyz.xenondevs.invui.gui.PagedGui;
import xyz.xenondevs.invui.gui.ScrollGui;
import xyz.xenondevs.invui.item.Item;
import xyz.xenondevs.invui.window.Window;

import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Objects;
import java.util.stream.Stream;

public class ShopGUI extends GUI {
    @NotNull
    private final Shop shop;
    @NotNull
    private final ShopGUIRecord guiRecord;
    @NotNull
    private final IngredientPreset iconPreset = buildIconPreset();
    @NotNull
    private final List<Item> contents = buildContents();

    public ShopGUI(@NotNull Shop shop, @NotNull ShopGUIRecord guiRecord) {
        this.shop = shop;
        this.guiRecord = guiRecord;
    }

    protected IngredientPreset buildIconPreset() {
        IngredientPreset.Builder builder = IngredientPreset.builder();
        Stream.ofNullable(guiRecord.icons())
                .flatMap(Collection::stream)
                .forEach(iconRecord ->
                        builder.addIngredient(
                                iconRecord.key(),
                                NormalIconBuilder.build(iconRecord, shop, null, null)
                        )
                );
        return builder.build();
    }

    protected List<Item> buildContents() {
        return shop.getShopStocker().getListedProducts().stream()
                .map(productId -> DailyShop.PRODUCT_FACTORY.getProduct(productId))
                .filter(Objects::nonNull)
                .map(product -> ProductIconBuilder.build(product.getIconDecorator(), shop.getId(), product))
                .toList();
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

    public void open(@NotNull Player player) {
        ShopGUIRecord record = ShopConfig.getShopGUIRecord(shop.getId());
        Window window = Window.single()
                .setGui(build())
                .setTitle(TextUtils.decorateTextToComponent(record.title(), player, new HashMap<>() {{
                    put("shop-name", shop.getName());
                    put("shop-id", shop.getId());
                    put("player-name", player.getName());
                    put("player-uuid", player.getUniqueId().toString());
                }}))
                .build(player);
        window.open();
    }
}
