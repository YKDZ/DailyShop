package cn.encmys.ykdz.forest.dailyshop.builder;

import cn.encmys.ykdz.forest.dailyshop.api.DailyShop;
import cn.encmys.ykdz.forest.dailyshop.api.config.Config;
import cn.encmys.ykdz.forest.dailyshop.api.config.MessageConfig;
import cn.encmys.ykdz.forest.dailyshop.api.config.ShopConfig;
import cn.encmys.ykdz.forest.dailyshop.api.gui.icon.Icon;
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
import cn.encmys.ykdz.forest.dailyshop.api.utils.TextUtils;
import cn.encmys.ykdz.forest.dailyshop.gui.icon.NormalIcon;
import cn.encmys.ykdz.forest.dailyshop.product.BundleProduct;
import cn.encmys.ykdz.forest.dailyshop.shop.order.ShopOrderImpl;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import xyz.xenondevs.invui.item.Item;
import xyz.xenondevs.invui.item.ItemProvider;
import xyz.xenondevs.invui.item.builder.ItemBuilder;

import java.util.*;

public class ProductIconBuilder {
    public static Item build(@NotNull BaseItemDecorator decorator, @Nullable Player player, @NotNull String shopId, @NotNull Product product) {
        ProductFactory productFactory = DailyShop.PRODUCT_FACTORY;
        Item icon = new NormalIcon() {
            @Override
            public ItemProvider getItemProvider() {
                Shop shop = DailyShop.SHOP_FACTORY.getShop(shopId);
                decorator.setNameFormat(ShopConfig.getProductNameFormat(shopId));
                decorator.setLoreFormat(ShopConfig.getProductLoreFormat(shopId));
                decorator.setBundleContentsLineFormat(ShopConfig.getBundleContentsLineFormat(shopId));

                // 处理捆绑包商品的列表 lore
                List<String> bundleContentsLore = new ArrayList<>();
                if (product instanceof BundleProduct) {
                    Set<String> bundleContents = ((BundleProduct) product).getBundleContents().keySet();
                    if (!bundleContents.isEmpty()) {
                        for (String contentId : bundleContents) {
                            Product content = productFactory.getProduct(contentId);
                            if (content == null) {
                                continue;
                            }
                            bundleContentsLore.add(TextUtils.decorateTextKeepMiniMessage(decorator.getBundleContentsLineFormat(), null, new HashMap<>() {{
                                put("name", content.getIconDecorator().getName());
                                put("amount", String.valueOf(content.getItemDecorator().getAmount()));
                            }}));
                        }
                    }
                }

                ShopPricer shopPricer = shop.getShopPricer();
                // 关于产品自身的的变量
                Map<String, String> vars = new HashMap<>() {{
                    put("name", decorator.getName());
                    put("amount", String.valueOf(decorator.getAmount()));
                    put("buy-price", shopPricer.getBuyPrice(product.getId()) != -1d ? MessageConfig.format_decimal.format(shopPricer.getBuyPrice(product.getId())) : ShopConfig.getDisabledPrice(shopId));
                    put("sell-price", shopPricer.getSellPrice(product.getId()) != -1d ? MessageConfig.format_decimal.format(shopPricer.getSellPrice(product.getId())) : ShopConfig.getDisabledPrice(shopId));
                    put("current-global-stock", String.valueOf(product.getProductStock().getCurrentGlobalAmount()));
                    put("initial-global-stock", String.valueOf(product.getProductStock().getInitialGlobalAmount()));
                    put("current-player-stock", String.valueOf(player == null ? -1 : product.getProductStock().getCurrentPlayerAmount(player.getUniqueId())));
                    put("initial-player-stock", String.valueOf(product.getProductStock().getInitialPlayerAmount()));
                    put("rarity", product.getRarity().getName());
                }};
                // 列表行的变量
                Map<String, List<String>> listVars = new HashMap<>() {{
                    put("desc-lore", decorator.getLore());
                    put("bundle-contents", bundleContentsLore);
                }};

                return new ItemBuilder(
                        new cn.encmys.ykdz.forest.dailyshop.api.utils.ItemBuilder(decorator.getItem().build(null))
                                .setCustomModelData(decorator.getCustomModelData())
                                .setItemFlags(decorator.getItemFlags())
                                .setLore(TextUtils.decorateText(decorator.getLoreFormat(), null, vars, listVars))
                                .setDisplayName(TextUtils.decorateText(decorator.getNameFormat(), null, vars))
                                .setBannerPatterns(decorator.getPatternsData())
                                .setFireworkEffects(decorator.getFireworkEffectData())
                                .build(decorator.getAmount()));
            }

            @Override
            public void handleClick(@NotNull ClickType clickType, @NotNull Player player, @NotNull InventoryClickEvent event) {
                Profile profile = DailyShop.PROFILE_FACTORY.getProfile(player);
                Shop shop = DailyShop.SHOP_FACTORY.getShop(shopId);
                ShopPricer shopPricer = shop.getShopPricer();
                Map<String, String> vars = new HashMap<>() {{
                    put("name", decorator.getName());
                    put("amount", String.valueOf(decorator.getAmount()));
                    put("shop", DailyShop.SHOP_FACTORY.getShop(shopId).getName());
                    put("cost", MessageConfig.format_decimal.format(shopPricer.getBuyPrice(product.getId())));
                    put("earn", MessageConfig.format_decimal.format(shopPricer.getSellPrice(product.getId())));
                }};

                // 玩家从商店购买商品
                if (clickType == ClickType.LEFT) {
                    switch (profile.getShoppingMode(shopId)) {
                        case DIRECT -> sellToDirectly(shop, player, product, vars);
                        case CART -> addToCart(player, shop, product, vars);
                    }
                }
                // 玩家向商店出售商品（仅直接模式）
                else if (clickType == ClickType.RIGHT) {
                    buyFromDirectly(shop, player, product, vars);
                }
                // 玩家向商店出售背包内全部商品（仅直接模式）
                else if (clickType == ClickType.SHIFT_RIGHT) {
                    buyAllFromDirectly(shop, player, product, vars);
                }

                // 根据需求刷新菜单界面
                if (profile.getShoppingMode(shopId) == ShoppingMode.DIRECT &&
                        (product.getProductStock().isPlayerStock()
                                || product.getProductStock().isGlobalStock())) {
                    notifyWindows();
                }
            }
        };
        // 根据需求设置是否自动刷新
        if (product.getProductStock().isPlayerStock()
                || product.getProductStock().isGlobalStock()) {
            ((Icon) icon).startUpdater(Config.period_updateProductIcon);
        }
        return icon;
    }

    private static void sellToDirectly(Shop shop, Player player, Product product, Map<String, String> vars) {
        ShopCashier shopCashier = shop.getShopCashier();
        SettlementResult result = shopCashier.settle(
                new ShopOrderImpl(player)
                        .setOrderType(OrderType.SELL_TO)
                        .addProduct(product, 1)
        );
        if (result != SettlementResult.SUCCESS) {
            switch (result) {
                case TRANSITION_DISABLED ->
                        sendMessage(MessageConfig.getActionMessage(shop.getId(), "buy.failure.disable"), player, vars);
                case NOT_ENOUGH_MONEY ->
                        sendMessage(MessageConfig.getActionMessage(shop.getId(), "buy.failure.money"), player, vars);
                case NOT_ENOUGH_GLOBAL_STOCK ->
                        sendMessage(MessageConfig.getActionMessage(shop.getId(), "buy.failure.stock.global"), player, vars);
                case NOT_ENOUGH_PLAYER_STOCK ->
                        sendMessage(MessageConfig.getActionMessage(shop.getId(), "buy.failure.stock.player"), player, vars);
                case NOT_ENOUGH_INVENTORY_SPACE ->
                        sendMessage(MessageConfig.getActionMessage(shop.getId(), "buy.failure.inventory-space"), player, vars);
            }
        } else {
            sendMessage(MessageConfig.getActionMessage(shop.getId(), "buy.success"), player, vars);
            player.playSound(player.getLocation(), ShopConfig.getBuySound(shop.getId()), 1f, 1f);
        }
    }

    private static void buyFromDirectly(Shop shop, Player player, Product product, Map<String, String> vars) {
        ShopCashier shopCashier = shop.getShopCashier();
        SettlementResult result = shopCashier.settle(
                new ShopOrderImpl(player)
                        .setOrderType(OrderType.BUY_FROM)
                        .addProduct(product, 1)
        );
        if (result != SettlementResult.SUCCESS) {
            switch (result) {
                case TRANSITION_DISABLED ->
                        sendMessage(MessageConfig.getActionMessage(shop.getId(), "sell.failure.disable"), player, vars);
                case NOT_ENOUGH_PRODUCT ->
                        sendMessage(MessageConfig.getActionMessage(shop.getId(), "sell.failure.not-enough"), player, vars);
                case NOT_ENOUGH_MERCHANT_BALANCE ->
                        sendMessage(MessageConfig.getActionMessage(shop.getId(), "sell.failure.merchant-balance"), player, vars);
            }
            return;
        }
        sendMessage(MessageConfig.getActionMessage(shop.getId(), "sell.success"), player, vars);
        player.playSound(player.getLocation(), ShopConfig.getSellSound(shop.getId()), 1f, 1f);
    }

    private static void buyAllFromDirectly(Shop shop, Player player, Product product, Map<String, String> vars) {
        ShopCashier shopCashier = shop.getShopCashier();
        ShopPricer shopPricer = shop.getShopPricer();
        ShopOrder order =
                new ShopOrderImpl(player)
                        .setOrderType(OrderType.BUY_ALL_FROM)
                        .addProduct(product, 1);
        SettlementResult result = shopCashier.settle(order);
        if (result != SettlementResult.SUCCESS) {
            switch (result) {
                case NOT_ENOUGH_PRODUCT ->
                        sendMessage(MessageConfig.getActionMessage(shop.getId(), "sell-all.failure.not-enough"), player, vars);
                case TRANSITION_DISABLED ->
                        sendMessage(MessageConfig.getActionMessage(shop.getId(), "sell-all.failure.disable"), player, vars);
                case NOT_ENOUGH_MERCHANT_BALANCE ->
                        sendMessage(MessageConfig.getActionMessage(shop.getId(), "sell-all.failure.merchant-balance"), player, vars);
            }
            return;
        }
        // TODO 为收购全部操作增加单独的提示逻辑
//        vars.put("earn", MessageConfig.format_decimal.format(shopPricer.getSellPrice(product.getId()) * stack));
//        vars.put("stack", String.valueOf(stack));
        sendMessage(MessageConfig.getActionMessage(shop.getId(), "sell-all.success"), player, vars);
        player.playSound(player.getLocation(), ShopConfig.getSellSound(shop.getId()), 1f, 1f);
    }

    private static void addToCart(Player player, Shop shop, Product product, Map<String, String> vars) {
        Profile profile = DailyShop.PROFILE_FACTORY.getProfile(player);
        ShopOrder cart = profile.getCart(shop.getId());
        // 构建一个新订单并等待被检查与合并
        // 避免反复检测购物车中的商品
        ShopOrder newOrder = new ShopOrderImpl(player)
                .setOrderType(cart.getOrderType())
                .addProduct(product, 1);
        shop.getShopCashier().billOrder(newOrder);
        // 在一个限制或情况“无法被玩家解决”的情况下
        // 阻止玩家将商品加入购物车
        switch (newOrder.getOrderType() == OrderType.SELL_TO ? shop.getShopCashier().canSellTo(newOrder) : shop.getShopCashier().canBuyFrom(newOrder)) {
            case TRANSITION_DISABLED -> {
                switch (newOrder.getOrderType()) {
                    // TODO 为无法加入购物车单独指定提示信息
                    case BUY_ALL_FROM ->
                            sendMessage(MessageConfig.getActionMessage(shop.getId(), "sell-all.failure.disable"), player, vars);
                    case BUY_FROM ->
                            sendMessage(MessageConfig.getActionMessage(shop.getId(), "sell.failure.disable"), player, vars);
                    case SELL_TO ->
                            sendMessage(MessageConfig.getActionMessage(shop.getId(), "buy.failure.disable"), player, vars);
                }
            }
        }
        cart.combineOrder(newOrder);
        profile.setCartOrder(shop.getId(), cart);
        // TODO 加入购物车音效
        player.playSound(player.getLocation(), ShopConfig.getSellSound(shop.getId()), 1f, 1f);
    }

    private static void removeFromCart(Player player, Shop shop, Product product, Map<String, String> vars) {

    }

    private static void sendMessage(String message, Player player, Map<String, String> vars) {
        DailyShop.ADVENTURE_MANAGER.sendMessageWithPrefix(player, TextUtils.decorateTextKeepMiniMessage(message, player, vars));
    }
}
