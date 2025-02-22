package cn.encmys.ykdz.forest.hyphashop.gui;

import cn.encmys.ykdz.forest.hyphascript.context.Context;
import cn.encmys.ykdz.forest.hyphashop.api.HyphaShop;
import cn.encmys.ykdz.forest.hyphashop.api.gui.GUI;
import cn.encmys.ykdz.forest.hyphashop.api.profile.Profile;
import cn.encmys.ykdz.forest.hyphashop.api.shop.Shop;
import cn.encmys.ykdz.forest.hyphashop.api.shop.order.ShopOrder;
import cn.encmys.ykdz.forest.hyphashop.config.record.gui.CartGUIRecord;
import cn.encmys.ykdz.forest.hyphashop.item.builder.CartProductIconBuilder;
import cn.encmys.ykdz.forest.hyphashop.item.builder.NormalIconBuilder;
import cn.encmys.ykdz.forest.hyphashop.scheduler.Scheduler;
import cn.encmys.ykdz.forest.hyphashop.utils.ScriptUtils;
import cn.encmys.ykdz.forest.hyphashop.utils.VarUtils;
import cn.encmys.ykdz.forest.hyphautils.HyphaAdventureUtils;
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
    protected final static @NotNull Map<UUID, Window> windows = new HashMap<>();
    private final @NotNull CartGUIRecord record;
    private boolean isScrolled;
    private @Nullable Gui gui;

    public CartGUI(@NotNull CartGUIRecord record) {
        this.record = record;
    }

    @Override
    public void open(@NotNull Player player) {
        Window window = Window.single()
                .setGui(build())
                .setTitleSupplier(() -> {
                    Context ctx = ScriptUtils.buildContext(
                            Context.GLOBAL_CONTEXT,
                            VarUtils.extractVars(player, null)
                    );
                    return HyphaAdventureUtils.getComponentFromMiniMessage(ScriptUtils.evaluateString(ctx, record.title()));
                })
                .setCloseHandlers(new ArrayList<>() {{
                    add(() -> windows.remove(player.getUniqueId()));
                }})
                .build(player);

        loadContent(player);

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

//        HyphaShop.PROFILE_FACTORY.getProfile(player).setViewingGuiType(GUIType.CART);

        windows.put(player.getUniqueId(), window);
    }

    @Override
    public void closeAll() {
        windows.forEach((uuid, window) -> window.close());
        windows.clear();
    }

    @Override
    public void close(@NotNull Player player) {
        Window window = windows.get(player.getUniqueId());
        if (window != null) window.close();
        windows.remove(player.getUniqueId());
    }

    protected @NotNull IngredientPreset buildIconPreset() {
        IngredientPreset.Builder builder = IngredientPreset.builder();
        Stream.ofNullable(record.icons())
                .map(Map::entrySet)
                .flatMap(Collection::stream)
                .forEach(entry ->
                        builder.addIngredient(
                                entry.getKey(),
                                NormalIconBuilder.build(entry.getValue(), null)
                        )
                );
        return builder.build();
    }

    protected @NotNull Gui build() {
        if (record.pageMode() != null) {
            isScrolled = false;
            gui = buildPagedGUI();
        }
        else {
            isScrolled = true;
            gui = buildScrollGUI();
        }
        return gui;
    }

    protected @NotNull Gui buildScrollGUI() {
        if (record.scrollMode() == null) throw new IllegalStateException("Try to build ScrollGui with a null scrollMode");

        ScrollGui.Builder<Item> guiBuilder = ScrollGui.items()
                .setStructure(record.layout().toArray(new String[0]))
                .addIngredient(markerIdentifier, record.scrollMode())
                .applyPreset(buildIconPreset());

        return guiBuilder.build();
    }

    protected @NotNull Gui buildPagedGUI() {
        if (record.pageMode() == null) throw new IllegalStateException("Try to build PagedGUI with a null pageMode");

        PagedGui.Builder<Item> guiBuilder = PagedGui.items()
                .setStructure(record.layout().toArray(new String[0]))
                .addIngredient(markerIdentifier, record.pageMode())
                .applyPreset(buildIconPreset());

        return guiBuilder.build();
    }

    @Override
    @SuppressWarnings("unchecked")
    public void loadContent(@NotNull Player player) {
        Profile profile = HyphaShop.PROFILE_FACTORY.getProfile(player);
        Map<String, ShopOrder> cart = profile.getCart().getOrders();
        List<Item> contents = new ArrayList<>();

        for (Map.Entry<String, ShopOrder> entry : cart.entrySet()) {
            Shop shop = HyphaShop.SHOP_FACTORY.getShop(entry.getKey());
            if (shop == null) {
                continue;
            }
            ShopOrder cartOrder = entry.getValue();
            for (String productId : cartOrder.getOrderedProducts().keySet()) {
                Item content = CartProductIconBuilder.toCartGUIItem(shop, cartOrder, productId);
                contents.add(content);
            }
        }

        if (!isScrolled) {
            ((PagedGui<Item>) gui).setContent(contents);
        } else {
            ((ScrollGui<Item>) gui).setContent(contents);
        }
    }
}
