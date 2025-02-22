package cn.encmys.ykdz.forest.hyphashop.gui;

import cn.encmys.ykdz.forest.hyphascript.context.Context;
import cn.encmys.ykdz.forest.hyphashop.api.HyphaShop;
import cn.encmys.ykdz.forest.hyphashop.api.gui.GUI;
import cn.encmys.ykdz.forest.hyphashop.api.shop.Shop;
import cn.encmys.ykdz.forest.hyphashop.config.ShopConfig;
import cn.encmys.ykdz.forest.hyphashop.config.record.gui.ShopGUIRecord;
import cn.encmys.ykdz.forest.hyphashop.item.builder.NormalIconBuilder;
import cn.encmys.ykdz.forest.hyphashop.item.builder.ProductIconBuilder;
import cn.encmys.ykdz.forest.hyphashop.scheduler.Scheduler;
import cn.encmys.ykdz.forest.hyphashop.utils.ScriptUtils;
import cn.encmys.ykdz.forest.hyphashop.utils.VarUtils;
import cn.encmys.ykdz.forest.hyphautils.HyphaAdventureUtils;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import xyz.xenondevs.invui.gui.Gui;
import xyz.xenondevs.invui.gui.IngredientPreset;
import xyz.xenondevs.invui.gui.PagedGui;
import xyz.xenondevs.invui.gui.ScrollGui;
import xyz.xenondevs.invui.item.Item;
import xyz.xenondevs.invui.window.Window;

import java.util.*;
import java.util.stream.Stream;

public class ShopGUI extends GUI {
    @NotNull
    protected final static Map<String, Map<UUID, Window>> windows = new HashMap<>();
    @NotNull
    private final Shop shop;
    @NotNull
    private final ShopGUIRecord record;
    @NotNull
    private final List<Item> contents = new ArrayList<>();

    public ShopGUI(@NotNull Shop shop, @NotNull ShopGUIRecord record) {
        this.shop = shop;
        this.record = record;
    }

    protected IngredientPreset buildIconPreset() {
        IngredientPreset.Builder builder = IngredientPreset.builder();
        Stream.ofNullable(record.icons())
                .map(Map::entrySet)
                .flatMap(Collection::stream)
                .forEach(entry ->
                        builder.addIngredient(
                                entry.getKey(),
                                NormalIconBuilder.build(entry.getValue(), shop)
                        )
                );
        return builder.build();
    }

    @Override
    public void loadContent(@NotNull Player player) {
        contents.clear();
        contents.addAll(shop.getShopStocker().getListedProducts().stream()
                .map(productId -> HyphaShop.PRODUCT_FACTORY.getProduct(productId))
                .filter(Objects::nonNull)
                .map(product -> ProductIconBuilder.build(shop.getId(), product))
                .toList());
    }

    protected Gui build() {
        if (record.pageMode() != null) return buildPagedGUI();
        else return buildScrollGUI();
    }

    protected Gui buildScrollGUI() {
        if (record.scrollMode() == null) throw new IllegalStateException("Try to build ScrollGui with a null scrollMode");

        ScrollGui.Builder<Item> guiBuilder = ScrollGui.items()
                .setStructure(record.layout().toArray(new String[0]))
                .addIngredient(markerIdentifier, record.scrollMode())
                .applyPreset(buildIconPreset());
        contents.forEach(guiBuilder::addContent);

        return guiBuilder.build();
    }

    protected Gui buildPagedGUI() {
        if (record.pageMode() == null) throw new IllegalStateException("Try to build PagedGUI with a null pageMode");

        PagedGui.Builder<Item> guiBuilder = PagedGui.items()
                .setStructure(record.layout().toArray(new String[0]))
                .addIngredient(markerIdentifier, record.pageMode())
                .applyPreset(buildIconPreset());
        contents.forEach(guiBuilder::addContent);

        return guiBuilder.build();
    }

    public void open(@NotNull Player player) {
        loadContent(player);

        ShopGUIRecord record = ShopConfig.getShopGUIRecord(shop.getId());
        Window window = Window.single()
                .setGui(build())
                .setTitleSupplier(() -> {
                    Context ctx = ScriptUtils.buildContext(
                            shop.getScriptContext(),
                            VarUtils.extractVars(player, shop)
                    );
                    return HyphaAdventureUtils.getComponentFromMiniMessage(ScriptUtils.evaluateString(ctx, record.title()));
                })
                .addCloseHandler(() -> windows.getOrDefault(shop.getId(), new HashMap<>()).remove(player.getUniqueId()))
                .build(player);

        window.open();

        // GUI 开启的一瞬间也是一次刷新标题
        // 故加上与间隔等长的 delay
        Scheduler.runAsyncTaskAtFixedRate((task) -> {
            if (!window.isOpen()) {
                task.cancel();
                return;
            }
            window.updateTitle();
        }, record.timeUpdatePeriod(), record.timeUpdatePeriod());

        Map<UUID, Window> shopWindows = windows.getOrDefault(shop.getId(), new HashMap<>());
        shopWindows.put(player.getUniqueId(), window);
        windows.put(shop.getId(), shopWindows);
    }

    @Override
    public void closeAll() {
        // 先收集后遍历
        // 因为 close 会修改 windows 本身导致异常
        new ArrayList<>(windows.getOrDefault(shop.getId(), new HashMap<>()).values()).forEach(Window::close);
    }

    @Override
    public void close(@NotNull Player player) {
        Window window = windows.getOrDefault(shop.getId(), new HashMap<>()).get(player.getUniqueId());
        if (window != null) window.close();
    }
}
