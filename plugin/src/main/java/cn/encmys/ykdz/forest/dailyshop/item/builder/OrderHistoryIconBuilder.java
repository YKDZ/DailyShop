package cn.encmys.ykdz.forest.dailyshop.item.builder;

import cn.encmys.ykdz.forest.dailyshop.api.DailyShop;
import cn.encmys.ykdz.forest.dailyshop.api.config.MessageConfig;
import cn.encmys.ykdz.forest.dailyshop.api.config.OrderHistoryGUIConfig;
import cn.encmys.ykdz.forest.dailyshop.api.config.record.gui.OrderHistoryGUIRecord;
import cn.encmys.ykdz.forest.dailyshop.api.product.Product;
import cn.encmys.ykdz.forest.dailyshop.api.shop.cashier.log.SettlementLog;
import cn.encmys.ykdz.forest.dailyshop.api.utils.TextUtils;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;
import xyz.xenondevs.invui.item.Item;
import xyz.xenondevs.invui.item.builder.ItemBuilder;
import xyz.xenondevs.invui.item.impl.AsyncItem;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class OrderHistoryIconBuilder {
    private static final OrderHistoryGUIRecord record = OrderHistoryGUIConfig.getGUIRecord();

    @NotNull
    public static Item build(@NotNull SettlementLog log, Player player) {
        return new AsyncItem(
                new ItemBuilder(Material.AIR),
                () -> {
                    Map<String, String> vars = new HashMap<>() {{
                        put("date", MessageConfig.format_date.format(log.getTransitionTime()));
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

                    // 配置文件
                    ItemStack displayItem = new ItemStack(Material.PAPER);
                    if (!log.getOrderedProductIds().isEmpty()) {
                        String productId = log.getOrderedProductIds().get(0);
                        Product product = DailyShop.PRODUCT_FACTORY.getProduct(productId);
                        if (product != null) {
                            displayItem = product.getIconDecorator().getBaseItem().build(player);
                        }
                    }

                    return new ItemBuilder(new cn.encmys.ykdz.forest.dailyshop.api.utils.ItemBuilder(displayItem)
                            .setDisplayName(name)
                            .setLore(lore)
                            .build(1)
                    );
                }
        );
    }
}
