package cn.encmys.ykdz.forest.dailyshop.gui;

import cn.encmys.ykdz.forest.dailyshop.api.DailyShop;
import cn.encmys.ykdz.forest.dailyshop.api.config.OrderHistoryGUIConfig;
import cn.encmys.ykdz.forest.dailyshop.api.config.record.gui.OrderHistoryGUIRecord;
import cn.encmys.ykdz.forest.dailyshop.api.config.record.shop.IconRecord;
import cn.encmys.ykdz.forest.dailyshop.api.gui.PlayerRelatedGUI;
import cn.encmys.ykdz.forest.dailyshop.api.item.decorator.BaseItemDecorator;
import cn.encmys.ykdz.forest.dailyshop.api.shop.cashier.log.SettlementLog;
import cn.encmys.ykdz.forest.dailyshop.api.shop.order.enums.OrderType;
import cn.encmys.ykdz.forest.dailyshop.api.utils.LogUtils;
import cn.encmys.ykdz.forest.dailyshop.api.utils.SettlementLogUtils;
import cn.encmys.ykdz.forest.dailyshop.api.utils.TextUtils;
import cn.encmys.ykdz.forest.dailyshop.item.builder.NormalIconBuilder;
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

public class OrderHistoryGUI extends PlayerRelatedGUI {
    public OrderHistoryGUI(Player player) {
        super(player);
    }

    @Override
    public void open() {
        OrderHistoryGUIRecord record = OrderHistoryGUIConfig.getGUIRecord();
        Gui gui = buildGUI(player);

        Window window = Window.single()
                .setGui(gui)
                .setViewer(player)
                .setTitle(TextUtils.decorateText(record.title(), player, new HashMap<>() {{
                    put("player-name", player.getName());
                    put("player-uuid", player.getUniqueId().toString());
                }}))
                .setCloseHandlers(new ArrayList<>() {{
                    add(() -> getWindows().remove(player.getUniqueId()));
                }})
                .build();

        getWindows().put(player.getUniqueId(), window);
        window.open();
    }

    @Override
    public void close() {

    }

    @Override
    public Gui buildGUI(Player player) {
        OrderHistoryGUIRecord record = OrderHistoryGUIConfig.getGUIRecord();

        List<SettlementLog> logs;
        try {
            logs = DailyShop.DATABASE.queryLogs(null, player.getUniqueId(), null, 365, 100, OrderType.BUY_ALL_FROM, OrderType.BUY_FROM, OrderType.SELL_TO).get();
        } catch (InterruptedException | ExecutionException e) {
            LogUtils.warn("Error querying logs for " + player.getDisplayName() + ": " + e.getMessage());
            throw new RuntimeException(e);
        }

        ScrollGui.Builder<Item> guiBuilder = ScrollGui.items()
                .setStructure(record.layout().toArray(new String[0]));

        if (record.scrollMode().isHorizontal()) {
            guiBuilder.addIngredient(markerIdentifier, Markers.CONTENT_LIST_SLOT_HORIZONTAL);
        } else {
            guiBuilder.addIngredient(markerIdentifier, Markers.CONTENT_LIST_SLOT_VERTICAL);
        }

        // 普通图标
        if (record.icons() != null) {
            for (IconRecord iconRecord : record.icons()) {
                guiBuilder.addIngredient(iconRecord.key(), buildNormalIcon(iconRecord, player));
            }
        }

        // 日志图标
        for (SettlementLog log : logs) {
            guiBuilder.addContent(SettlementLogUtils.toHistoryGuiItem(log, player));
        }

        return guiBuilder.build();
    }

    @Override
    public int getLayoutContentSlotAmount() {
        return 0;
    }

    @Override
    public int getLayoutContentSlotLineAmount() {
        return 0;
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
}
