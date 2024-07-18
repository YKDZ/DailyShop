package cn.encmys.ykdz.forest.dailyshop.builder;

import cn.encmys.ykdz.forest.dailyshop.api.DailyShop;
import cn.encmys.ykdz.forest.dailyshop.api.config.Config;
import cn.encmys.ykdz.forest.dailyshop.api.config.MessageConfig;
import cn.encmys.ykdz.forest.dailyshop.api.config.ShopConfig;
import cn.encmys.ykdz.forest.dailyshop.api.gui.icon.Icon;
import cn.encmys.ykdz.forest.dailyshop.api.item.decorator.BaseItemDecorator;
import cn.encmys.ykdz.forest.dailyshop.api.product.Product;
import cn.encmys.ykdz.forest.dailyshop.api.product.factory.ProductFactory;
import cn.encmys.ykdz.forest.dailyshop.api.shop.Shop;
import cn.encmys.ykdz.forest.dailyshop.api.shop.cashier.ShopCashier;
import cn.encmys.ykdz.forest.dailyshop.api.shop.order.ShopOrder;
import cn.encmys.ykdz.forest.dailyshop.api.shop.order.enums.SettlementResult;
import cn.encmys.ykdz.forest.dailyshop.api.shop.pricer.ShopPricer;
import cn.encmys.ykdz.forest.dailyshop.api.utils.TextUtils;
import cn.encmys.ykdz.forest.dailyshop.gui.icon.NormalIcon;
import cn.encmys.ykdz.forest.dailyshop.product.BundleProduct;
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
                            bundleContentsLore.add(TextUtils.decorateTextInMiniMessage(decorator.getBundleContentsLineFormat(), null, new HashMap<>() {{
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
                                .setLore(TextUtils.parseVar(decorator.getLoreFormat(), null, listVars, vars))
                                .setDisplayName(TextUtils.parseVar(decorator.getNameFormat(), null, vars))
                                .setBannerPatterns(decorator.getPatternsData())
                                .setFireworkEffects(decorator.getFireworkEffectData())
                                .build(decorator.getAmount()));
            }

            @Override
            public void handleClick(@NotNull ClickType clickType, @NotNull Player player, @NotNull InventoryClickEvent event) {
                Shop shop = DailyShop.SHOP_FACTORY.getShop(shopId);
                ShopPricer shopPricer = shop.getShopPricer();
                ShopCashier shopCashier = shop.getShopCashier();
                Map<String, String> vars = new HashMap<>() {{
                    put("name", decorator.getName());
                    put("amount", String.valueOf(decorator.getAmount()));
                    put("shop", DailyShop.SHOP_FACTORY.getShop(shopId).getName());
                    put("cost", MessageConfig.format_decimal.format(shopPricer.getBuyPrice(product.getId())));
                    put("earn", MessageConfig.format_decimal.format(shopPricer.getSellPrice(product.getId())));
                }};

                // 玩家从商店购买商品
                if (clickType == ClickType.LEFT) {
                    SettlementResult result = shopCashier.settle(DailyShop.SHOP_ORDER_BUILDER.sellToOrder(player)
                            .addProduct(product, 1));
                    if (result != SettlementResult.SUCCESS) {
                        switch (result) {
                            case TRANSITION_DISABLED ->
                                    DailyShop.ADVENTURE_MANAGER.sendMessageWithPrefix(player, TextUtils.decorateTextInMiniMessage(MessageConfig.messages_action_buy_failure_disable, player, vars));
                            case NOT_ENOUGH_MONEY ->
                                    DailyShop.ADVENTURE_MANAGER.sendMessageWithPrefix(player, TextUtils.decorateTextInMiniMessage(MessageConfig.messages_action_buy_failure_money, player, vars));
                            case NOT_ENOUGH_GLOBAL_STOCK ->
                                    DailyShop.ADVENTURE_MANAGER.sendMessageWithPrefix(player, TextUtils.decorateTextInMiniMessage(MessageConfig.messages_action_buy_failure_stock_global, player, vars));
                            case NOT_ENOUGH_PLAYER_STOCK ->
                                    DailyShop.ADVENTURE_MANAGER.sendMessageWithPrefix(player, TextUtils.decorateTextInMiniMessage(MessageConfig.messages_action_buy_failure_stock_player, player, vars));
                            case NOT_ENOUGH_INVENTORY_SPACE ->
                                    DailyShop.ADVENTURE_MANAGER.sendMessageWithPrefix(player, TextUtils.decorateTextInMiniMessage(MessageConfig.messages_action_buy_failure_inventory_space, player, vars));
                        }
                        return;
                    } else {
                        DailyShop.ADVENTURE_MANAGER.sendMessageWithPrefix(player, TextUtils.decorateTextInMiniMessage(MessageConfig.messages_action_buy_success, player, vars));
                        player.playSound(player.getLocation(), ShopConfig.getBuySound(shopId), 1f, 1f);
                    }
                }
                // 玩家向商店出售商品
                else if (clickType == ClickType.RIGHT) {
                    SettlementResult result = shopCashier.settle(DailyShop.SHOP_ORDER_BUILDER.buyFromOrder(player)
                            .addProduct(product, 1));
                    if (result != SettlementResult.SUCCESS) {
                        switch (result) {
                            case TRANSITION_DISABLED ->
                                    DailyShop.ADVENTURE_MANAGER.sendMessageWithPrefix(player, TextUtils.decorateTextInMiniMessage(MessageConfig.messages_action_sell_failure_disable, player, vars));
                            case NOT_ENOUGH_PRODUCT ->
                                    DailyShop.ADVENTURE_MANAGER.sendMessageWithPrefix(player, TextUtils.decorateTextInMiniMessage(MessageConfig.messages_action_sell_failure_notEnough, player, vars));
                        }
                        return;
                    }
                    DailyShop.ADVENTURE_MANAGER.sendMessageWithPrefix(player, TextUtils.decorateTextInMiniMessage(MessageConfig.messages_action_sell_success, player, vars));
                    player.playSound(player.getLocation(), ShopConfig.getSellSound(shopId), 1f, 1f);
                }
                // 玩家向商店出售背包内全部商品
                else if (clickType == ClickType.SHIFT_RIGHT) {
                    ShopOrder order = DailyShop.SHOP_ORDER_BUILDER.buyAllFromOrder(player)
                            .addProduct(product, 1);
                    SettlementResult result = shopCashier.settle(order);
                    if (result != SettlementResult.SUCCESS) {
                        switch (result) {
                            case NOT_ENOUGH_PRODUCT ->
                                    DailyShop.ADVENTURE_MANAGER.sendMessageWithPrefix(player, TextUtils.decorateTextInMiniMessage(MessageConfig.messages_action_sellAll_failure_notEnough, player, vars));
                            case TRANSITION_DISABLED ->
                                    DailyShop.ADVENTURE_MANAGER.sendMessageWithPrefix(player, TextUtils.decorateTextInMiniMessage(MessageConfig.messages_action_sellAll_failure_disable, player, vars));
                        }
                        return;
                    }
                    int stack = order.getTotalStack();
                    vars.put("earn", MessageConfig.format_decimal.format(shopPricer.getSellPrice(product.getId()) * stack));
                    vars.put("stack", String.valueOf(stack));
                    DailyShop.ADVENTURE_MANAGER.sendMessageWithPrefix(player, TextUtils.decorateTextInMiniMessage(MessageConfig.messages_action_sellAll_success, player, vars));
                    player.playSound(player.getLocation(), ShopConfig.getSellSound(shopId), 1f, 1f);
                }

                notifyWindows();
            }
        };
        // 根据需求设置是否自动刷新
        if (product.getProductStock().isPlayerStock() || product.getProductStock().isGlobalStock()) {
            ((Icon) icon).startUpdater(Config.period_updateProductIcon);
        }
        return icon;
    }
}
