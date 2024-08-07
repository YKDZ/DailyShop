package cn.encmys.ykdz.forest.dailyshop.api.utils;

import cn.encmys.ykdz.forest.dailyshop.api.config.ShopConfig;
import cn.encmys.ykdz.forest.dailyshop.api.config.record.shop.HistoryGUIRecord;
import cn.encmys.ykdz.forest.dailyshop.api.shop.Shop;
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
    public static Item toHistoryGuiItem(@NotNull Shop shop, @NotNull SettlementLog log, Player player) {
        HistoryGUIRecord record = ShopConfig.getHistoryGUIRecord(shop.getId());

        Map<String, String> vars = new HashMap<>() {{
            put("date", log.getTransitionTime().toString());
            put("price", String.valueOf(log.getPrice()));
            put("action", log.getType().name());
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
                put("amount", Integer.toString(stack * log.getTotalStack()));
            }}));
        }
        listVars.put("order-contents", orderContentsLines);

        List<String> lore = TextUtils.decorateText(record.historyIconRecord().formatLore(), player, vars, listVars);
        String name = TextUtils.decorateText(record.historyIconRecord().formatName(), player, vars);

        return new SimpleItem(
                new ItemBuilder(new cn.encmys.ykdz.forest.dailyshop.api.utils.ItemBuilder(Material.PAPER)
                        .setDisplayName(name)
                        .setLore(lore)
                        .build(log.getTotalStack())));
    }
}
