package cn.encmys.ykdz.forest.dailyshop.api.utils;

import cn.encmys.ykdz.forest.dailyshop.api.DailyShop;
import cn.encmys.ykdz.forest.dailyshop.api.config.CartGUIConfig;
import cn.encmys.ykdz.forest.dailyshop.api.config.MessageConfig;
import cn.encmys.ykdz.forest.dailyshop.api.config.record.shop.CartProductIconRecord;
import cn.encmys.ykdz.forest.dailyshop.api.gui.icon.AbstractIcon;
import cn.encmys.ykdz.forest.dailyshop.api.product.Product;
import cn.encmys.ykdz.forest.dailyshop.api.shop.Shop;
import cn.encmys.ykdz.forest.dailyshop.api.shop.order.ShopOrder;
import cn.encmys.ykdz.forest.dailyshop.api.shop.order.enums.OrderType;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.jetbrains.annotations.NotNull;
import xyz.xenondevs.invui.item.Item;
import xyz.xenondevs.invui.item.ItemProvider;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class OrderUtils {
    @NotNull
    public static Item toCartGUIItem(@NotNull Shop shop, @NotNull ShopOrder cartOrder, @NotNull String productId) {
        CartProductIconRecord iconRecord = CartGUIConfig.getGUIRecord().cartProductIcon();
        Product product = DailyShop.PRODUCT_FACTORY.getProduct(productId);

        AbstractIcon icon = new AbstractIcon() {
            @Override
            public ItemProvider getItemProvider() {
                int stack = cartOrder.getOrderedProducts().getOrDefault(productId, 0);

                if (stack <= 0 || product == null) {
                    return new xyz.xenondevs.invui.item.builder.ItemBuilder(Material.AIR);
                }

                Map<String, String> vars = new HashMap<>() {{
                    put("name", product.getIconDecorator().getName());
                    if (product.getItemDecorator() != null) {
                        put("amount", String.valueOf(product.getItemDecorator().getAmount()));
                    } else {
                        put("amount", String.valueOf(product.getIconDecorator().getAmount()));
                    }
                    put("stack", String.valueOf(stack));
                    // 保证购物车商品数量更新时能立刻看到价格变化
                    if (!cartOrder.isBilled()) {
                        shop.getShopCashier().billOrder(cartOrder);
                    }
                    put("price", cartOrder.getOrderType() == OrderType.SELL_TO ? MessageConfig.format_decimal.format(cartOrder.getBilledPrice(product)) : MessageConfig.placeholderAPI_cartTotalPrice_notSellToMode);
                }};

                String name = TextUtils.decorateText(iconRecord.formatName(), null, vars);
                List<String> lore = TextUtils.decorateText(iconRecord.formatLore(), null, vars, null);

                return new xyz.xenondevs.invui.item.builder.ItemBuilder(
                        new ItemBuilder(product.getIconDecorator().getBaseItem().build(null))
                                .setDisplayName(name)
                                .setLore(lore)
                                .build(stack)
                );
            }

            @Override
            public void handleClick(@NotNull ClickType clickType, @NotNull Player player, @NotNull InventoryClickEvent event) {
                if (iconRecord.featuresAdd1Stack() == clickType) {
                    cartOrder.modifyStack(product, 1);
                }
                if (iconRecord.featuresRemove1Stack() == clickType) {
                    cartOrder.modifyStack(product, -1);
                }
                if (iconRecord.featuresRemoveAll() == clickType) {
                    cartOrder.setStack(product, 0);
                }
                if (iconRecord.featuresInputInAnvil() == clickType) {
                    DailyShop.PROFILE_FACTORY.getProfile(player).pickProductStack(shop, productId);
                }
                notifyWindows();
            }
        };

        if (iconRecord.updatePeriod() > 0) {
            icon.startUpdater(iconRecord.updatePeriod());
        }

        return icon;
    }
}
