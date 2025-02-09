package cn.encmys.ykdz.forest.dailyshop.gui;

import cn.encmys.ykdz.forest.dailyshop.api.DailyShop;
import cn.encmys.ykdz.forest.dailyshop.api.config.record.gui.OrderHistoryGUIRecord;
import cn.encmys.ykdz.forest.dailyshop.api.gui.GUI;
import cn.encmys.ykdz.forest.dailyshop.api.shop.cashier.log.SettlementLog;
import cn.encmys.ykdz.forest.dailyshop.api.shop.order.enums.OrderType;
import cn.encmys.ykdz.forest.dailyshop.api.utils.TextUtils;
import cn.encmys.ykdz.forest.dailyshop.item.builder.NormalIconBuilder;
import cn.encmys.ykdz.forest.dailyshop.item.builder.OrderHistoryIconBuilder;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import xyz.xenondevs.invui.gui.Gui;
import xyz.xenondevs.invui.gui.IngredientPreset;
import xyz.xenondevs.invui.gui.PagedGui;
import xyz.xenondevs.invui.gui.ScrollGui;
import xyz.xenondevs.invui.item.Item;
import xyz.xenondevs.invui.window.Window;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.stream.IntStream;
import java.util.stream.Stream;

public class OrderHistoryGUI extends GUI {
    @NotNull
    private final OrderHistoryGUIRecord guiRecord;
    @NotNull
    private final IngredientPreset iconPreset = buildIconPreset();
    private final int pageSize;
    private int currentPage = 1;

    public OrderHistoryGUI(@NotNull OrderHistoryGUIRecord guiRecord) {
        this.guiRecord = guiRecord;
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
        loadContent(player);

        Window window = Window.single()
                .setGui(build())
                .setViewer(player)
                .setTitle(TextUtils.decorateText(guiRecord.title(), player, new HashMap<>() {{
                    put("player_name", player.getName());
                    put("player_uuid", player.getUniqueId().toString());
                }}))
                .setCloseHandlers(new ArrayList<>() {{
                    add(() -> windows.remove(player.getUniqueId()));
                }})
                .build();

//        DailyShop.PROFILE_FACTORY.getProfile(player).setViewingGuiType(GUIType.ORDER_HISTORY);

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

        return guiBuilder.build();
    }

    protected Gui buildPagedGUI() {
        if (guiRecord.pageMode() == null) throw new IllegalStateException("Try to build PagedGUI with a null pageMode");

        PagedGui.Builder<Item> guiBuilder = PagedGui.items()
                .setStructure(guiRecord.layout().toArray(new String[0]));
        guiBuilder.addIngredient(markerIdentifier, guiRecord.pageMode());
        guiBuilder.applyPreset(buildIconPreset());

        guiBuilder.applyPreset(iconPreset);

        return guiBuilder.build();
    }

    @Override
    public void loadContent(@Nullable Player player) {
        if (player == null) return;

        DailyShop.INSTANCE.getServer().getScheduler().runTaskAsynchronously(
                DailyShop.INSTANCE,
                () -> {
                    // TODO 不重复加载
//                    int count;
//                    try {
//                        count = DailyShop.DATABASE.countLogs(this.player.getUniqueId(), 11111).get();
//                    } catch (InterruptedException | ExecutionException e) {
//                        throw new RuntimeException(e);
//                    }
//                    if (pageSize * currentPage >= count) {
//                        return;
//                    }
                    List<Item> contents = new ArrayList<>();
                    IntStream.range(0, ++currentPage).forEach(page -> {
                        List<SettlementLog> logs;
                        logs = DailyShop.DATABASE_FACTORY.getSettlementLogDao().queryLogs(player.getUniqueId(), page, pageSize, OrderType.SELL_TO, OrderType.BUY_FROM, OrderType.BUY_ALL_FROM);
                        for (SettlementLog log : logs) {
                            contents.add(
                                    OrderHistoryIconBuilder.build(log, player)
                            );
                        }
                    });
                    if (gui instanceof PagedGui) {
                        ((PagedGui<Item>) gui).setContent(contents);
                    } else if (gui instanceof ScrollGui) {
                        ((ScrollGui<Item>) gui).setContent(contents);
                    }
                }
        );
    }
}
