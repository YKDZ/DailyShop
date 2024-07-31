package cn.encmys.ykdz.forest.dailyshop.api.utils;

import cn.encmys.ykdz.forest.dailyshop.api.DailyShop;
import cn.encmys.ykdz.forest.dailyshop.api.config.ShopConfig;
import cn.encmys.ykdz.forest.dailyshop.api.config.record.shop.CartProductIconRecord;
import cn.encmys.ykdz.forest.dailyshop.api.gui.icon.AbstractIcon;
import cn.encmys.ykdz.forest.dailyshop.api.product.Product;
import cn.encmys.ykdz.forest.dailyshop.api.shop.Shop;
import cn.encmys.ykdz.forest.dailyshop.api.shop.order.ShopOrder;
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
    public static Item toCartGUIItem(@NotNull Shop shop, @NotNull ShopOrder cart, @NotNull String productId) {
        CartProductIconRecord iconRecord = ShopConfig.getCartGUIRecord(shop.getId()).cartProductIcon();
        Product product = DailyShop.PRODUCT_FACTORY.getProduct(productId);

        return new AbstractIcon() {
            @Override
            public ItemProvider getItemProvider() {
                int stack = cart.getOrderedProducts().getOrDefault(productId, 0);

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
                // TODO 尊重配置文件
                switch (clickType) {
                    case LEFT:
                        cart.modifyStack(product, 1);
                        notifyWindows();
                    case RIGHT:
                        cart.modifyStack(product, -1);
                        notifyWindows();
                    case DROP:
                        cart.setStack(product, 0);
                }
            }
        };
    }
}
