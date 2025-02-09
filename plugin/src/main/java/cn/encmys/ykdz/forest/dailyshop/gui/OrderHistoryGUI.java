package cn.encmys.ykdz.forest.dailyshop.gui;

import cn.encmys.ykdz.forest.dailyshop.api.DailyShop;
import cn.encmys.ykdz.forest.dailyshop.api.config.record.gui.OrderHistoryGUIRecord;
import cn.encmys.ykdz.forest.dailyshop.api.config.record.misc.IconRecord;
import cn.encmys.ykdz.forest.dailyshop.api.gui.enums.GUIContentType;
import cn.encmys.ykdz.forest.dailyshop.api.profile.enums.GUIType;
import cn.encmys.ykdz.forest.dailyshop.api.shop.cashier.log.SettlementLog;
import cn.encmys.ykdz.forest.dailyshop.api.shop.order.enums.OrderType;
import cn.encmys.ykdz.forest.dailyshop.api.utils.ConfigUtils;
import cn.encmys.ykdz.forest.dailyshop.api.utils.TextUtils;
import cn.encmys.ykdz.forest.dailyshop.item.builder.NormalIconBuilder;
import cn.encmys.ykdz.forest.dailyshop.item.builder.OrderHistoryIconBuilder;
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
import java.util.stream.IntStream;

public class OrderHistoryGUI extends PlayerRelatedGUI {
    private final OrderHistoryGUIRecord guiRecord;
    private final int pageSize;
    private int currentPage = 1;

    public OrderHistoryGUI(Player player, OrderHistoryGUIRecord guiRecord) {
        super(player);
        this.guiRecord = guiRecord;
        this.guiContentType = guiRecord.scrollMode() != null ? GUIContentType.SCROLL : GUIContentType.PAGED;
        if (guiRecord.scrollMode() != null) {
            this.pageSize = ConfigUtils.getLastLineMarkerAmount(guiRecord.layout(), markerIdentifier, guiRecord.scrollMode());
        } else if (guiRecord.pageMode() != null) {
            this.pageSize = ConfigUtils.getLayoutMarkerAmount(guiRecord.layout(), markerIdentifier);
        } else {
            this.pageSize = 54;
        }
    }

    @Override
    public void open() {
        if (guiRecord.scrollMode() != null) {
            currentPage = guiRecord.scrollMode().isHorizontal() ? ConfigUtils.getLayoutMarkerColumAmount(guiRecord.layout(), markerIdentifier) : ConfigUtils.getLayoutMarkerRowAmount(guiRecord.layout(), markerIdentifier);
        } else if (guiRecord.pageMode() != null) {
            currentPage = 1;
        }
        loadContent(player);

        Window window = Window.single()
                .setGui(build(player))
                .setViewer(player)
                .setTitle(TextUtils.decorateText(guiRecord.title(), player, new HashMap<>() {{
                    put("player_name", player.getName());
                    put("player_uuid", player.getUniqueId().toString());
                }}))
                .setCloseHandlers(new ArrayList<>() {{
                    add(() -> getWindows().remove(player.getUniqueId()));
                }})
                .build();

        DailyShop.PROFILE_FACTORY.getProfile(player).setViewingGuiType(GUIType.ORDER_HISTORY);

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

        gui = guiBuilder.build();

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
        if (player == null) {
            return;
        }
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
                                    OrderHistoryIconBuilder.build(log, this.player)
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
