package cn.encmys.ykdz.forest.hyphashop.item.builder;

import cn.encmys.ykdz.forest.hyphashop.api.HyphaShop;
import cn.encmys.ykdz.forest.hyphashop.api.product.Product;
import cn.encmys.ykdz.forest.hyphashop.api.shop.Shop;
import cn.encmys.ykdz.forest.hyphashop.api.shop.cashier.log.SettlementLog;
import cn.encmys.ykdz.forest.hyphashop.api.shop.cashier.log.amount.AmountPair;
import cn.encmys.ykdz.forest.hyphashop.config.MessageConfig;
import cn.encmys.ykdz.forest.hyphashop.config.OrderHistoryGUIConfig;
import cn.encmys.ykdz.forest.hyphashop.config.record.gui.OrderHistoryGUIRecord;
import cn.encmys.ykdz.forest.hyphashop.utils.TextUtils;
import cn.encmys.ykdz.forest.hyphashop.utils.VarUtils;
import net.kyori.adventure.text.Component;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;
import xyz.xenondevs.invui.item.Item;
import xyz.xenondevs.invui.item.ItemBuilder;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class OrderHistoryIconBuilder {
    @NotNull
    private static final OrderHistoryGUIRecord record = OrderHistoryGUIConfig.getGUIRecord();

    @NotNull
    public static Item build(@NotNull SettlementLog log, @NotNull Player player) {
        return Item.builder()
                .async(new ItemBuilder(Material.AIR),
                        () -> {
                            Shop shop = HyphaShop.SHOP_FACTORY.getShop(log.getSettledShopId());
                            // 构造内部变量
                            Map<String, Object> vars = new HashMap<>() {{
                                putAll(VarUtils.extractVars(player, shop));
                                put("date", MessageConfig.format_date.format(log.getTransitionTime()));
                                put("type", MessageConfig.getTerm(log.getType()));
                                put("total_price", MessageConfig.format_decimal.format(log.getTotalPrice()));
                            }};
                            // 构造内部列表变量
                            Map<String, List<Object>> listVars = new HashMap<>() {{
                                List<Object> orderContentsLines = new ArrayList<>();
                                for (Map.Entry<String, AmountPair> entry : log.getOrderedProducts().entrySet()) {
                                    String productId = entry.getKey();
                                    AmountPair amountPair = entry.getValue();
                                    Product product = HyphaShop.PRODUCT_FACTORY.getProduct(productId);
                                    if (product == null) {
                                        orderContentsLines.add(TextUtils.decorateText(record.historyIconRecord().formatInvalidOrderContentLine(), player, new HashMap<>() {{
                                            VarUtils.extractVars(shop, null);
                                            putAll(vars);
                                            put("stack", amountPair.stack());
                                            put("product_id", productId);
                                        }}));
                                    } else {
                                        orderContentsLines.add(TextUtils.decorateText(record.historyIconRecord().formatOrderContentLine(), player, new HashMap<>() {{
                                            VarUtils.extractVars(shop, product);
                                            putAll(vars);
                                            put("stack", amountPair.stack());
                                        }}));
                                    }
                                }
                                put("order_contents", orderContentsLines);
                            }};

                            // 构建显示物品
                            ItemStack displayItem = null;
                            for (String id : log.getOrderedProducts().keySet()) {
                                Product product = HyphaShop.PRODUCT_FACTORY.getProduct(id);
                                // 其他插件的物品不保证在异步环境下能被构建
                                // 例如 MMOItems
                                if (product != null && product.getIconDecorator().getBaseItem().getItemType().isAsyncBuildable()) {
                                    displayItem = product.getIconDecorator().getBaseItem().build(player);
                                    break;
                                }
                            }

                            if (displayItem == null) {
                                if (record.historyIconRecord().miscPlaceholderIcon() == null) {
                                    displayItem = new ItemStack(Material.PAPER);
                                } else {
                                    // 这里传递 shop 不知道是否会导致问题
                                    return NormalIconBuilder.build(record.historyIconRecord().miscPlaceholderIcon(), shop)
                                            .getItemProvider(player);
                                }
                            }

                            List<Component> lore = TextUtils.decorateTextToComponent(record.historyIconRecord().formatLore(), player, vars, listVars);
                            Component name = TextUtils.decorateTextToComponent(record.historyIconRecord().formatName(), player, vars);

                            return new ItemBuilder(new cn.encmys.ykdz.forest.hyphashop.utils.ItemBuilder(displayItem)
                                    .setDisplayName(name)
                                    .setLore(lore)
                                    .build(1)
                            );
                        })
                .build();
    }
}
