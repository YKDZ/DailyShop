package cn.encmys.ykdz.forest.dailyshop.api.utils;

import cn.encmys.ykdz.forest.dailyshop.api.config.MessageConfig;
import cn.encmys.ykdz.forest.dailyshop.api.config.OrderHistoryGUIConfig;
import cn.encmys.ykdz.forest.dailyshop.api.config.record.gui.OrderHistoryGUIRecord;
import cn.encmys.ykdz.forest.dailyshop.api.shop.cashier.log.SettlementLog;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import xyz.xenondevs.invui.item.Item;
import xyz.xenondevs.invui.item.builder.ItemBuilder;
import xyz.xenondevs.invui.item.impl.SimpleItem;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class SettlementLogUtils {
    @NotNull
    public static Item toHistoryGuiItem(@NotNull SettlementLog log, Player player) {
        OrderHistoryGUIRecord record = OrderHistoryGUIConfig.getGUIRecord();

        Map<String, String> vars = new HashMap<>() {{
            put("date", MessageConfig.formatTime(log.getTransitionTime(), record.historyIconRecord().miscDatePrecision()));
            put("price", MessageConfig.format_decimal.format(log.getTotalPrice()));
            put("type", MessageConfig.getTerm(log.getType()));
            put("total-price", MessageConfig.format_decimal.format(log.getTotalPrice()));
        }};

        String orderContentsLineFormat = record.historyIconRecord().formatOrderContentsLine();
        Map<String, List<String>> listVars = new HashMap<>();
        List<String> orderContentsLines = new ArrayList<>();
        List<String> names = log.getOrderedProductNames();
        List<Integer> stacks = log.getOrderedProductStacks();
        for (int i = 0; i < names.size(); i++) {
            String name = names.get(i);
            int stack = stacks.get(i);

            orderContentsLines.add(TextUtils.decorateTextKeepMiniMessage(orderContentsLineFormat, player, new HashMap<>() {{
                put("name", name);
                put("amount", Integer.toString(stack));
            }}));
        }
        listVars.put("order-contents", orderContentsLines);

        List<String> lore = TextUtils.decorateText(record.historyIconRecord().formatLore(), player, vars, listVars);
        String name = TextUtils.decorateText(record.historyIconRecord().formatName(), player, vars);

        return new SimpleItem(
                new ItemBuilder(new cn.encmys.ykdz.forest.dailyshop.api.utils.ItemBuilder(Material.PAPER)
                        .setDisplayName(name)
                        .setLore(lore)
                        .build(1)
                )
        );
    }
}
