package cn.encmys.ykdz.forest.dailyshop.gui;

import cn.encmys.ykdz.forest.dailyshop.api.DailyShop;
import cn.encmys.ykdz.forest.dailyshop.api.config.Config;
import cn.encmys.ykdz.forest.dailyshop.api.config.record.gui.OrderHistoryGUIRecord;
import cn.encmys.ykdz.forest.dailyshop.api.config.record.shop.IconRecord;
import cn.encmys.ykdz.forest.dailyshop.api.gui.PlayerRelatedGUI;
import cn.encmys.ykdz.forest.dailyshop.api.item.decorator.BaseItemDecorator;
import cn.encmys.ykdz.forest.dailyshop.api.profile.enums.GUIType;
import cn.encmys.ykdz.forest.dailyshop.api.shop.cashier.log.SettlementLog;
import cn.encmys.ykdz.forest.dailyshop.api.shop.order.enums.OrderType;
import cn.encmys.ykdz.forest.dailyshop.api.utils.ConfigUtils;
import cn.encmys.ykdz.forest.dailyshop.api.utils.LogUtils;
import cn.encmys.ykdz.forest.dailyshop.api.utils.TextUtils;
import cn.encmys.ykdz.forest.dailyshop.item.builder.NormalIconBuilder;
import cn.encmys.ykdz.forest.dailyshop.item.builder.OrderHistoryIconBuilder;
import cn.encmys.ykdz.forest.dailyshop.item.decorator.BaseItemDecoratorImpl;
import org.bukkit.entity.Player;
import xyz.xenondevs.invui.gui.Gui;
import xyz.xenondevs.invui.gui.ScrollGui;
import xyz.xenondevs.invui.gui.structure.Markers;
import xyz.xenondevs.invui.item.Item;
import xyz.xenondevs.invui.window.Window;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.stream.IntStream;

public class OrderHistoryGUI extends PlayerRelatedGUI {
    private final OrderHistoryGUIRecord guiRecord;
    private final int pageSize;
    private ScrollGui<Item> gui;
    private int currentPage = 1;

    public OrderHistoryGUI(Player player, OrderHistoryGUIRecord guiRecord) {
        super(player);
        this.guiRecord = guiRecord;
        this.pageSize = ConfigUtils.getLayoutMarkerAmount(guiRecord.layout(), markerIdentifier) / 5;
    }

    @Override
    public void open() {
        currentPage = guiRecord.scrollMode().isHorizontal() ? ConfigUtils.getLayoutMarkerColumAmount(guiRecord.layout(), markerIdentifier) : ConfigUtils.getLayoutMarkerRowAmount(guiRecord.layout(), markerIdentifier);
        loadMore();

        Window window = Window.single()
                .setGui(buildGUI(player))
                .setViewer(player)
                .setTitle(TextUtils.decorateText(guiRecord.title(), player, new HashMap<>() {{
                    put("player-name", player.getName());
                    put("player-uuid", player.getUniqueId().toString());
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
    public Gui buildGUI(Player player) {
        if (gui != null) {
            return gui;
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
                guiBuilder.addIngredient(icon.key(), buildNormalIcon(icon, player));
            }
        }

        gui = guiBuilder.build();

        return gui;
    }

    @Override
    public Item buildNormalIcon(IconRecord record, Player player) {
        BaseItemDecorator decorator = BaseItemDecoratorImpl.get(record, true);
        if (decorator == null) {
            LogUtils.warn("Icon history-gui.icons." + record + " has invalid base setting. Please check it.");
            return null;
        }
        return NormalIconBuilder.build(decorator, null, player);
    }

    public void loadMore() {
        currentPage += 1;
        DailyShop.INSTANCE.getServer().getScheduler().runTaskAsynchronously(
                DailyShop.INSTANCE,
                () -> {
                    List<Item> contents = new ArrayList<>();
                    IntStream.range(1, currentPage).forEach(page -> {
                        try {
                            List<SettlementLog> logs = DailyShop.DATABASE.queryLogs(null, player.getUniqueId(), null, Config.logUsageLimit_timeRange, page, pageSize, OrderType.SELL_TO, OrderType.BUY_FROM, OrderType.BUY_ALL_FROM).get();
                            for (SettlementLog log : logs) {
                                contents.add(
                                        OrderHistoryIconBuilder.build(log, player)
                                );
                            }
                        } catch (ExecutionException | InterruptedException e) {
                            throw new RuntimeException(e);
                        }
                    });
                    gui.setContent(contents);
                }
        );
    }
}
