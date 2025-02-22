package cn.encmys.ykdz.forest.hyphashop.item.builder;

import cn.encmys.ykdz.forest.hyphashop.api.HyphaShop;
import cn.encmys.ykdz.forest.hyphashop.api.item.decorator.BaseItemDecorator;
import cn.encmys.ykdz.forest.hyphashop.api.item.decorator.enums.PropertyType;
import cn.encmys.ykdz.forest.hyphashop.api.product.Product;
import cn.encmys.ykdz.forest.hyphashop.api.product.factory.ProductFactory;
import cn.encmys.ykdz.forest.hyphashop.api.profile.Profile;
import cn.encmys.ykdz.forest.hyphashop.api.profile.enums.ShoppingMode;
import cn.encmys.ykdz.forest.hyphashop.api.shop.Shop;
import cn.encmys.ykdz.forest.hyphashop.api.shop.cashier.ShopCashier;
import cn.encmys.ykdz.forest.hyphashop.api.shop.order.ShopOrder;
import cn.encmys.ykdz.forest.hyphashop.api.shop.order.enums.OrderType;
import cn.encmys.ykdz.forest.hyphashop.api.shop.order.enums.SettlementResult;
import cn.encmys.ykdz.forest.hyphashop.api.shop.pricer.ShopPricer;
import cn.encmys.ykdz.forest.hyphashop.config.MessageConfig;
import cn.encmys.ykdz.forest.hyphashop.config.ShopConfig;
import cn.encmys.ykdz.forest.hyphashop.config.record.gui.ProductIconRecord;
import cn.encmys.ykdz.forest.hyphashop.product.BundleProduct;
import cn.encmys.ykdz.forest.hyphashop.shop.order.ShopOrderImpl;
import cn.encmys.ykdz.forest.hyphashop.utils.MessageUtils;
import cn.encmys.ykdz.forest.hyphashop.utils.PlayerUtils;
import cn.encmys.ykdz.forest.hyphashop.utils.TextUtils;
import cn.encmys.ykdz.forest.hyphashop.utils.VarUtils;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import xyz.xenondevs.invui.item.Item;
import xyz.xenondevs.invui.item.ItemBuilder;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ProductIconBuilder {
    public static Item build(@NotNull String shopId, @NotNull Product product) {
        ProductFactory productFactory = HyphaShop.PRODUCT_FACTORY;
        ProductIconRecord record = ShopConfig.getShopGUIRecord(shopId).productIconRecord();

        BaseItemDecorator iconDecorator = product.getIconDecorator();

        Item.Builder<?> icon = Item.builder()
                .setItemProvider((player) -> {
                    Shop shop = HyphaShop.SHOP_FACTORY.getShop(shopId);

                    if (shop == null) {
                        return new ItemBuilder(Material.AIR);
                    }

                    // 处理捆绑包商品的列表 lore
                    List<Object> bundleContentsLore = new ArrayList<>();
                    if (product instanceof BundleProduct) {
                        Map<String, Integer> bundleContents = ((BundleProduct) product).getBundleContents();
                        if (!bundleContents.isEmpty()) {
                            for (Map.Entry<String, Integer> entry : bundleContents.entrySet()) {
                                Product content = productFactory.getProduct(entry.getKey());
                                int stack = entry.getValue();
                                if (content == null) {
                                    continue;
                                }
                                bundleContentsLore.add(TextUtils.decorateText(record.formatBundleContentsLine(), player, new HashMap<>() {{
                                    put("name", content.getIconDecorator().getNameOrUseBaseItemName());
                                    put("amount", stack * shop.getShopCounter().getAmount(content.getId()));
                                    put("stack", stack);
                                    put("total_amount", stack * shop.getShopCounter().getAmount(content.getId()));
                                }}));
                            }
                        }
                    }

                    ShopPricer shopPricer = shop.getShopPricer();
                    // 关于产品自身的的变量
                    Map<String, Object> vars = new HashMap<>() {{
                        put("name", iconDecorator.getNameOrUseBaseItemName());
                        put("amount", shop.getShopCounter().getAmount(product.getId()));
                        put("buy_price", shopPricer.getBuyPrice(product.getId()) != -1d ? MessageConfig.format_decimal.format(shopPricer.getBuyPrice(product.getId())) : record.miscDisabledPrice());
                        put("sell_price", shopPricer.getSellPrice(product.getId()) != -1d ? MessageConfig.format_decimal.format(shopPricer.getSellPrice(product.getId())) : record.miscDisabledPrice());
                        put("current_global_stock", product.getProductStock().getCurrentGlobalAmount());
                        put("initial_global_stock", product.getProductStock().getInitialGlobalAmount());
                        put("current_player_stock", product.getProductStock().getCurrentPlayerAmount(player.getUniqueId()));
                        put("initial_player_stock", product.getProductStock().getInitialPlayerAmount());
                        put("rarity", product.getRarity().name());
                    }};
                    // 列表行的变量
                    Map<String, List<Object>> listVars = new HashMap<>() {{
                        put("desc_lore", iconDecorator.getProperty(PropertyType.LORE));
                        put("bundle_contents", bundleContentsLore);
                    }};

                    return new ItemBuilder(
                            new cn.encmys.ykdz.forest.hyphashop.utils.ItemBuilder(iconDecorator.getBaseItem().build(player))
                                    .setCustomModelData(iconDecorator.getProperty(PropertyType.CUSTOM_MODEL_DATA))
                                    .setItemFlags(iconDecorator.getProperty(PropertyType.ITEM_FLAGS))
                                    .setLore(TextUtils.decorateTextToComponent(record.formatLore(), player, vars, listVars))
                                    .setDisplayName(TextUtils.decorateTextToComponent(record.formatName(), player, vars))
                                    .setBannerPatterns(iconDecorator.getProperty(PropertyType.BANNER_PATTERNS))
                                    .setFireworkEffects(iconDecorator.getProperty(PropertyType.FIREWORK_EFFECTS))
                                    .setEnchantments(iconDecorator.getProperty(PropertyType.ENCHANTMENTS))
                                    .setPotionEffects(iconDecorator.getProperty(PropertyType.POTION_EFFECTS))
                                    .setArmorTrim(iconDecorator.getProperty(PropertyType.ARMOR_TRIM))
                                    .setEnchantable(iconDecorator.getProperty(PropertyType.ENCHANTABLE))
                                    .setGlider(iconDecorator.getProperty(PropertyType.GLIDER))
                                    .setFlightDuration(iconDecorator.getProperty(PropertyType.FLIGHT_DURATION))
                                    .setEnchantGlint(iconDecorator.getProperty(PropertyType.ENCHANT_GLINT))
                                    .setPotionCustomColor(iconDecorator.getProperty(PropertyType.POTION_COLOR))
                                    .setPotionType(iconDecorator.getProperty(PropertyType.POTION_TYPE))
                                    .setPotionCustomName(iconDecorator.getProperty(PropertyType.POTION_CUSTOM_NAME))
                                    .build(shop.getShopCounter().getAmount(product.getId())));
                })
                .addClickHandler((item, click) -> {
                    Player player = click.getPlayer();
                    Profile profile = HyphaShop.PROFILE_FACTORY.getProfile(player);
                    Shop shop = HyphaShop.SHOP_FACTORY.getShop(shopId);
                    if (shop == null) {
                        return;
                    }
                    Map<String, Object> vars = new HashMap<>() {{
                        putAll(VarUtils.extractVars(player, shop));
                        put("name", iconDecorator.getNameOrUseBaseItemName());
                        put("amount", shop.getShopCounter().getAmount(product.getId()));
                    }};
                    if (record.featuresSellTo() == click.getClickType() && profile.getShoppingMode(shopId) == ShoppingMode.DIRECT) {
                        sellToDirectly(shop, player, product, vars);
                    }
                    if (record.featuresBuyFrom() == click.getClickType() && profile.getShoppingMode(shopId) == ShoppingMode.DIRECT) {
                        buyFromDirectly(shop, player, product, vars);
                    }
                    if (record.featuresBuyAllFrom() == click.getClickType() && profile.getShoppingMode(shopId) == ShoppingMode.DIRECT) {
                        buyAllFromDirectly(shop, player, product, vars);
                    }
                    if (record.featuresAdd1ToCart() == click.getClickType() && profile.getShoppingMode(shopId) == ShoppingMode.CART) {
                        add1ToCart(player, shop, product, vars);
                    }
                    if (record.featuresRemove1FromCart() == click.getClickType() && profile.getShoppingMode(shopId) == ShoppingMode.CART) {
                        remove1FromCart(player, shop, product, vars);
                    }
                    if (record.featuresRemoveAllFromCart() == click.getClickType() && profile.getShoppingMode(shopId) == ShoppingMode.CART) {
                        removeAllFromCart(player, shop, product, vars);
                    }
                });

        // 根据需求设置是否点击时自动刷新和按时自动刷新
        if (product.getProductStock().isPlayerStock()
                || product.getProductStock().isGlobalStock()) {
            // 点击时自动刷新
            icon.updateOnClick();

            ProductIconRecord productIconRecord = ShopConfig.getShopGUIRecord(shopId).productIconRecord();
            if (productIconRecord.updatePeriod() > 0) {
                // 按时自动刷新
                icon.updatePeriodically(productIconRecord.updatePeriod());
            }
        }

        return icon.build();
    }

    private static void sellToDirectly(@NotNull Shop shop, @NotNull Player player, @NotNull Product product, @NotNull Map<String, Object> vars) {
        ShopCashier shopCashier = shop.getShopCashier();
        ShopOrder order =
                new ShopOrderImpl(player)
                        .setOrderType(OrderType.SELL_TO)
                        .modifyStack(product, 1);
        SettlementResult result = shopCashier.settle(order);
        if (result != SettlementResult.SUCCESS) {
            MessageUtils.sendMessage(player, MessageConfig.getShopOverrideableString(shop.getId(), "messages.action.shop.sell-to." + result.getConfigKey()), vars);
            PlayerUtils.playSound(ShopConfig.getSoundRecord(shop.getId(), "sell-to.failure"), player);
        } else {
            vars.put("cost", MessageConfig.format_decimal.format(order.getTotalPrice()));
            MessageUtils.sendMessage(player, MessageConfig.getShopOverrideableString(shop.getId(), "messages.action.shop.sell-to." + result.getConfigKey()), vars);
            PlayerUtils.playSound(ShopConfig.getSoundRecord(shop.getId(), "sell-to.success"), player);
        }
    }

    private static void buyFromDirectly(@NotNull Shop shop, @NotNull Player player, @NotNull Product product, @NotNull Map<String, Object> vars) {
        ShopCashier shopCashier = shop.getShopCashier();
        ShopOrder order =
                new ShopOrderImpl(player)
                        .setOrderType(OrderType.BUY_FROM)
                        .modifyStack(product, 1);
        SettlementResult result = shopCashier.settle(order);
        if (result != SettlementResult.SUCCESS) {
            MessageUtils.sendMessage(player, MessageConfig.getShopOverrideableString(shop.getId(), "messages.action.shop.buy-from." + result.getConfigKey()), vars);
            PlayerUtils.playSound(ShopConfig.getSoundRecord(shop.getId(), "buy-from.failure"), player);
        } else {
            vars.put("earn", MessageConfig.format_decimal.format(order.getTotalPrice()));
            MessageUtils.sendMessage(player, MessageConfig.getShopOverrideableString(shop.getId(), "messages.action.shop.buy-from." + result.getConfigKey()), vars);
            PlayerUtils.playSound(ShopConfig.getSoundRecord(shop.getId(), "buy-from.success"), player);
        }
    }

    private static void buyAllFromDirectly(@NotNull Shop shop, @NotNull Player player, @NotNull Product product, @NotNull Map<String, Object> vars) {
        ShopCashier shopCashier = shop.getShopCashier();
        ShopOrder order =
                new ShopOrderImpl(player)
                        .setOrderType(OrderType.BUY_ALL_FROM)
                        .modifyStack(product, 1);
        SettlementResult result = shopCashier.settle(order);
        if (result != SettlementResult.SUCCESS) {
            MessageUtils.sendMessage(player, MessageConfig.getShopOverrideableString(shop.getId(), "messages.action.shop.buy-all-from." + result.getConfigKey()), vars);
            PlayerUtils.playSound(ShopConfig.getSoundRecord(shop.getId(), "buy-all-from.failure"), player);
        } else {
            vars.put("earn", MessageConfig.format_decimal.format(order.getTotalPrice()));
            vars.put("stack", order.getOrderedProducts().get(product.getId()));
            MessageUtils.sendMessage(player, MessageConfig.getShopOverrideableString(shop.getId(), "messages.action.shop.buy-all-from." + result.getConfigKey()), vars);
            PlayerUtils.playSound(ShopConfig.getSoundRecord(shop.getId(), "buy-all-from.success"), player);
        }
    }

    private static void add1ToCart(@NotNull Player player, @NotNull Shop shop, @NotNull Product product, @NotNull Map<String, Object> vars) {
        Profile profile = HyphaShop.PROFILE_FACTORY.getProfile(player);
        ShopOrder cartOrder = profile.getCart().getOrder(shop.getId());
        // 构建一个新订单并等待被检查与合并
        // 避免反复检测购物车中的商品
        ShopOrder newOrder = new ShopOrderImpl(player)
                .setOrderType(cartOrder.getOrderType())
                .setStack(product, 1);
        // 一个订单在能被判断成功前必须被计算订单价值
        shop.getShopCashier().billOrder(newOrder);
        // 在一个限制或情况“无法被玩家解决”的情况下
        // 阻止玩家将商品加入购物车
        SettlementResult result = newOrder.getOrderType() == OrderType.SELL_TO ? shop.getShopCashier().canSellTo(newOrder) : shop.getShopCashier().canBuyFrom(newOrder);
        if (result == SettlementResult.SUCCESS) {
            cartOrder.combineOrder(newOrder);
            PlayerUtils.playSound(ShopConfig.getSoundRecord(shop.getId(), "add-1-to-cart.success"), player);
        } else {
            PlayerUtils.playSound(ShopConfig.getSoundRecord(shop.getId(), "add-1-to-cart.failure"), player);
        }
        MessageUtils.sendMessage(player, MessageConfig.getShopOverrideableString(shop.getId(), "messages.action.shop.add-1-to-cart.success"), vars);
    }

    private static void remove1FromCart(@NotNull Player player, @NotNull Shop shop, @NotNull Product product, @NotNull Map<String, Object> vars) {
        Profile profile = HyphaShop.PROFILE_FACTORY.getProfile(player);
        ShopOrder cartOrder = profile.getCart().getOrder(shop.getId());
        if (cartOrder.getOrderedProducts().containsKey(product.getId())) {
            cartOrder.setStack(product, cartOrder.getOrderedProducts().get(product.getId()) - 1);
        }
        MessageUtils.sendMessage(player, MessageConfig.getShopOverrideableString(shop.getId(), "messages.action.shop.remove-1-from-cart.success"), vars);
        PlayerUtils.playSound(ShopConfig.getSoundRecord(shop.getId(), "remove-1-from-cart.success"), player);
    }

    private static void removeAllFromCart(@NotNull Player player, @NotNull Shop shop, @NotNull Product product, @NotNull Map<String, Object> vars) {
        Profile profile = HyphaShop.PROFILE_FACTORY.getProfile(player);
        ShopOrder cartOrder = profile.getCart().getOrder(shop.getId());
        vars.put("stack", cartOrder.getOrderedProducts().get(product.getId()));
        if (cartOrder.getOrderedProducts().containsKey(product.getId())) {
            cartOrder.setStack(product, 0);
        }
        MessageUtils.sendMessage(player, MessageConfig.getShopOverrideableString(shop.getId(), "messages.action.shop.remove-all-from-cart.success"), vars);
        PlayerUtils.playSound(ShopConfig.getSoundRecord(shop.getId(), "remove-all-from-cart.success"), player);
    }
}
