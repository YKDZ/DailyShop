package cn.encmys.ykdz.forest.dailyshop.item.builder;

import cn.encmys.ykdz.forest.dailyshop.api.DailyShop;
import cn.encmys.ykdz.forest.dailyshop.api.config.CartGUIConfig;
import cn.encmys.ykdz.forest.dailyshop.api.config.MessageConfig;
import cn.encmys.ykdz.forest.dailyshop.api.gui.icon.AbstractControlIcon;
import cn.encmys.ykdz.forest.dailyshop.api.gui.icon.AbstractIcon;
import cn.encmys.ykdz.forest.dailyshop.api.item.decorator.BaseItemDecorator;
import cn.encmys.ykdz.forest.dailyshop.api.profile.Profile;
import cn.encmys.ykdz.forest.dailyshop.api.profile.enums.GUIType;
import cn.encmys.ykdz.forest.dailyshop.api.profile.enums.ShoppingMode;
import cn.encmys.ykdz.forest.dailyshop.api.shop.Shop;
import cn.encmys.ykdz.forest.dailyshop.api.shop.order.enums.OrderType;
import cn.encmys.ykdz.forest.dailyshop.api.shop.order.enums.SettlementResult;
import cn.encmys.ykdz.forest.dailyshop.api.utils.CommandUtils;
import cn.encmys.ykdz.forest.dailyshop.api.utils.PlayerUtils;
import cn.encmys.ykdz.forest.dailyshop.api.utils.TextUtils;
import cn.encmys.ykdz.forest.dailyshop.gui.OrderHistoryGUI;
import cn.encmys.ykdz.forest.dailyshop.gui.StackPickerGUI;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import xyz.xenondevs.invui.gui.Gui;
import xyz.xenondevs.invui.gui.PagedGui;
import xyz.xenondevs.invui.gui.ScrollGui;
import xyz.xenondevs.invui.item.Item;
import xyz.xenondevs.invui.item.ItemProvider;
import xyz.xenondevs.invui.item.builder.ItemBuilder;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class NormalIconBuilder {
    public static Item build(@NotNull BaseItemDecorator decorator, @Nullable Shop shop, Player player) {
        if (decorator.getFeaturesScroll() != null) {
            return buildControlIcon(decorator, shop, player);
        }
        return buildIcon(decorator, shop, player);
    }

    private static Item buildIcon(@NotNull BaseItemDecorator decorator, @Nullable Shop shop, Player player) {
        AbstractIcon icon = new AbstractIcon() {
            @Override
            public ItemProvider getItemProvider() {
                return itemFromDecorator(decorator, shop, player, null);
            }

            @Override
            public void handleClick(@NotNull ClickType clickType, @NotNull Player player, @NotNull InventoryClickEvent event) {
                // 通用变量
                Map<String, String> vars = new HashMap<>() {{
                    put("player-name", player.getName());
                    put("player-uuid", player.getUniqueId().toString());
                    put("click-type", clickType.name());
                    if (shop != null) {
                        put("shop-id", shop.getId());
                        put("shop-name", shop.getName());
                    }
                }};
                dispatchCommand(clickType, player, decorator.getCommands(), vars);
                handleNormalFeatures(clickType, decorator, player, shop);
                notifyWindows();
            }
        };

        // 配置自动更新
        if (decorator.getPeriod() > 0) {
            icon.startUpdater(decorator.getPeriod());
        }

        return icon;
    }

    // TODO 支持更多种类的 Gui
    private static Item buildControlIcon(@NotNull BaseItemDecorator decorator, @Nullable Shop shop, Player player) {
        AbstractControlIcon<ScrollGui<Item>> icon = new AbstractControlIcon<>() {
            @Override
            public ItemProvider getItemProvider(ScrollGui<Item> gui) {
                return itemFromDecorator(decorator, shop, player, new HashMap<>() {{
                    put("current-scroll", String.valueOf(gui.getCurrentLine()));
                    put("max-scroll", String.valueOf(gui.getMaxLine()));
                }});
            }

            @Override
            public void handleClick(@NotNull ClickType clickType, @NotNull Player player, @NotNull InventoryClickEvent event) {
                // 通用变量
                Map<String, String> vars = new HashMap<>() {{
                    put("player-name", player.getName());
                    put("player-uuid", player.getUniqueId().toString());
                    put("click-type", clickType.name());
                    if (shop != null) {
                        put("shop-id", shop.getId());
                        put("shop-name", shop.getName());
                    }
                }};
                dispatchCommand(clickType, player, decorator.getCommands(), vars);
                handleNormalFeatures(clickType, decorator, player, shop);
                handleControlFeatures(clickType, decorator, player, shop, getGui());
            }
        };

        // 配置自动更新
        if (decorator.getPeriod() > 0) {
            icon.startUpdater(decorator.getPeriod());
        }

        return icon;
    }

    private static void handleNormalFeatures(ClickType clickType, @NotNull BaseItemDecorator decorator, Player player, @Nullable Shop shop) {
        if (clickType == decorator.getFeaturesBackToShop() && shop != null) {
            backToShop(shop, player);
        }
        if (clickType == decorator.getFeaturesSwitchShoppingMode() && shop != null) {
            switchShoppingMode(shop, player);
        }
        if (clickType == decorator.getFeaturesOpenCart()) {
            openCart(player);
        }
        if (clickType == decorator.getFeaturesSettleCart()) {
            settleCart(player);
        }
        if (clickType == decorator.getFeaturesSwitchCartMode()) {
            switchCartMode(player);
        }
        if (clickType == decorator.getFeaturesCleanCart()) {
            cleanCart(player);
        }
        if (clickType == decorator.getFeaturesClearCart()) {
            clearCart(player);
        }
        if (clickType == decorator.getFeaturesLoadMoreLog()) {
            featuresLoadMoreLog(player);
        }
    }

    private static void handleControlFeatures(ClickType clickType, @NotNull BaseItemDecorator decorator, @NotNull Player player, @Nullable Shop shop, @NotNull Gui gui) {
        if (gui instanceof ScrollGui<?> && clickType == decorator.getFeaturesScroll()) {
            featuresScroll(decorator.getFeaturesScrollAmount(), (ScrollGui<?>) gui, player);
        }
        if (gui instanceof PagedGui<?> && clickType == decorator.getFeaturesPageChange()) {
            featuresPageChange(decorator.setFeaturesPageChangeAmount(), (PagedGui<?>) gui, player);
        }
    }

    private static void dispatchCommand(ClickType clickType, Player player, Map<ClickType, List<String>> commands, Map<String, String> vars) {
        switch (clickType) {
            case LEFT ->
                    CommandUtils.dispatchCommands(player, commands.getOrDefault(ClickType.LEFT, new ArrayList<>()), vars);
            case RIGHT ->
                    CommandUtils.dispatchCommands(player, commands.getOrDefault(ClickType.RIGHT, new ArrayList<>()), vars);
            case SHIFT_LEFT ->
                    CommandUtils.dispatchCommands(player, commands.getOrDefault(ClickType.SHIFT_LEFT, new ArrayList<>()), vars);
            case SHIFT_RIGHT ->
                    CommandUtils.dispatchCommands(player, commands.getOrDefault(ClickType.SHIFT_RIGHT, new ArrayList<>()), vars);
            case DROP ->
                    CommandUtils.dispatchCommands(player, commands.getOrDefault(ClickType.DROP, new ArrayList<>()), vars);
            case DOUBLE_CLICK ->
                    CommandUtils.dispatchCommands(player, commands.getOrDefault(ClickType.DOUBLE_CLICK, new ArrayList<>()), vars);
            case MIDDLE ->
                    CommandUtils.dispatchCommands(player, commands.getOrDefault(ClickType.MIDDLE, new ArrayList<>()), vars);
            case CONTROL_DROP ->
                    CommandUtils.dispatchCommands(player, commands.getOrDefault(ClickType.CONTROL_DROP, new ArrayList<>()), vars);
            case SWAP_OFFHAND ->
                    CommandUtils.dispatchCommands(player, commands.getOrDefault(ClickType.SWAP_OFFHAND, new ArrayList<>()), vars);
            case NUMBER_KEY ->
                    CommandUtils.dispatchCommands(player, commands.getOrDefault(ClickType.NUMBER_KEY, new ArrayList<>()), vars);
            case WINDOW_BORDER_LEFT ->
                    CommandUtils.dispatchCommands(player, commands.getOrDefault(ClickType.WINDOW_BORDER_LEFT, new ArrayList<>()), vars);
            case WINDOW_BORDER_RIGHT ->
                    CommandUtils.dispatchCommands(player, commands.getOrDefault(ClickType.WINDOW_BORDER_RIGHT, new ArrayList<>()), vars);
            default -> {
            }
        }
    }

    private static ItemBuilder itemFromDecorator(@NotNull BaseItemDecorator decorator, @Nullable Shop shop, Player player, @Nullable Map<String, String> additionalVars) {
        Profile profile = DailyShop.PROFILE_FACTORY.getProfile(player);
        Map<String, String> vars = new HashMap<>() {{
            put("player-name", player.getName());
            put("player-uuid", player.getUniqueId().toString());
            if (shop != null) {
                put("shop-id", shop.getId());
                put("shop-name", shop.getName());
            }
            put("cart-total-price", profile.getCart().getMode() == OrderType.SELL_TO ?
                    MessageConfig.format_decimal.format(profile.getCart().getTotalPrice()) :
                    MessageConfig.placeholderAPI_cartTotalPrice_notSellToMode
            );
            if (profile.getCurrentStackPickerGUI() != null) {
                put("stack", String.valueOf(((StackPickerGUI) profile.getCurrentStackPickerGUI()).getStack()));
            }
            if (additionalVars != null) {
                putAll(additionalVars);
            }
        }};
        return new ItemBuilder(
                new cn.encmys.ykdz.forest.dailyshop.api.utils.ItemBuilder(decorator.getBaseItem().build(player))
                        .setCustomModelData(decorator.getCustomModelData())
                        .setItemFlags(decorator.getItemFlags())
                        .setLore(TextUtils.decorateText(decorator.getLore(), player, vars, null))
                        .setDisplayName(TextUtils.decorateText(decorator.getName(), player, vars))
                        .setBannerPatterns(decorator.getPatternsData())
                        .setFireworkEffects(decorator.getFireworkEffectData())
                        .build(decorator.getAmount()));
    }

    private static void settleCart(Player player) {
        Profile profile = DailyShop.PROFILE_FACTORY.getProfile(player);
        // 结算后购物车被清空的情况下无法获取总价
        // 故需要提前缓存
        double totalPrice = profile.getCart().getTotalPrice();
        SettlementResult result = profile.getCart().settle();
        if (result != SettlementResult.SUCCESS) {
            PlayerUtils.playSound(CartGUIConfig.getSoundRecord("settle-cart.failure"), player);
            PlayerUtils.sendMessage(MessageConfig.getCartSettleMessage(profile.getCart().getMode(), result), player, new HashMap<>() {{
                put("mode", MessageConfig.getTerm(profile.getCart().getMode()));
                if (profile.getCart().getMode() == OrderType.SELL_TO) {
                    put("cost", MessageConfig.format_decimal.format(totalPrice));
                } else {
                    put("earn", MessageConfig.format_decimal.format(totalPrice));
                }
            }});
        } else {
            PlayerUtils.playSound(CartGUIConfig.getSoundRecord("settle-cart.success"), player);
            profile.getCartGUI().close();
            PlayerUtils.sendMessage(MessageConfig.getCartSettleMessage(profile.getCart().getMode(), result), player, new HashMap<>() {{
                put("mode", MessageConfig.getTerm(profile.getCart().getMode()));
                if (profile.getCart().getMode() == OrderType.SELL_TO) {
                    put("cost", MessageConfig.format_decimal.format(totalPrice));
                } else {
                    put("earn", MessageConfig.format_decimal.format(totalPrice));
                }
            }});
        }
    }

    private static void featuresLoadMoreLog(@NotNull Player player) {
        Profile profile = DailyShop.PROFILE_FACTORY.getProfile(player);
        if (profile.getViewingGuiType() == GUIType.ORDER_HISTORY) {
            OrderHistoryGUI orderHistoryGUI = (OrderHistoryGUI) profile.getOrderHistoryGUI();
            orderHistoryGUI.loadMore();
        }
    }

    private static void featuresScroll(int featuresScrollAmount, ScrollGui<?> gui, @NotNull Player player) {
        gui.scroll(featuresScrollAmount);
    }

    private static void featuresPageChange(int featuresPageChangeAmount, PagedGui<?> gui, @NotNull Player player) {
        gui.setPage(gui.getCurrentPage() + featuresPageChangeAmount);
    }

    private static void backToShop(@NotNull Shop shop, Player player) {
        shop.getShopGUI().open(player);
    }

    private static void switchShoppingMode(@NotNull Shop shop, Player player) {
        Profile profile = DailyShop.PROFILE_FACTORY.getProfile(player);
        profile.setShoppingMode(shop.getId(),
                profile.getShoppingMode(shop.getId()) == ShoppingMode.DIRECT ? ShoppingMode.CART : ShoppingMode.DIRECT);
        PlayerUtils.sendMessage(MessageConfig.getShopOverrideableString(shop.getId(), "messages.action.shop.switch-shopping-mode.success"), player, new HashMap<>() {{
            put("shop-name", shop.getName());
            put("player-name", player.getDisplayName());
            put("mode", MessageConfig.getTerm(profile.getShoppingMode(shop.getId())));
        }});
    }

    private static void openCart(Player player) {
        Profile profile = DailyShop.PROFILE_FACTORY.getProfile(player);
        profile.getCartGUI().open();
        PlayerUtils.sendMessage(MessageConfig.messages_action_cart_openCart_success, player, new HashMap<>() {{
            put("player-name", player.getDisplayName());
        }});
        PlayerUtils.playSound(CartGUIConfig.getSoundRecord("open-cart.success"), player);
    }

    private static void switchCartMode(Player player) {
        Profile profile = DailyShop.PROFILE_FACTORY.getProfile(player);
        profile.getCart().setMode(
                switch (profile.getCart().getMode()) {
                    case SELL_TO -> OrderType.BUY_FROM;
                    case BUY_FROM -> OrderType.BUY_ALL_FROM;
                    case BUY_ALL_FROM -> OrderType.SELL_TO;
                }
        );
        PlayerUtils.sendMessage(MessageConfig.messages_action_cart_switchCartMode_success, player, new HashMap<>() {{
            put("player-name", player.getDisplayName());
            put("mode", MessageConfig.getTerm(profile.getCart().getMode()));
        }});
        PlayerUtils.playSound(CartGUIConfig.getSoundRecord("switch-cart-mode.success"), player);
    }

    private static void cleanCart(Player player) {
        DailyShop.PROFILE_FACTORY.getProfile(player).getCart().clean();
        PlayerUtils.sendMessage(MessageConfig.messages_action_cart_cleanCart_success, player, new HashMap<>() {{
            put("player-name", player.getDisplayName());
        }});
        PlayerUtils.playSound(CartGUIConfig.getSoundRecord("clean-cart.success"), player);
    }

    private static void clearCart(Player player) {
        DailyShop.PROFILE_FACTORY.getProfile(player).getCart().clear();
        PlayerUtils.sendMessage(MessageConfig.messages_action_cart_clearCart_success, player, new HashMap<>() {{
            put("player-name", player.getDisplayName());
        }});
        PlayerUtils.playSound(CartGUIConfig.getSoundRecord("clear-cart.success"), player);
    }
}
