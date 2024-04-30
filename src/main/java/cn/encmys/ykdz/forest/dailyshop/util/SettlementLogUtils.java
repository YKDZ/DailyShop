package cn.encmys.ykdz.forest.dailyshop.util;

import cn.encmys.ykdz.forest.dailyshop.config.ShopConfig;
import cn.encmys.ykdz.forest.dailyshop.shop.Shop;
import cn.encmys.ykdz.forest.dailyshop.shop.cashier.log.SettlementLog;
import cn.encmys.ykdz.forest.dailyshop.shop.cashier.log.enums.SettlementLogType;
import org.bukkit.Material;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import xyz.xenondevs.invui.item.Item;
import xyz.xenondevs.invui.item.builder.ItemBuilder;
import xyz.xenondevs.invui.item.impl.SimpleItem;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class SettlementLogUtils {
    @NotNull
    public static Item toHistoryGuiItem(@NotNull Shop shop, @NotNull SettlementLog log, @Nullable Player player) {
        ConfigurationSection section = ShopConfig.getHistoryGuiSection(shop.getId()).getConfigurationSection("history-icon");

        Map<String, String> vars = new HashMap<>() {{
            put("date", log.getTransitionTime().toString());
            put("price", String.valueOf(log.getPrice()));
            put("action", log.getType().name());
        }};

        String orderContentsLineFormat = section.getString("format.order-contents-line");
        Map<String, List<String>> listVars = new HashMap<>();
        List<String> orderContentsLines = new ArrayList<>();
        List<String> names = log.getOrderedProductNames();
        List<Integer> stacks = log.getOrderedProductStacks();
        for (int i = 0; i < names.size(); i++) {
            String name = names.get(i);
            int stack = stacks.get(i);

            orderContentsLines.add(TextUtils.parseInternalVariables(orderContentsLineFormat, new HashMap<>() {{
                put("name", name);
                put("amount", Integer.toString(stack * log.getTotalStack()));
            }}));
        }
        listVars.put("order-contents", orderContentsLines);

        List<String> loreFormats = section.getStringList("format.lore");
        List<String> lore = TextUtils.decorateTextWithListVar(loreFormats, null, listVars, vars);

        String name = TextUtils.decorateTextWithVar(section.getString("format.name"), null, vars);

        return new SimpleItem(
                new ItemBuilder(new cn.encmys.ykdz.forest.dailyshop.util.ItemBuilder(Material.PAPER)
                        .setDisplayName(name)
                        .setLore(lore)
                        .build(log.getTotalStack())));
    }

    public static int getHistoryAmount(List<SettlementLog> logs, String productId, SettlementLogType... types) {
//        logs = LogStream.of(logs)
//                .withProduct(productId)
//                .withType(types)
//                .toList();

        int amount = 0;
        for (SettlementLog log : logs) {
            List<String> ids = log.getOrderedProductIds();
            amount += log.getOrderedProductStacks().get(ids.indexOf(productId)) * log.getTotalStack();
        }

        return amount;
    }
}
