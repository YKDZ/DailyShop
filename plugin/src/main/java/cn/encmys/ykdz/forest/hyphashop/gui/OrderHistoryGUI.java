package cn.encmys.ykdz.forest.hyphashop.gui;

import cn.encmys.ykdz.forest.hyphascript.context.Context;
import cn.encmys.ykdz.forest.hyphashop.api.HyphaShop;
import cn.encmys.ykdz.forest.hyphashop.api.gui.GUI;
import cn.encmys.ykdz.forest.hyphashop.api.shop.cashier.log.SettlementLog;
import cn.encmys.ykdz.forest.hyphashop.api.shop.order.enums.OrderType;
import cn.encmys.ykdz.forest.hyphashop.config.record.gui.OrderHistoryGUIRecord;
import cn.encmys.ykdz.forest.hyphashop.item.builder.NormalIconBuilder;
import cn.encmys.ykdz.forest.hyphashop.item.builder.OrderHistoryIconBuilder;
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
import java.util.stream.IntStream;
import java.util.stream.Stream;

public class OrderHistoryGUI extends GUI {
    @NotNull
    protected final static Map<UUID, Window> windows = new HashMap<>();
    @NotNull
    private final OrderHistoryGUIRecord record;
    @Nullable
    private Gui gui;
    private boolean isScrolled;
    private final int pageSize;
    private int currentPage = 1;

    public OrderHistoryGUI(@NotNull OrderHistoryGUIRecord record) {
        this.record = record;
//        if (guiRecord.scrollMode() != null) {
//            this.pageSize = ConfigUtils.getLastLineMarkerAmount(guiRecord.layout(), markerIdentifier, guiRecord.scrollMode());
//        } else if (guiRecord.pageMode() != null) {
//            this.pageSize = ConfigUtils.getLayoutMarkerAmount(guiRecord.layout(), markerIdentifier);
//        } else {
//            this.pageSize = 54;
//        }
        this.pageSize = 54;
    }

    @Override
    public void open(@NotNull Player player) {
//        if (guiRecord.scrollMode() != null) {
//            currentPage = guiRecord.scrollMode().isHorizontal() ? ConfigUtils.getLayoutMarkerColumAmount(guiRecord.layout(), markerIdentifier) : ConfigUtils.getLayoutMarkerRowAmount(guiRecord.layout(), markerIdentifier);
//        } else if (guiRecord.pageMode() != null) {
//            currentPage = 1;
//        }
        currentPage = 1;

        Window window = Window.single()
                .setGui(build())
                .setViewer(player)
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
                .build();

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

        loadContent(player);

//        HyphaShop.PROFILE_FACTORY.getProfile(player).setViewingGuiType(GUIType.ORDER_HISTORY);

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

    protected IngredientPreset buildIconPreset() {
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

    protected Gui build() {
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

    protected Gui buildScrollGUI() {
        if (record.scrollMode() == null) throw new IllegalStateException("Try to build ScrollGui with a null scrollMode");

        return ScrollGui.items()
                .setStructure(record.layout().toArray(new String[0]))
                .addIngredient(markerIdentifier, record.scrollMode())
                .applyPreset(buildIconPreset())
                .build();
    }

    protected Gui buildPagedGUI() {
        if (record.pageMode() == null) throw new IllegalStateException("Try to build PagedGUI with a null pageMode");

        return PagedGui.items()
                .setStructure(record.layout().toArray(new String[0]))
                .addIngredient(markerIdentifier, record.pageMode())
                .applyPreset(buildIconPreset())
                .applyPreset(buildIconPreset())
                .build();
    }

    @Override
    @SuppressWarnings("unchecked")
    public void loadContent(@NotNull Player player) {
        HyphaShop.INSTANCE.getServer().getScheduler().runTaskAsynchronously(
                HyphaShop.INSTANCE,
                () -> {
                    // TODO 不重复加载
//                    int count;
//                    try {
//                        count = HyphaShop.DATABASE.countLogs(this.player.getUniqueId(), 11111).get();
//                    } catch (InterruptedException | ExecutionException e) {
//                        throw new RuntimeException(e);
//                    }
//                    if (pageSize * currentPage >= count) {
//                        return;
//                    }
                    List<Item> contents = new ArrayList<>();
                    IntStream.range(0, ++currentPage).forEach(page -> {
                        List<SettlementLog> logs;
                        logs = HyphaShop.DATABASE_FACTORY.getSettlementLogDao().queryLogs(player.getUniqueId(), page, pageSize, OrderType.SELL_TO, OrderType.BUY_FROM, OrderType.BUY_ALL_FROM);
                        for (SettlementLog log : logs) {
                            contents.add(
                                    OrderHistoryIconBuilder.build(log, player)
                            );
                        }
                    });
                    if (!isScrolled) {
                        ((PagedGui<Item>) gui).setContent(contents);
                    } else {
                        ((ScrollGui<Item>) gui).setContent(contents);
                    }
                }
        );
    }
}
