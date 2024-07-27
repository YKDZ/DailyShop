package cn.encmys.ykdz.forest.dailyshop.api.utils;

import cn.encmys.ykdz.forest.dailyshop.api.DailyShop;
import cn.encmys.ykdz.forest.dailyshop.api.config.ShopConfig;
import cn.encmys.ykdz.forest.dailyshop.api.config.record.shop.CartProductIconRecord;
import cn.encmys.ykdz.forest.dailyshop.api.product.Product;
import cn.encmys.ykdz.forest.dailyshop.api.shop.Shop;
import cn.encmys.ykdz.forest.dailyshop.api.shop.order.ShopOrder;
import org.bukkit.Material;
import org.jetbrains.annotations.NotNull;
import xyz.xenondevs.invui.item.Item;
import xyz.xenondevs.invui.item.impl.SimpleItem;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class OrderUtils {
    public static Item toCartGUIItem(@NotNull Shop shop, @NotNull ShopOrder order, @NotNull String productId) {
        CartProductIconRecord iconRecord = ShopConfig.getCartGUIRecord(shop.getId()).cartProductIcon();
        Product product = DailyShop.PRODUCT_FACTORY.getProduct(productId);
        int stack = order.getOrderedProducts().get(productId);

        if (product == null) {
            return null;
        }

        Map<String, String> vars = new HashMap<>() {{
            put("name", product.getIconDecorator().getName());
            if (product.getItemDecorator() != null) {
                put("amount", String.valueOf(product.getItemDecorator().getAmount()));
            }
            put("stack", String.valueOf(stack));
        }};

        String name = TextUtils.decorateText(iconRecord.formatName(), null, vars);
        List<String> lore = TextUtils.decorateText(iconRecord.formatLore(), null, vars, null);

        return new SimpleItem(
                new xyz.xenondevs.invui.item.builder.ItemBuilder(
                        new cn.encmys.ykdz.forest.dailyshop.api.utils.ItemBuilder(Material.PAPER)
                            .setDisplayName(name)
                            .setLore(lore)
                            .build(stack)
                )
        );
    }
}
