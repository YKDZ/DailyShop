package cn.encmys.ykdz.forest.dailyshop.item.builder;

import cn.encmys.ykdz.forest.dailyshop.api.DailyShop;
import cn.encmys.ykdz.forest.dailyshop.api.config.MessageConfig;
import cn.encmys.ykdz.forest.dailyshop.api.config.ShopConfig;
import cn.encmys.ykdz.forest.dailyshop.api.config.record.gui.ProductIconRecord;
import cn.encmys.ykdz.forest.dailyshop.api.gui.icon.AbstractIcon;
import cn.encmys.ykdz.forest.dailyshop.api.item.decorator.BaseItemDecorator;
import cn.encmys.ykdz.forest.dailyshop.api.product.Product;
import cn.encmys.ykdz.forest.dailyshop.api.product.factory.ProductFactory;
import cn.encmys.ykdz.forest.dailyshop.api.profile.Profile;
import cn.encmys.ykdz.forest.dailyshop.api.profile.enums.ShoppingMode;
import cn.encmys.ykdz.forest.dailyshop.api.shop.Shop;
import cn.encmys.ykdz.forest.dailyshop.api.shop.cashier.ShopCashier;
import cn.encmys.ykdz.forest.dailyshop.api.shop.order.ShopOrder;
import cn.encmys.ykdz.forest.dailyshop.api.shop.order.enums.OrderType;
import cn.encmys.ykdz.forest.dailyshop.api.shop.order.enums.SettlementResult;
import cn.encmys.ykdz.forest.dailyshop.api.shop.pricer.ShopPricer;
import cn.encmys.ykdz.forest.dailyshop.api.utils.PlayerUtils;
import cn.encmys.ykdz.forest.dailyshop.api.utils.TextUtils;
import cn.encmys.ykdz.forest.dailyshop.product.BundleProduct;
import cn.encmys.ykdz.forest.dailyshop.shop.order.ShopOrderImpl;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.jetbrains.annotations.NotNull;
import xyz.xenondevs.invui.item.Item;
import xyz.xenondevs.invui.item.ItemProvider;
import xyz.xenondevs.invui.item.builder.ItemBuilder;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ProductIconBuilder {
    public static Item build(@NotNull BaseItemDecorator decorator, @NotNull Player player, @NotNull String shopId, @NotNull Product product) {
        ProductFactory productFactory = DailyShop.PRODUCT_FACTORY;
        ProductIconRecord record = ShopConfig.getShopGUIRecord(shopId).productIconRecord();
        AbstractIcon icon = new AbstractIcon() {
            @Override
            public ItemProvider getItemProvider() {
                Shop shop = DailyShop.SHOP_FACTORY.getShop(shopId);

                if (shop == null) {
                    return new ItemBuilder(Material.AIR);
                }

                // 处理捆绑包商品的列表 lore
                List<String> bundleContentsLore = new ArrayList<>();
                if (product instanceof BundleProduct) {
                    Map<String, Integer> bundleContents = ((BundleProduct) product).getBundleContents();
                    if (!bundleContents.isEmpty()) {
                        for (Map.Entry<String, Integer> entry : bundleContents.entrySet()) {
                            Product content = productFactory.getProduct(entry.getKey());
                            int stack = entry.getValue();
                            if (content == null) {
                                continue;
                            }
                            bundleContentsLore.add(TextUtils.decorateTextKeepMiniMessage(record.formatBundleContentsLine(), null, new HashMap<>() {{
                                put("name", content.getIconDecorator().getName());
                                put("amount", String.valueOf(content.getItemDecorator() != null ? content.getItemDecorator().getAmount() : content.getIconDecorator().getAmount()));
                                put("stack", String.valueOf(stack));
                                put("total-amount", String.valueOf(stack * (content.getItemDecorator() != null ? content.getItemDecorator().getAmount() : content.getIconDecorator().getAmount())));
                            }}));
                        }
                    }
                }

                ShopPricer shopPricer = shop.getShopPricer();
                // 关于产品自身的的变量
                Map<String, String> vars = new HashMap<>() {{
                    put("name", decorator.getName());
                    put("amount", String.valueOf(decorator.getAmount()));
                    put("buy-price", shopPricer.getBuyPrice(product.getId()) != -1d ? MessageConfig.format_decimal.format(shopPricer.getBuyPrice(product.getId())) : record.miscDisabledPrice());
                    put("sell-price", shopPricer.getSellPrice(product.getId()) != -1d ? MessageConfig.format_decimal.format(shopPricer.getSellPrice(product.getId())) : record.miscDisabledPrice());
                    put("current-global-stock", String.valueOf(product.getProductStock().getCurrentGlobalAmount()));
                    put("initial-global-stock", String.valueOf(product.getProductStock().getInitialGlobalAmount()));
                    put("current-player-stock", String.valueOf(product.getProductStock().getCurrentPlayerAmount(player.getUniqueId())));
                    put("initial-player-stock", String.valueOf(product.getProductStock().getInitialPlayerAmount()));
                    put("rarity", product.getRarity().name());
                }};
                // 列表行的变量
                Map<String, List<String>> listVars = new HashMap<>() {{
                    put("desc-lore", decorator.getLore());
                    put("bundle-contents", bundleContentsLore);
                }};

                return new ItemBuilder(
                        new cn.encmys.ykdz.forest.dailyshop.api.utils.ItemBuilder(decorator.getBaseItem().build(null))
                                .setCustomModelData(decorator.getCustomModelData())
                                .setItemFlags(decorator.getItemFlags())
                                .setLore(TextUtils.decorateText(record.formatLore(), null, vars, listVars))
                                .setDisplayName(TextUtils.decorateText(record.formatName(), null, vars))
                                .setBannerPatterns(decorator.getPatternsData())
                                .setFireworkEffects(decorator.getFireworkEffectData())
                                .build(decorator.getAmount()));
            }

            @Override
            public void handleClick(@NotNull ClickType clickType, @NotNull Player player, @NotNull InventoryClickEvent event) {
                Profile profile = DailyShop.PROFILE_FACTORY.getProfile(player);
                Shop shop = DailyShop.SHOP_FACTORY.getShop(shopId);
                if (shop == null) {
                    return;
                }
                Map<String, String> vars = new HashMap<>() {{
                    put("name", decorator.getName());
                    put("amount", String.valueOf(decorator.getAmount()));
                    put("shop-name", shop.getName());
                    put("shop-id", shop.getId());
                    put("cart-mode", MessageConfig.getTerm(profile.getCart().getMode()));
                }};

                if (record.featuresSellTo() == clickType && profile.getShoppingMode(shopId) == ShoppingMode.DIRECT) {
                    sellToDirectly(shop, player, product, vars);
                }
                if (record.featuresBuyFrom() == clickType && profile.getShoppingMode(shopId) == ShoppingMode.DIRECT) {
                    buyFromDirectly(shop, player, product, vars);
                }
                if (record.featuresBuyAllFrom() == clickType && profile.getShoppingMode(shopId) == ShoppingMode.DIRECT) {
                    buyAllFromDirectly(shop, player, product, vars);
                }
                if (record.featuresAdd1ToCart() == clickType && profile.getShoppingMode(shopId) == ShoppingMode.CART) {
                    add1ToCart(player, shop, product, vars);
                }
                if (record.featuresRemove1FromCart() == clickType && profile.getShoppingMode(shopId) == ShoppingMode.CART) {
                    remove1FromCart(player, shop, product, vars);
                }
                if (record.featuresRemoveAllFromCart() == clickType && profile.getShoppingMode(shopId) == ShoppingMode.CART) {
                    removeAllFromCart(player, shop, product, vars);
                }
            }
        };
        // 根据需求设置是否自动刷新
        if (product.getProductStock().isPlayerStock()
                || product.getProductStock().isGlobalStock()) {
            ProductIconRecord productIconRecord = ShopConfig.getShopGUIRecord(shopId).productIconRecord();
            if (productIconRecord.updatePeriod() > 0) {
                icon.startUpdater(productIconRecord.updatePeriod());
            }
        }
        return icon;
    }

    private static void sellToDirectly(Shop shop, Player player, Product product, Map<String, String> vars) {
        ShopCashier shopCashier = shop.getShopCashier();
        ShopOrder order =
                new ShopOrderImpl(player)
                        .setOrderType(OrderType.SELL_TO)
                        .modifyStack(product, 1);
        SettlementResult result = shopCashier.settle(order);
        if (result != SettlementResult.SUCCESS) {
            PlayerUtils.sendMessage(MessageConfig.getShopOverrideableString(shop.getId(), "messages.action.shop.sell-to." + result.getConfigKey()), player, vars);
            PlayerUtils.playSound(ShopConfig.getSoundRecord(shop.getId(), "sell-to.failure"), player);
        } else {
            vars.put("cost", MessageConfig.format_decimal.format(order.getTotalPrice()));
            PlayerUtils.sendMessage(MessageConfig.getShopOverrideableString(shop.getId(), "messages.action.shop.sell-to." + result.getConfigKey()), player, vars);
            PlayerUtils.playSound(ShopConfig.getSoundRecord(shop.getId(), "sell-to.success"), player);
        }
    }

    private static void buyFromDirectly(Shop shop, Player player, Product product, Map<String, String> vars) {
        ShopCashier shopCashier = shop.getShopCashier();
        ShopOrder order =
                new ShopOrderImpl(player)
                        .setOrderType(OrderType.BUY_FROM)
                        .modifyStack(product, 1);
        SettlementResult result = shopCashier.settle(order);
        if (result != SettlementResult.SUCCESS) {
            PlayerUtils.sendMessage(MessageConfig.getShopOverrideableString(shop.getId(), "messages.action.shop.buy-from." + result.getConfigKey()), player, vars);
            PlayerUtils.playSound(ShopConfig.getSoundRecord(shop.getId(), "buy-from.failure"), player);
        } else {
            vars.put("earn", MessageConfig.format_decimal.format(order.getTotalPrice()));
            PlayerUtils.sendMessage(MessageConfig.getShopOverrideableString(shop.getId(), "messages.action.shop.buy-from." + result.getConfigKey()), player, vars);
            PlayerUtils.playSound(ShopConfig.getSoundRecord(shop.getId(), "buy-from.success"), player);
        }
    }

    private static void buyAllFromDirectly(Shop shop, Player player, Product product, Map<String, String> vars) {
        ShopCashier shopCashier = shop.getShopCashier();
        ShopOrder order =
                new ShopOrderImpl(player)
                        .setOrderType(OrderType.BUY_ALL_FROM)
                        .modifyStack(product, 1);
        SettlementResult result = shopCashier.settle(order);
        if (result != SettlementResult.SUCCESS) {
            PlayerUtils.sendMessage(MessageConfig.getShopOverrideableString(shop.getId(), "messages.action.shop.buy-all-from." + result.getConfigKey()), player, vars);
            PlayerUtils.playSound(ShopConfig.getSoundRecord(shop.getId(), "buy-all-from.failure"), player);
        } else {
            vars.put("earn", MessageConfig.format_decimal.format(order.getTotalPrice()));
            vars.put("stack", String.valueOf(order.getOrderedProducts().get(product.getId())));
            PlayerUtils.sendMessage(MessageConfig.getShopOverrideableString(shop.getId(), "messages.action.shop.buy-all-from." + result.getConfigKey()), player, vars);
            PlayerUtils.playSound(ShopConfig.getSoundRecord(shop.getId(), "buy-all-from.success"), player);
        }
    }

    private static void add1ToCart(Player player, Shop shop, Product product, Map<String, String> vars) {
        Profile profile = DailyShop.PROFILE_FACTORY.getProfile(player);
        ShopOrder cartOrder = profile.getCart().getOrder(shop.getId());
        // 构建一个新订单并等待被检查与合并
        // 避免反复检测购物车中的商品
        ShopOrder newOrder = new ShopOrderImpl(player)
                .setOrderType(cartOrder.getOrderType())
                .setStack(product, 1);
        // 一个订单在被判断是否能成功前必须被计算订单价值
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
        PlayerUtils.sendMessage(MessageConfig.getShopOverrideableString(shop.getId(), "messages.action.shop.add-1-to-cart.success"), player, vars);
    }

    private static void remove1FromCart(Player player, Shop shop, Product product, Map<String, String> vars) {
        Profile profile = DailyShop.PROFILE_FACTORY.getProfile(player);
        ShopOrder cartOrder = profile.getCart().getOrder(shop.getId());
        if (cartOrder.getOrderedProducts().containsKey(product.getId())) {
            cartOrder.setStack(product, cartOrder.getOrderedProducts().get(product.getId()) - 1);
        }
        PlayerUtils.sendMessage(MessageConfig.getShopOverrideableString(shop.getId(), "messages.action.shop.remove-1-from-cart.success"), player, vars);
        PlayerUtils.playSound(ShopConfig.getSoundRecord(shop.getId(), "remove-1-from-cart.success"), player);
    }

    private static void removeAllFromCart(Player player, Shop shop, Product product, Map<String, String> vars) {
        Profile profile = DailyShop.PROFILE_FACTORY.getProfile(player);
        ShopOrder cartOrder = profile.getCart().getOrder(shop.getId());
        vars.put("stack", String.valueOf(cartOrder.getOrderedProducts().get(product.getId())));
        if (cartOrder.getOrderedProducts().containsKey(product.getId())) {
            cartOrder.setStack(product, 0);
        }
        PlayerUtils.sendMessage(MessageConfig.getShopOverrideableString(shop.getId(), "messages.action.shop.remove-all-from-cart.success"), player, vars);
        PlayerUtils.playSound(ShopConfig.getSoundRecord(shop.getId(), "remove-all-from-cart.success"), player);
    }
}
