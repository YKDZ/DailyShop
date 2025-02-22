package cn.encmys.ykdz.forest.hyphashop.item.builder;

import cn.encmys.ykdz.forest.hyphascript.context.Context;
import cn.encmys.ykdz.forest.hyphashop.api.HyphaShop;
import cn.encmys.ykdz.forest.hyphashop.api.item.decorator.BaseItemDecorator;
import cn.encmys.ykdz.forest.hyphashop.api.item.decorator.enums.PropertyType;
import cn.encmys.ykdz.forest.hyphashop.api.profile.Profile;
import cn.encmys.ykdz.forest.hyphashop.api.profile.enums.ShoppingMode;
import cn.encmys.ykdz.forest.hyphashop.api.shop.Shop;
import cn.encmys.ykdz.forest.hyphashop.api.shop.order.enums.OrderType;
import cn.encmys.ykdz.forest.hyphashop.api.shop.order.enums.SettlementResult;
import cn.encmys.ykdz.forest.hyphashop.config.CartGUIConfig;
import cn.encmys.ykdz.forest.hyphashop.config.MessageConfig;
import cn.encmys.ykdz.forest.hyphashop.utils.*;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import xyz.xenondevs.invui.gui.PagedGui;
import xyz.xenondevs.invui.gui.ScrollGui;
import xyz.xenondevs.invui.item.BoundItem;
import xyz.xenondevs.invui.item.Item;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class NormalIconBuilder {
    public static @NotNull Item build(@NotNull BaseItemDecorator staticDecorator, @Nullable Shop shop) {
        if (staticDecorator.getProperty(PropertyType.FEATURE_SCROLL) == null && staticDecorator.getProperty(PropertyType.FEATURE_PAGE_CHANGE) == null) {
            return buildNormalIcon(staticDecorator, shop);
        } else {
            if (staticDecorator.getProperty(PropertyType.FEATURE_SCROLL) != null) {
                return buildScrollIcon(staticDecorator, shop);
            } else {
                return buildPageIcon(staticDecorator, shop);
            }
        }
    }

    private static @NotNull Item buildNormalIcon(@NotNull BaseItemDecorator staticDecorator, @Nullable Shop shop) {
        var builder = Item.builder()
                .setItemProvider((player) -> {
                    BaseItemDecorator decorator = parseDecorator(staticDecorator, shop, VarUtils.extractVars(player, shop));
                    return itemFromDecorator(decorator, shop, player);
                })
                .addClickHandler((item, click) -> {
                    Player player = click.getPlayer();
                    ClickType clickType = click.getClickType();

                    Map<String, Object> vars = new HashMap<>() {{
                        putAll(VarUtils.extractVars(player, shop));
                        put("click_type", clickType.name());
                    }};

                    BaseItemDecorator decorator = parseDecorator(staticDecorator, shop, vars);
                    Map<ClickType, List<String>> commandData = decorator.getProperty(PropertyType.COMMANDS_DATA);
                    if (commandData != null) dispatchCommand(clickType, player, commandData, vars);
                    handleNormalFeatures(clickType, decorator, player, shop, vars);
                });
        if (Boolean.TRUE.equals(staticDecorator.getProperty(PropertyType.UPDATE_ON_CLICK))) builder.updateOnClick();
        Long period = staticDecorator.getProperty(PropertyType.UPDATE_PERIOD);
        if (period != null) builder.updatePeriodically(period);
        return builder.build();
    }

    private static @NotNull Item buildScrollIcon(@NotNull BaseItemDecorator staticDecorator, @Nullable Shop shop) {
        var builder = BoundItem.scrollGui()
                .setItemProvider((player, gui) -> {
                    Map<String, Object> vars = new HashMap<>() {{
                        putAll(VarUtils.extractVars(player, shop));
                        // 当前 scroll 从 0 开始
                        put("current_line", gui.getLine() + 1);
                        // 总数从 0 开始
                        put("total_line", gui.getMaxLine() + 1);
                    }};
                    BaseItemDecorator decorator = parseDecorator(staticDecorator, shop, vars);
                    return itemFromDecorator(decorator, shop, player);
                })
                .addClickHandler((item, gui, click) -> {
                    Player player = click.getPlayer();
                    ClickType clickType = click.getClickType();

                    Map<String, Object> vars = new HashMap<>() {{
                        putAll(VarUtils.extractVars(player, shop));
                        put("click_type", clickType.name());
                        // 当前 scroll 从 0 开始
                        put("current_line", gui.getLine() + 1);
                        // 总数从 0 开始
                        put("total_line", gui.getMaxLine() + 1);
                    }};

                    BaseItemDecorator decorator = parseDecorator(staticDecorator, shop, vars);
                    Map<ClickType, List<String>> commandData = decorator.getProperty(PropertyType.COMMANDS_DATA);
                    if (commandData != null) dispatchCommand(clickType, player, commandData, vars);
                    handleNormalFeatures(clickType, decorator, player, shop, vars);
                    if (clickType == decorator.getProperty(PropertyType.FEATURE_SCROLL)) {
                        Integer amount = decorator.getProperty(PropertyType.FEATURE_SCROLL_AMOUNT);
                        featuresScroll(amount == null ? 0 : amount, gui);
                    }
                });
        if (Boolean.TRUE.equals(staticDecorator.getProperty(PropertyType.UPDATE_ON_CLICK))) builder.updateOnClick();
        Long period = staticDecorator.getProperty(PropertyType.UPDATE_PERIOD);
        if (period != null) builder.updatePeriodically(period);
        return builder.build();
    }

    private static @NotNull Item buildPageIcon(@NotNull BaseItemDecorator staticDecorator, @Nullable Shop shop) {
        var builder = BoundItem.pagedGui()
                .setItemProvider((player, gui) -> {
                    Map<String, Object> vars = new HashMap<>() {{
                        putAll(VarUtils.extractVars(player, shop));
                        // 当前 page 从 0 开始
                        put("current_page", gui.getPage() + 1);
                        // 总数从 1 开始
                        // 若不存在 content 则为 0
                        put("total_page", gui.getPageAmount() == 0 ? 1 : gui.getPageAmount());
                    }};
                    BaseItemDecorator decorator = parseDecorator(staticDecorator, shop, vars);
                    return itemFromDecorator(decorator, shop, player);
                })
                .addClickHandler((item, gui, click) -> {
                    Player player = click.getPlayer();
                    ClickType clickType = click.getClickType();

                    Map<String, Object> vars = new HashMap<>() {{
                        putAll(VarUtils.extractVars(player, shop));
                        put("click_type", clickType.name());
                        // 当前 page 从 0 开始
                        put("current_page", gui.getPage() + 1);
                        // 总数从 1 开始
                        // 若不存在 content 则为 0
                        put("total_page", gui.getPageAmount() == 0 ? 1 : gui.getPageAmount());
                    }};

                    BaseItemDecorator decorator = parseDecorator(staticDecorator, shop, vars);
                    Map<ClickType, List<String>> commandData = decorator.getProperty(PropertyType.COMMANDS_DATA);
                    if (commandData != null) dispatchCommand(clickType, player, commandData, vars);
                    handleNormalFeatures(clickType, decorator, player, shop, vars);
                    if (clickType == decorator.getProperty(PropertyType.FEATURE_PAGE_CHANGE)) {
                        Integer amount = decorator.getProperty(PropertyType.FEATURE_PAGE_CHANGE_AMOUNT);
                        featuresPageChange(amount == null ? 0 : amount, gui);
                    }
                });
        if (Boolean.TRUE.equals(staticDecorator.getProperty(PropertyType.UPDATE_ON_CLICK))) builder.updateOnClick();
        Long period = staticDecorator.getProperty(PropertyType.UPDATE_PERIOD);
        if (period != null) builder.updatePeriodically(period);
        return builder.build();
    }

    private static @NotNull BaseItemDecorator parseDecorator(@NotNull BaseItemDecorator staticDecorator, @Nullable Shop shop, @NotNull Map<String, Object> vars) {
        Map<String, BaseItemDecorator> conditionalIcons = staticDecorator.getProperty(PropertyType.CONDITIONAL_ICONS);
        if (conditionalIcons == null) return staticDecorator;

        Context ctx = ScriptUtils.buildContext(
                (shop != null ? shop.getScriptContext().clone() : Context.GLOBAL_CONTEXT),
                vars
            );
        // 尝试找到满足条件的第一个子图标
        // 否则使用默认图标
        for (Map.Entry<String, BaseItemDecorator> entry : conditionalIcons.entrySet()) {
            if (ScriptUtils.evaluateBoolean(ctx, entry.getKey())) {
                LogUtils.debug("Apply condition icon with condition: " + entry.getKey());
                return entry.getValue();
            }
        }

        return staticDecorator;
    }

    private static @NotNull xyz.xenondevs.invui.item.ItemBuilder itemFromDecorator(@NotNull BaseItemDecorator decorator, @Nullable Shop shop, @NotNull Player player) {
        Map<String, Object> vars = VarUtils.extractVars(player, shop);

        String amount = decorator.getProperty(PropertyType.AMOUNT);
        return new xyz.xenondevs.invui.item.ItemBuilder(
                new ItemBuilder(decorator.getBaseItem().build(player))
                        .setDisplayName(TextUtils.decorateTextToComponent(decorator.getNameOrUseBaseItemName(), player, vars))
                        .setLore(TextUtils.decorateTextToComponent(new ArrayList<>() {{
                            addAll(decorator.getProperty(PropertyType.LORE));
                        }}, player, vars, null))
                        .setCustomModelData(decorator.getProperty(PropertyType.CUSTOM_MODEL_DATA))
                        .setItemFlags(decorator.getProperty(PropertyType.ITEM_FLAGS))
                        .setBannerPatterns(decorator.getProperty(PropertyType.BANNER_PATTERNS))
                        .setFireworkEffects(decorator.getProperty(PropertyType.FIREWORK_EFFECTS))
                        .setEnchantments(decorator.getProperty(PropertyType.ENCHANTMENTS))
                        .setPotionEffects(decorator.getProperty(PropertyType.POTION_EFFECTS))
                        .setArmorTrim(decorator.getProperty(PropertyType.ARMOR_TRIM))
                        .setEnchantable(decorator.getProperty(PropertyType.ENCHANTABLE))
                        .setEnchantGlint(decorator.getProperty(PropertyType.ENCHANT_GLINT))
                        .setGlider(decorator.getProperty(PropertyType.GLIDER))
                        .setFlightDuration(decorator.getProperty(PropertyType.FLIGHT_DURATION))
                        .setPotionCustomColor(decorator.getProperty(PropertyType.POTION_COLOR))
                        .setPotionType(decorator.getProperty(PropertyType.POTION_TYPE))
                        .setPotionCustomName(decorator.getProperty(PropertyType.POTION_CUSTOM_NAME))
                        .build(ScriptUtils.evaluateInt(
                                // 有商店则用商店上下文，否则用全局上下文
                                ScriptUtils.buildContext(
                                    (shop != null ? shop.getScriptContext().clone() : Context.GLOBAL_CONTEXT),
                                    new HashMap<>()),
                                amount == null ? "1" : amount)
                        )
        );
    }

    private static void handleNormalFeatures(@NotNull ClickType clickType, @NotNull BaseItemDecorator decorator, @NotNull Player player, @Nullable Shop shop, @NotNull Map<String, Object> vars) {
        if (clickType == decorator.getProperty(PropertyType.FEATURE_BACK_TO_SHOP) && shop != null) {
            backToShop(shop, player);
        }
        if (clickType == decorator.getProperty(PropertyType.FEATURE_SWITCH_SHOPPING_MODE) && shop != null) {
            switchShoppingMode(shop, player, vars);
        }
        if (clickType == decorator.getProperty(PropertyType.FEATURE_OPEN_CART)) {
            openCart(player, vars);
        }
        if (clickType == decorator.getProperty(PropertyType.FEATURE_SETTLE_CART)) {
            settleCart(player, vars);
        }
        if (clickType == decorator.getProperty(PropertyType.FEATURE_SWITCH_CART_MODE)) {
            switchCartMode(player, vars);
        }
        if (clickType == decorator.getProperty(PropertyType.FEATURE_CLEAN_CART)) {
            cleanCart(player, vars);
        }
        if (clickType == decorator.getProperty(PropertyType.FEATURE_CLEAR_CART)) {
            clearCart(player, vars);
        }
        if (clickType == decorator.getProperty(PropertyType.FEATURE_LOAD_MORE_LOG)) {
            loadMoreLog(player);
        }
        if (clickType == decorator.getProperty(PropertyType.FEATURE_OPEN_SHOP)) {
            String shopId = decorator.getProperty(PropertyType.FEATURE_OPEN_SHOP_TARGET);
            Shop shopToOpen = HyphaShop.SHOP_FACTORY.getShop(shopId);
            if (shopToOpen == null) {
                LogUtils.warn("Try to open shop " + shopId + " but shop do not exist.");
                return;
            }
            openShop(shopToOpen, player, vars);
        }
        if (clickType == decorator.getProperty(PropertyType.FEATURE_OPEN_ORDER_HISTORY)) {
            openOrderHistory(player, vars);
        }
    }

    private static void dispatchCommand(@NotNull ClickType clickType, @NotNull Player player, @NotNull Map<ClickType, List<String>> commands, @NotNull Map<String, Object> vars) {
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

    private static void settleCart(@NotNull Player player, @NotNull Map<String, Object> vars) {
        Profile profile = HyphaShop.PROFILE_FACTORY.getProfile(player);
        // 结算后购物车被清空的情况下无法获取总价
        // 故需要提前缓存
        double totalPrice = profile.getCart().getTotalPrice();
        Map<String, SettlementResult> result = profile.getCart().settle();
        // 根据结果集进行文字和音效提示
        // 购物车为空
        if (result.isEmpty()) {
            PlayerUtils.playSound(CartGUIConfig.getSoundRecord("settle-cart.failure"), player);
            MessageUtils.sendMessage(player, MessageConfig.getCartSettleMessage(profile.getCart().getMode(), SettlementResult.EMPTY), vars);
            return;
        }
        // 因各种原因失败的 ShopOrder
        for (Map.Entry<String, SettlementResult> entry : result.entrySet()) {
            String shopId = entry.getKey();
            Shop shop = HyphaShop.SHOP_FACTORY.getShop(shopId);
            if (shop == null) {
                continue;
            }
            SettlementResult settlementResult = entry.getValue();
            if (settlementResult != SettlementResult.SUCCESS) {
                // 仅提示错误
                MessageUtils.sendMessage(player, MessageConfig.getCartSettleMessage(profile.getCart().getMode(), settlementResult), new HashMap<>() {{
                    putAll(vars);
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
            MessageUtils.sendMessage(player, MessageConfig.getCartSettleMessage(profile.getCart().getMode(), SettlementResult.SUCCESS), new HashMap<>() {{
                putAll(vars);
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
            MessageUtils.sendMessage(player, MessageConfig.getCartSettleMessage(profile.getCart().getMode(), SettlementResult.PARTIAL_SUCCESS), new HashMap<>() {{
                putAll(vars);
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
        Profile profile = HyphaShop.PROFILE_FACTORY.getProfile(player);
        profile.getOrderHistoryGUI().loadContent(player);
    }

    private static void featuresScroll(int featuresScrollAmount, @NotNull ScrollGui<?> gui) {
        gui.scroll(featuresScrollAmount);
    }

    private static void featuresPageChange(int featuresPageChangeAmount, @NotNull PagedGui<?> gui) {
        gui.setPage(gui.getPage() + featuresPageChangeAmount);
    }

    private static void backToShop(@NotNull Shop shop, @NotNull Player player ) {
        shop.getShopGUI().open(player);
    }

    private static void switchShoppingMode(@NotNull Shop shop, @NotNull Player player, @NotNull Map<String, Object> vars) {
        Profile profile = HyphaShop.PROFILE_FACTORY.getProfile(player);
        profile.setShoppingMode(shop.getId(),
                profile.getShoppingMode(shop.getId()) == ShoppingMode.DIRECT ? ShoppingMode.CART : ShoppingMode.DIRECT);
        vars.put("shopping_mode_id", profile.getShoppingMode(shop.getId()).name());
        vars.put("shopping_mode_name", MessageConfig.getTerm(profile.getShoppingMode(shop.getId())));
        MessageUtils.sendMessage(player, MessageConfig.getShopOverrideableString(shop.getId(), "messages.action.shop.switch-shopping-mode.success"), vars);
    }

    private static void openCart(@NotNull Player player, @NotNull Map<String, Object> vars) {
        Profile profile = HyphaShop.PROFILE_FACTORY.getProfile(player);
        profile.getCartGUI().open(player);
        MessageUtils.sendMessage(player, MessageConfig.messages_action_cart_openCart_success, vars);
        PlayerUtils.playSound(CartGUIConfig.getSoundRecord("open-cart.success"), player);
    }

    private static void switchCartMode(@NotNull Player player, @NotNull Map<String, Object> vars) {
        Profile profile = HyphaShop.PROFILE_FACTORY.getProfile(player);
        profile.getCart().setMode(
                switch (profile.getCart().getMode()) {
                    case SELL_TO -> OrderType.BUY_FROM;
                    case BUY_FROM -> OrderType.BUY_ALL_FROM;
                    case BUY_ALL_FROM -> OrderType.SELL_TO;
                }
        );
        vars.put("cart_mode_id", profile.getCart().getMode().name());
        vars.put("cart_mode_name", MessageConfig.getTerm(profile.getCart().getMode()));
        MessageUtils.sendMessage(player, MessageConfig.messages_action_cart_switchCartMode_success, vars);
        PlayerUtils.playSound(CartGUIConfig.getSoundRecord("switch-cart-mode.success"), player);
    }

    private static void cleanCart(@NotNull Player player, @NotNull Map<String, Object> vars) {
        Profile profile = HyphaShop.PROFILE_FACTORY.getProfile(player);
        profile.getCart().clean();
        profile.getCartGUI().loadContent(player);
        vars.put("cart_total_price", profile.getCart().getTotalPrice());
        MessageUtils.sendMessage(player, MessageConfig.messages_action_cart_cleanCart_success, vars);
        PlayerUtils.playSound(CartGUIConfig.getSoundRecord("clean-cart.success"), player);
    }

    private static void clearCart(@NotNull Player player, @NotNull Map<String, Object> vars) {
        Profile profile = HyphaShop.PROFILE_FACTORY.getProfile(player);
        profile.getCart().clear();
        profile.getCartGUI().loadContent(player);
        vars.put("cart_total_price", profile.getCart().getTotalPrice());
        MessageUtils.sendMessage(player, MessageConfig.messages_action_cart_clearCart_success, vars);
        PlayerUtils.playSound(CartGUIConfig.getSoundRecord("clear-cart.success"), player);
    }

    private static void openShop(@NotNull Shop shop, @NotNull Player player, @NotNull Map<String, Object> vars) {
        shop.getShopGUI().open(player);
    }

    private static void openOrderHistory(@NotNull Player player, @NotNull Map<String, Object> vars) {
        Profile profile = HyphaShop.PROFILE_FACTORY.getProfile(player);
        profile.getOrderHistoryGUI().open(player);
    }
}
