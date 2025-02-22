package cn.encmys.ykdz.forest.hyphashop.item.builder;

import cn.encmys.ykdz.forest.hyphashop.api.HyphaShop;
import cn.encmys.ykdz.forest.hyphashop.api.product.Product;
import cn.encmys.ykdz.forest.hyphashop.api.profile.Profile;
import cn.encmys.ykdz.forest.hyphashop.api.shop.Shop;
import cn.encmys.ykdz.forest.hyphashop.api.shop.order.ShopOrder;
import cn.encmys.ykdz.forest.hyphashop.api.shop.order.enums.OrderType;
import cn.encmys.ykdz.forest.hyphashop.config.CartGUIConfig;
import cn.encmys.ykdz.forest.hyphashop.config.MessageConfig;
import cn.encmys.ykdz.forest.hyphashop.config.record.gui.CartProductIconRecord;
import cn.encmys.ykdz.forest.hyphashop.utils.ItemBuilder;
import cn.encmys.ykdz.forest.hyphashop.utils.TextUtils;
import cn.encmys.ykdz.forest.hyphashop.utils.VarUtils;
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

public class CartProductIconBuilder {
    @NotNull
    public static Item toCartGUIItem(@NotNull Shop shop, @NotNull ShopOrder cartOrder, @NotNull String productId) {
        CartProductIconRecord iconRecord = CartGUIConfig.getGUIRecord().cartProductIcon();
        Product product = HyphaShop.PRODUCT_FACTORY.getProduct(productId);

        if (product == null) return Item.simple(new ItemStack(Material.AIR));

        return Item.builder()
                .setItemProvider((player) -> {
                    int stack = cartOrder.getOrderedProducts().getOrDefault(productId, 0);
                    if (stack <= 0) {
                        return new xyz.xenondevs.invui.item.ItemBuilder(Material.AIR);
                    }

                    Map<String, Object> vars = new HashMap<>() {{
                        putAll(VarUtils.extractVars(player, shop));
                        putAll(VarUtils.extractVars(shop, product));
                        put("stack", stack);
                        {
                            // 保证购物车商品数量更新时能立刻看到价格变化
                            if (!cartOrder.isBilled()) {
                                shop.getShopCashier().billOrder(cartOrder);
                            }
                            put("total_price", cartOrder.getOrderType() == OrderType.SELL_TO ? MessageConfig.format_decimal.format(cartOrder.getBilledPrice(product)) : MessageConfig.placeholderAPI_cartTotalPrice_notSellToMode);
                        }
                    }};

                    Component name = TextUtils.decorateTextToComponent(iconRecord.formatName(), player, vars);
                    List<Component> lore = TextUtils.decorateTextToComponent(iconRecord.formatLore(), player, vars, null);

                    return new xyz.xenondevs.invui.item.ItemBuilder(
                            new ItemBuilder(product.getIconDecorator().getBaseItem().build(player))
                                    .setDisplayName(name)
                                    .setLore(lore)
                                    .build(stack)
                    );
                })
                .addClickHandler((item, click) -> {
                    Player player = click.getPlayer();
                    ClickType clickType = click.getClickType();

                    Profile profile = HyphaShop.PROFILE_FACTORY.getProfile(player);
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
                        HyphaShop.PROFILE_FACTORY.getProfile(player).pickProductStack(shop, productId);
                    }
                    profile.getCartGUI().loadContent(player);
                })
                // 购物车商品图标不刷新
                .build();
    }
}
