package cn.encmys.ykdz.forest.dailyshop.item.builder;

import cn.encmys.ykdz.forest.dailyshop.api.DailyShop;
import cn.encmys.ykdz.forest.dailyshop.api.config.MessageConfig;
import cn.encmys.ykdz.forest.dailyshop.api.config.OrderHistoryGUIConfig;
import cn.encmys.ykdz.forest.dailyshop.api.config.record.gui.OrderHistoryGUIRecord;
import cn.encmys.ykdz.forest.dailyshop.api.item.decorator.enums.PropertyType;
import cn.encmys.ykdz.forest.dailyshop.api.product.Product;
import cn.encmys.ykdz.forest.dailyshop.api.profile.Profile;
import cn.encmys.ykdz.forest.dailyshop.api.shop.Shop;
import cn.encmys.ykdz.forest.dailyshop.api.shop.cashier.log.SettlementLog;
import cn.encmys.ykdz.forest.dailyshop.api.utils.TextUtils;
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
                            // 构造内部变量
                            Map<String, String> vars = new HashMap<>() {{
                                put("date", MessageConfig.format_date.format(log.getTransitionTime()));
                                put("price", MessageConfig.format_decimal.format(log.getTotalPrice()));
                                put("type", MessageConfig.getTerm(log.getType()));
                                put("total-price", MessageConfig.format_decimal.format(log.getTotalPrice()));
                                Shop shop = DailyShop.SHOP_FACTORY.getShop(log.getSettledShop());
                                if (shop != null) {
                                    put("shop-name", shop.getName());
                                }
                            }};
                            // 构造内部列表变量
                            Map<String, List<String>> listVars = new HashMap<>() {{
                                List<String> orderContentsLines = new ArrayList<>();
                                Map<String, Integer> orderedProducts = log.getOrderedProducts();
                                for (Map.Entry<String, Integer> entry : orderedProducts.entrySet()) {
                                    String productId = entry.getKey();
                                    int stack = entry.getValue();
                                    Product product = DailyShop.PRODUCT_FACTORY.getProduct(productId);
                                    if (product == null) {
                                        orderContentsLines.add(TextUtils.decorateText(record.historyIconRecord().formatInvalidOrderContentLine(), player, new HashMap<>() {{
                                            put("id", productId);
                                            put("stack", Integer.toString(stack));
                                        }}));
                                    } else {
                                        orderContentsLines.add(TextUtils.decorateText(record.historyIconRecord().formatOrderContentLine(), player, new HashMap<>() {{
                                            put("name", product.getIconDecorator().getProperty(PropertyType.NAME));
                                            put("stack", Integer.toString(stack));
                                        }}));
                                    }
                                }
                                put("order-contents", orderContentsLines);
                            }};

                            // 构建显示物品
                            ItemStack displayItem = null;
                            for (String id : log.getOrderedProducts().keySet()) {
                                Product product = DailyShop.PRODUCT_FACTORY.getProduct(id);
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
                                    Profile profile = DailyShop.PROFILE_FACTORY.getProfile(player);
                                    return NormalIconBuilder.build(record.historyIconRecord().miscPlaceholderIcon(), null, profile.getOrderHistoryGUI(), player, vars, listVars)
                                            .getItemProvider(player);
                                }
                            }

                            List<Component> lore = TextUtils.decorateTextToComponent(record.historyIconRecord().formatLore(), player, vars, listVars);
                            Component name = TextUtils.decorateTextToComponent(record.historyIconRecord().formatName(), player, vars);

                            return new ItemBuilder(new cn.encmys.ykdz.forest.dailyshop.api.utils.ItemBuilder(displayItem)
                                    .setDisplayName(name)
                                    .setLore(lore)
                                    .build(1)
                            );
                        })
                .build();
    }
}
