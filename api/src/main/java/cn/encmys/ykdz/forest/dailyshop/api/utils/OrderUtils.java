package cn.encmys.ykdz.forest.dailyshop.api.utils;

import cn.encmys.ykdz.forest.dailyshop.api.DailyShop;
import cn.encmys.ykdz.forest.dailyshop.api.config.CartGUIConfig;
import cn.encmys.ykdz.forest.dailyshop.api.config.MessageConfig;
import cn.encmys.ykdz.forest.dailyshop.api.config.record.gui.CartProductIconRecord;
import cn.encmys.ykdz.forest.dailyshop.api.item.decorator.enums.PropertyType;
import cn.encmys.ykdz.forest.dailyshop.api.product.Product;
import cn.encmys.ykdz.forest.dailyshop.api.profile.Profile;
import cn.encmys.ykdz.forest.dailyshop.api.shop.Shop;
import cn.encmys.ykdz.forest.dailyshop.api.shop.order.ShopOrder;
import cn.encmys.ykdz.forest.dailyshop.api.shop.order.enums.OrderType;
import net.kyori.adventure.text.Component;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;
import xyz.xenondevs.invui.item.Item;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

public class OrderUtils {
    @NotNull
    public static Item toCartGUIItem(@NotNull Shop shop, @NotNull ShopOrder cartOrder, @NotNull String productId) {
        CartProductIconRecord iconRecord = CartGUIConfig.getGUIRecord().cartProductIcon();
        Product product = DailyShop.PRODUCT_FACTORY.getProduct(productId);

        if (product == null) return Item.simple(new ItemStack(Material.AIR));

        return Item.builder()
                .setItemProvider((player) -> {
                    int stack = cartOrder.getOrderedProducts().getOrDefault(productId, 0);
                    if (stack <= 0) {
                        return new xyz.xenondevs.invui.item.ItemBuilder(Material.AIR);
                    }

                    Map<String, String> vars = new HashMap<>() {{
                        put("name", product.getIconDecorator().getProperty(PropertyType.NAME));
                        Integer amount;
                        if (product.getProductItemDecorator() != null) {
                            amount = product.getProductItemDecorator().getProperty(PropertyType.AMOUNT);
                        } else {
                            amount = product.getIconDecorator().getProperty(PropertyType.AMOUNT);
                        }
                        put("amount", amount != null ? String.valueOf(amount) : "0");
                        put("stack", String.valueOf(stack));
                        // 保证购物车商品数量更新时能立刻看到价格变化
                        if (!cartOrder.isBilled()) {
                            shop.getShopCashier().billOrder(cartOrder);
                        }
                        put("price", cartOrder.getOrderType() == OrderType.SELL_TO ? MessageConfig.format_decimal.format(cartOrder.getBilledPrice(product)) : MessageConfig.placeholderAPI_cartTotalPrice_notSellToMode);
                    }};

                    Component name = TextUtils.decorateTextToComponent(iconRecord.formatName(), null, vars);
                    List<Component> lore = TextUtils.decorateTextToComponent(iconRecord.formatLore(), null, vars, null);

                    return new xyz.xenondevs.invui.item.ItemBuilder(
                            new ItemBuilder(product.getIconDecorator().getBaseItem().build(null))
                                    .setDisplayName(name)
                                    .setLore(lore)
                                    .build(stack)
                    );
                })
                .addClickHandler((item, click) -> {
                    Player player = click.getPlayer();
                    ClickType clickType = click.getClickType();

                    Profile profile = DailyShop.PROFILE_FACTORY.getProfile(player);
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
                    profile.getCartGUI().loadContent(player);
                })
                .updatePeriodically((Long) Optional.ofNullable(product.getIconDecorator().getProperty(PropertyType.UPDATE_PERIOD)).orElse(-1L))
                .build();
    }
}
