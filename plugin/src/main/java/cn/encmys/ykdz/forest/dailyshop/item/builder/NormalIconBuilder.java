package cn.encmys.ykdz.forest.dailyshop.item.builder;

import cn.encmys.ykdz.forest.dailyshop.api.DailyShop;
import cn.encmys.ykdz.forest.dailyshop.api.config.CartGUIConfig;
import cn.encmys.ykdz.forest.dailyshop.api.config.MessageConfig;
import cn.encmys.ykdz.forest.dailyshop.api.config.record.misc.IconRecord;
import cn.encmys.ykdz.forest.dailyshop.api.gui.GUI;
import cn.encmys.ykdz.forest.dailyshop.api.gui.enums.GUIContentType;
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
import cn.encmys.ykdz.forest.dailyshop.api.utils.LogUtils;
import cn.encmys.ykdz.forest.dailyshop.api.utils.PlayerUtils;
import cn.encmys.ykdz.forest.dailyshop.api.utils.TextUtils;
import cn.encmys.ykdz.forest.dailyshop.gui.StackPickerGUI;
import cn.encmys.ykdz.forest.dailyshop.item.decorator.BaseItemDecoratorImpl;
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
    private static BaseItemDecorator decoratorFromIconRecord(@NotNull IconRecord iconRecord, @Nullable Shop shop, @NotNull Player player, @Nullable Map<String, String> additionalVars) {
        Profile profile = DailyShop.PROFILE_FACTORY.getProfile(player);
        Map<String, String> vars = new HashMap<>() {{
            if (shop != null) {
                put("shopping-mode-id", profile.getShoppingMode(shop.getId()).name());
                put("shop-id", shop.getId());
            }
            put("player-uuid", player.getUniqueId().toString());
            put("player-name", player.getName());
            if (additionalVars != null) {
                putAll(additionalVars);
            }
        }};
        // 尝试找到满足条件的第一个子图标
        // 否则使用默认图标
        IconRecord targetIconRecord = iconRecord;
        for (Map.Entry<String, IconRecord> entry : iconRecord.conditionIcons().entrySet()) {
            if (TextUtils.evaluateBooleanFormula(entry.getKey(), vars, player)) {
                targetIconRecord = entry.getValue();
                break;
            }
        }
        return BaseItemDecoratorImpl.get(targetIconRecord, true);
    }

    @NotNull
    public static Item build(@NotNull IconRecord iconRecord, @Nullable Shop shop, @NotNull GUI workGUI, Player player, @Nullable Map<String, String> additionalVars, @Nullable Map<String, List<String>> additionalListVars) {
        BaseItemDecorator decorator = decoratorFromIconRecord(iconRecord, shop, player, null);
        if (decorator.getFeaturesScroll() != null || decorator.getFeaturesPageChange() != null) {
            return buildControlIcon(iconRecord, shop, workGUI, player, additionalVars, additionalListVars);
        }
        return buildIcon(iconRecord, shop, workGUI, player, additionalVars, additionalListVars, decorator);
    }

    private static Item buildControlIcon(@NotNull IconRecord iconRecord, @Nullable Shop shop, @NotNull GUI workGUI, Player player, @Nullable Map<String, String> additionalVars, @Nullable Map<String, List<String>> additionalListVars) {
        BaseItemDecorator decorator = decoratorFromIconRecord(iconRecord, shop, player, null);
        if (workGUI.getGuiContentType() == GUIContentType.SCROLL) {
            return buildScrollControlIcon(iconRecord, shop, workGUI, player, additionalVars, additionalListVars, decorator);
        } else {
            return buildPagedControlIcon(iconRecord, shop, workGUI, player, additionalVars, additionalListVars, decorator);
        }
    }

    private static Item buildIcon(@NotNull IconRecord iconRecord, @Nullable Shop shop, @NotNull GUI workGUI, Player player, @Nullable Map<String, String> additionalVars, @Nullable Map<String, List<String>> additionalListVars, BaseItemDecorator baseDecorator) {
        AbstractIcon icon = new AbstractIcon(baseDecorator) {
            @Override
            public ItemProvider getItemProvider() {
                decorator = decoratorFromIconRecord(iconRecord, shop, player, additionalVars);
                return itemFromDecorator(decorator, shop, player, additionalVars, additionalListVars);
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
                    if (additionalVars != null) {
                        putAll(additionalVars);
                    }
                }};
                BaseItemDecorator decorator = decoratorFromIconRecord(iconRecord, shop, player, vars);
                dispatchCommand(clickType, player, decorator.getCommands(), vars);
                handleNormalFeatures(clickType, decorator, player, shop);
                notifyWindows();
            }
        };

        // 配置自动更新
        if (icon.getDecorator().getUpdatePeriod() > 0) {
            icon.startUpdater(icon.getDecorator().getUpdatePeriod());
        }

        return icon;
    }

    private static Item buildScrollControlIcon(@NotNull IconRecord iconRecord, @Nullable Shop shop, @NotNull GUI workGUI, Player player, @Nullable Map<String, String> additionalVars, @Nullable Map<String, List<String>> additionalListVars, BaseItemDecorator baseDecorator) {
        AbstractControlIcon<ScrollGui<Item>> icon = new AbstractControlIcon<>(baseDecorator) {
            @Override
            public ItemProvider getItemProvider(ScrollGui<Item> gui) {
                Map<String, String> vars = new HashMap<>() {{
                    // 当前 scroll 从 0 开始
                    put("current-line", String.valueOf(gui.getCurrentLine() + 1));
                    // 总数从 0 开始
                    put("total-line", String.valueOf(gui.getMaxLine() + 1));
                    if (additionalVars != null) {
                        putAll(additionalVars);
                    }
                }};
                decorator = decoratorFromIconRecord(iconRecord, shop, player, vars);
                return itemFromDecorator(decorator, shop, player, vars, additionalListVars);
            }

            @Override
            public void handleClick(@NotNull ClickType clickType, @NotNull Player player, @NotNull InventoryClickEvent event) {
                // 通用变量
                Map<String, String> vars = new HashMap<>() {{
                    put("player-name", player.getName());
                    put("player-uuid", player.getUniqueId().toString());
                    put("click-type", clickType.name());
                    // 当前 scroll 从 0 开始
                    put("current-line", String.valueOf(getGui().getCurrentLine() + 1));
                    // 总数从 0 开始
                    put("total-line", String.valueOf(getGui().getMaxLine() + 1));
                    if (shop != null) {
                        put("shop-id", shop.getId());
                        put("shop-name", shop.getName());
                    }
                    if (additionalVars != null) {
                        putAll(additionalVars);
                    }
                }};
                dispatchCommand(clickType, player, decorator.getCommands(), vars);
                handleNormalFeatures(clickType, decorator, player, shop);
                handleControlFeatures(clickType, decorator, player, shop, getGui());
            }
        };

        // 自动更新
        if (icon.getDecorator().getUpdatePeriod() > 0) {
            icon.startUpdater(icon.getDecorator().getUpdatePeriod());
        }

        return icon;
    }

    private static Item buildPagedControlIcon(@NotNull IconRecord iconRecord, @Nullable Shop shop, @NotNull GUI workGUI, Player player, @Nullable Map<String, String> additionalVars, @Nullable Map<String, List<String>> additionalListVars, BaseItemDecorator baseDecorator) {
        AbstractControlIcon<PagedGui<Item>> icon = new AbstractControlIcon<>(baseDecorator) {
            @Override
            public ItemProvider getItemProvider(PagedGui<Item> gui) {
                Map<String, String> vars = new HashMap<>() {{
                    // 当前 page 从 0 开始
                    put("current-page", String.valueOf(gui.getCurrentPage() + 1));
                    // 总数从 1 开始
                    // 若不存在 content 则为 0
                    put("total-page", String.valueOf(gui.getPageAmount() == 0 ? 1 : gui.getPageAmount()));
                    if (additionalVars != null) {
                        putAll(additionalVars);
                    }
                }};
                decorator = decoratorFromIconRecord(iconRecord, shop, player, vars);
                return itemFromDecorator(decorator, shop, player, vars, additionalListVars);
            }

            @Override
            public void handleClick(@NotNull ClickType clickType, @NotNull Player player, @NotNull InventoryClickEvent event) {
                // 通用变量
                Map<String, String> vars = new HashMap<>() {{
                    put("player-name", player.getName());
                    put("player-uuid", player.getUniqueId().toString());
                    put("click-type", clickType.name());
                    // 当前 page 从 0 开始
                    put("current-page", String.valueOf(getGui().getCurrentPage() + 1));
                    // 总数从 1 开始
                    // 若不存在 content 则为 0
                    put("total-page", String.valueOf(getGui().getPageAmount() == 0 ? 1 : getGui().getPageAmount()));
                    if (shop != null) {
                        put("shop-id", shop.getId());
                        put("shop-name", shop.getName());
                    }
                    if (additionalVars != null) {
                        putAll(additionalVars);
                    }
                }};
                dispatchCommand(clickType, player, decorator.getCommands(), vars);
                handleNormalFeatures(clickType, decorator, player, shop);
                handleControlFeatures(clickType, decorator, player, shop, getGui());
            }
        };

        // 自动更新
        if (icon.getDecorator().getUpdatePeriod() > 0) {
            icon.startUpdater(icon.getDecorator().getUpdatePeriod());
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
            loadMoreLog(player);
        }
        if (clickType == decorator.getFeaturesOpenShop()) {
            openShop(decorator.getFeaturesOpenShopTarget(), player);
        }
        if (clickType == decorator.getFeaturesOpenOrderHistory()) {
            openOrderHistory(player);
        }
    }

    private static void handleControlFeatures(ClickType clickType, @NotNull BaseItemDecorator decorator, @NotNull Player player, @Nullable Shop shop, @NotNull Gui gui) {
        if (gui instanceof ScrollGui<?> && clickType == decorator.getFeaturesScroll()) {
            featuresScroll(decorator.getFeaturesScrollAmount(), (ScrollGui<?>) gui, player);
        }
        if (gui instanceof PagedGui<?> && clickType == decorator.getFeaturesPageChange()) {
            featuresPageChange(decorator.getFeaturesPageChangeAmount(), (PagedGui<?>) gui, player);
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

    private static ItemBuilder itemFromDecorator(@NotNull BaseItemDecorator decorator, @Nullable Shop shop, Player player, @Nullable Map<String, String> additionalVars, @Nullable Map<String, List<String>> additionalListVars) {
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
                        .setLore(TextUtils.decorateText(decorator.getLore(), player, vars, additionalListVars))
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
        Map<String, SettlementResult> result = profile.getCart().settle();
        // 根据结果集进行文字和音效提示
        // 购物车为空
        if (result.isEmpty()) {
            PlayerUtils.playSound(CartGUIConfig.getSoundRecord("settle-cart.failure"), player);
            PlayerUtils.sendMessage(MessageConfig.getCartSettleMessage(profile.getCart().getMode(), SettlementResult.EMPTY), player, new HashMap<>() {{
                put("mode", MessageConfig.getTerm(profile.getCart().getMode()));
            }});
            return;
        }
        // 因各种原因失败的 ShopOrder
        for (Map.Entry<String, SettlementResult> entry : result.entrySet()) {
            String shopId = entry.getKey();
            Shop shop = DailyShop.SHOP_FACTORY.getShop(shopId);
            if (shop == null) {
                continue;
            }
            SettlementResult settlementResult = entry.getValue();
            if (settlementResult != SettlementResult.SUCCESS) {
                // 仅提示错误
                PlayerUtils.sendMessage(MessageConfig.getCartSettleMessage(profile.getCart().getMode(), settlementResult), player, new HashMap<>() {{
                    put("shop-name", shop.getName());
                    put("shop-id", shop.getId());
                    put("mode", MessageConfig.getTerm(profile.getCart().getMode()));
                    if (profile.getCart().getMode() == OrderType.SELL_TO) {
                        put("cost", MessageConfig.format_decimal.format(totalPrice));
                    } else {
                        put("earn", MessageConfig.format_decimal.format(totalPrice));
                    }
                }});
            }
        }
        // 全部成功
        if (result.values().stream().allMatch(r -> r == SettlementResult.SUCCESS)) {
            PlayerUtils.playSound(CartGUIConfig.getSoundRecord("settle-cart.success"), player);
            PlayerUtils.sendMessage(MessageConfig.getCartSettleMessage(profile.getCart().getMode(), SettlementResult.SUCCESS), player, new HashMap<>() {{
                put("mode", MessageConfig.getTerm(profile.getCart().getMode()));
                if (profile.getCart().getMode() == OrderType.SELL_TO) {
                    put("cost", MessageConfig.format_decimal.format(totalPrice));
                } else {
                    put("earn", MessageConfig.format_decimal.format(totalPrice));
                }
            }});
        }
        // 部分成功
        else if (result.containsValue(SettlementResult.SUCCESS)) {
            PlayerUtils.playSound(CartGUIConfig.getSoundRecord("settle-cart.success"), player);
            PlayerUtils.sendMessage(MessageConfig.getCartSettleMessage(profile.getCart().getMode(), SettlementResult.PARTIAL_SUCCESS), player, new HashMap<>() {{
                put("mode", MessageConfig.getTerm(profile.getCart().getMode()));
                if (profile.getCart().getMode() == OrderType.SELL_TO) {
                    put("cost", MessageConfig.format_decimal.format(totalPrice - profile.getCart().getTotalPrice()));
                } else {
                    put("earn", MessageConfig.format_decimal.format(totalPrice - profile.getCart().getTotalPrice()));
                }
            }});
        }
        // 失败音效
        else {
            PlayerUtils.playSound(CartGUIConfig.getSoundRecord("settle-cart.failure"), player);
        }
        profile.getCartGUI().loadContent(player);
    }

    private static void loadMoreLog(@NotNull Player player) {
        Profile profile = DailyShop.PROFILE_FACTORY.getProfile(player);
        if (profile.getViewingGuiType() == GUIType.ORDER_HISTORY) {
            profile.getOrderHistoryGUI().loadContent(player);
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
        Profile profile = DailyShop.PROFILE_FACTORY.getProfile(player);
        profile.getCart().clean();
        profile.getCartGUI().loadContent(player);
        PlayerUtils.sendMessage(MessageConfig.messages_action_cart_cleanCart_success, player, new HashMap<>() {{
            put("player-name", player.getDisplayName());
        }});
        PlayerUtils.playSound(CartGUIConfig.getSoundRecord("clean-cart.success"), player);
    }

    private static void clearCart(Player player) {
        Profile profile = DailyShop.PROFILE_FACTORY.getProfile(player);
        profile.getCart().clear();
        profile.getCartGUI().loadContent(player);
        PlayerUtils.sendMessage(MessageConfig.messages_action_cart_clearCart_success, player, new HashMap<>() {{
            put("player-name", player.getDisplayName());
        }});
        PlayerUtils.playSound(CartGUIConfig.getSoundRecord("clear-cart.success"), player);
    }

    private static void openShop(@NotNull String shopId, Player player) {
        Shop shop = DailyShop.SHOP_FACTORY.getShop(shopId);
        if (shop == null) {
            LogUtils.warn("Try to open shop " + shopId + " but shop do not exist.");
            return;
        }
        shop.getShopGUI().open(player);
    }

    private static void openOrderHistory(Player player) {
        Profile profile = DailyShop.PROFILE_FACTORY.getProfile(player);
        profile.getOrderHistoryGUI().open();
    }
}
