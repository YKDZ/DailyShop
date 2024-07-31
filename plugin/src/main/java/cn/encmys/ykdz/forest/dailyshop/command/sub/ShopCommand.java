package cn.encmys.ykdz.forest.dailyshop.command.sub;

import cn.encmys.ykdz.forest.dailyshop.api.DailyShop;
import cn.encmys.ykdz.forest.dailyshop.api.config.MessageConfig;
import cn.encmys.ykdz.forest.dailyshop.api.config.ShopConfig;
import cn.encmys.ykdz.forest.dailyshop.api.profile.Profile;
import cn.encmys.ykdz.forest.dailyshop.api.profile.enums.ShoppingMode;
import cn.encmys.ykdz.forest.dailyshop.api.shop.Shop;
import cn.encmys.ykdz.forest.dailyshop.api.utils.TextUtils;
import dev.jorel.commandapi.CommandAPICommand;
import dev.jorel.commandapi.arguments.ArgumentSuggestions;
import dev.jorel.commandapi.arguments.PlayerArgument;
import dev.jorel.commandapi.arguments.StringArgument;
import org.bukkit.entity.Player;

import java.util.HashMap;

public class ShopCommand {
    public static ShopCommand INSTANCE = new ShopCommand();

    public CommandAPICommand getShopCommand() {
        return new CommandAPICommand("shop")
                .withSubcommands(
                        getShopOpenCommand(),
                        getShopHistoryCommand(),
                        getShopRestockCommand(),
                        getShopCacheCommand(),
                        getShopCartCommand(),
                        getShopMiscCommand()
                );
    }

    private CommandAPICommand getShopOpenCommand() {
        return new CommandAPICommand("open")
                .withPermission("dailyshop.command.shop.open")
                .withArguments(
                        new StringArgument("shop")
                                .replaceSuggestions(ArgumentSuggestions.strings(ShopConfig.getAllId())),
                        new PlayerArgument("player")
                )
                .executes((sender, args) -> {
                    String shopId = (String) args.get("shop");
                    Shop shop = DailyShop.SHOP_FACTORY.getShop(shopId);
                    Player player = (Player) args.get("player");
                    if (player == null) {
                        DailyShop.ADVENTURE_MANAGER.sendMessageWithPrefix(sender, TextUtils.parseInternalVar(MessageConfig.messages_command_shop_open_failure_invalidPlayer, new HashMap<>() {{
                            put("shop", shopId);
                        }}));
                        return;
                    }
                    if (!sender.hasPermission("dailyshop.shop.open." + shopId)) {
                        DailyShop.ADVENTURE_MANAGER.sendMessageWithPrefix(sender, MessageConfig.messages_noPermission);
                        return;
                    }
                    if (shop == null) {
                        DailyShop.ADVENTURE_MANAGER.sendMessageWithPrefix(sender, TextUtils.parseInternalVar(MessageConfig.messages_command_shop_open_failure_invalidShop, new HashMap<>() {{
                            put("shop", (String) args.get("shop"));
                        }}));
                        return;
                    }
                    shop.getShopGUI().open(player);
                });
    }

    private CommandAPICommand getShopHistoryCommand() {
        return new CommandAPICommand("history")
                .withPermission("dailyshop.command.shop.history")
                .withArguments(
                        new StringArgument("shop")
                                .replaceSuggestions(ArgumentSuggestions.strings(ShopConfig.getAllId())),
                        new PlayerArgument("player")
                )
                .executes((sender, args) -> {
                    String shopId = (String) args.get("shop");
                    Shop shop = DailyShop.SHOP_FACTORY.getShop(shopId);
                    Player player = (Player) args.get("player");
                    if (player == null) {
                        DailyShop.ADVENTURE_MANAGER.sendMessageWithPrefix(sender, TextUtils.parseInternalVar(MessageConfig.messages_command_shop_history_failure_invalidPlayer, new HashMap<>() {{
                            put("shop", shopId);
                        }}));
                        return;
                    }
                    if (!player.hasPermission("dailyshop.shop.history." + shopId)) {
                        DailyShop.ADVENTURE_MANAGER.sendMessageWithPrefix(sender, MessageConfig.messages_noPermission);
                        return;
                    }
                    if (shop == null) {
                        DailyShop.ADVENTURE_MANAGER.sendMessageWithPrefix(sender, TextUtils.parseInternalVar(MessageConfig.messages_command_shop_history_failure_invalidShop, new HashMap<>() {{
                            put("shop", (String) args.get("shop"));
                        }}));
                        return;
                    }
                    shop.getHistoryGUI().open(player);
                });
    }

    private CommandAPICommand getShopRestockCommand() {
        return new CommandAPICommand("restock")
                .withPermission("dailyshop.command.shop.restock")
                .withArguments(
                        new StringArgument("shop")
                                .replaceSuggestions(ArgumentSuggestions.strings(ShopConfig.getAllId()))
                )
                .executes((sender, args) -> {
                    String shopId = (String) args.get("shop");
                    Shop shop = DailyShop.SHOP_FACTORY.getShop(shopId);
                    if (shop == null) {
                        DailyShop.ADVENTURE_MANAGER.sendMessageWithPrefix(sender, TextUtils.parseInternalVar(MessageConfig.messages_command_shop_restock_failure_invalidShop, new HashMap<>() {{
                            put("shop", shopId);
                        }}));
                        return;
                    }
                    shop.getShopStocker().stock();
                    DailyShop.ADVENTURE_MANAGER.sendMessageWithPrefix(sender, TextUtils.parseInternalVar(MessageConfig.messages_command_shop_restock_success, new HashMap<>() {{
                        put("shop", shop.getName());
                    }}));
                });
    }

    private CommandAPICommand getShopCacheCommand() {
        return new CommandAPICommand("cache")
                .withSubcommands(
                        new CommandAPICommand("clear")
                                .withPermission("dailyshop.command.shop.cache.clear")
                                .withArguments(
                                        new StringArgument("shop")
                                                .replaceSuggestions(ArgumentSuggestions.strings(ShopConfig.getAllId()))
                                )
                                .executes((sender, args) -> {
                                    String shopId = (String) args.get("shop");
                                    Shop shop = DailyShop.SHOP_FACTORY.getShop(shopId);
                                    if (shop == null) {
                                        DailyShop.ADVENTURE_MANAGER.sendMessageWithPrefix(sender, TextUtils.parseInternalVar(MessageConfig.messages_command_shop_cache_clear_failure_invalidShop, new HashMap<>() {{
                                            put("shop", shopId);
                                        }}));
                                        return;
                                    }
                                    shop.getCachedProductItems().clear();
                                    DailyShop.ADVENTURE_MANAGER.sendMessageWithPrefix(sender, TextUtils.parseInternalVar(MessageConfig.messages_command_shop_cache_clear_success, new HashMap<>() {{
                                        put("shop", shopId);
                                    }}));
                                })
                );
    }

    private CommandAPICommand getShopCartCommand() {
        return new CommandAPICommand("cart")
                .withSubcommands(
                        new CommandAPICommand("open")
                                .withPermission("dailyshop.command.shop.cart.open")
                                .withArguments(
                                        new StringArgument("shop")
                                                .replaceSuggestions(ArgumentSuggestions.strings(ShopConfig.getAllId())),
                                        new PlayerArgument("player")
                                )
                                .executes((sender, args) -> {
                                    String shopId = (String) args.get("shop");
                                    Shop shop = DailyShop.SHOP_FACTORY.getShop(shopId);
                                    Player player = (Player) args.get("player");
                                    if (player == null) {
                                        DailyShop.ADVENTURE_MANAGER.sendMessageWithPrefix(sender, TextUtils.parseInternalVar(MessageConfig.messages_command_shop_cart_failure_invalidPlayer, new HashMap<>() {{
                                            put("shop", shopId);
                                        }}));
                                        return;
                                    }
                                    if (shop == null) {
                                        DailyShop.ADVENTURE_MANAGER.sendMessageWithPrefix(sender, TextUtils.parseInternalVar(MessageConfig.messages_command_shop_cart_failure_invalidShop, new HashMap<>() {{
                                            put("shop", (String) args.get("shop"));
                                        }}));
                                        return;
                                    }
                                    shop.getCartGUI().open(player);
                                }),
                        new CommandAPICommand("settle")
                                .withPermission("dailyshop.command.shop.cart.remove")
                                .withArguments(
                                        new StringArgument("shop")
                                                .replaceSuggestions(ArgumentSuggestions.strings(ShopConfig.getAllId())),
                                        new PlayerArgument("player")
                                )
                                .executes((sender, args) -> {
                                    String shopId = (String) args.get("shop");
                                    Shop shop = DailyShop.SHOP_FACTORY.getShop(shopId);
                                    Player player = (Player) args.get("player");
                                    Profile profile = DailyShop.PROFILE_FACTORY.getProfile(player);
                                    if (player == null) {
                                        DailyShop.ADVENTURE_MANAGER.sendMessageWithPrefix(sender, TextUtils.parseInternalVar(MessageConfig.messages_command_shop_cart_failure_invalidPlayer, new HashMap<>() {{
                                            put("shop", shopId);
                                        }}));
                                        return;
                                    }
                                    if (shop == null) {
                                        DailyShop.ADVENTURE_MANAGER.sendMessageWithPrefix(sender, TextUtils.parseInternalVar(MessageConfig.messages_command_shop_cart_failure_invalidShop, new HashMap<>() {{
                                            put("shop", (String) args.get("shop"));
                                        }}));
                                        return;
                                    }
                                    if (profile == null) {
                                        // TODO 无档案提示
                                        DailyShop.ADVENTURE_MANAGER.sendMessageWithPrefix(sender, TextUtils.parseInternalVar(MessageConfig.messages_command_shop_cart_failure_invalidShop, new HashMap<>() {{
                                            put("shop", (String) args.get("shop"));
                                        }}));
                                        return;
                                    }
                                    profile.settleCart(shop);
                                })
                );
    }

    private CommandAPICommand getShopMiscCommand() {
        return new CommandAPICommand("misc")
                .withSubcommands(
                        new CommandAPICommand("switch-shopping-mode")
                                .withPermission("dailyshop.command.shop.misc.switch-shopping-mode")
                                .withArguments(
                                        new StringArgument("shop")
                                                .replaceSuggestions(ArgumentSuggestions.strings(ShopConfig.getAllId())),
                                        new PlayerArgument("player")
                                )
                                .executes(((sender, args) -> {
                                    String shopId = (String) args.get("shop");
                                    Shop shop = DailyShop.SHOP_FACTORY.getShop(shopId);
                                    Player player = (Player) args.get("player");
                                    Profile profile = DailyShop.PROFILE_FACTORY.getProfile(player);
                                    if (player == null) {
                                        DailyShop.ADVENTURE_MANAGER.sendMessageWithPrefix(sender, TextUtils.parseInternalVar(MessageConfig.messages_command_shop_cart_failure_invalidPlayer, new HashMap<>() {{
                                            put("shop", shopId);
                                        }}));
                                        return;
                                    }
                                    if (shop == null) {
                                        DailyShop.ADVENTURE_MANAGER.sendMessageWithPrefix(sender, TextUtils.parseInternalVar(MessageConfig.messages_command_shop_cart_failure_invalidShop, new HashMap<>() {{
                                            put("shop", (String) args.get("shop"));
                                        }}));
                                        return;
                                    }
                                    if (profile == null) {
                                        // TODO 提示信息
                                        return;
                                    }
                                    profile.setShoppingMode(shopId, profile.getShoppingMode(shopId) == ShoppingMode.DIRECT ? ShoppingMode.CART : ShoppingMode.DIRECT);
                                    // TODO 提示消息
                                    DailyShop.ADVENTURE_MANAGER.sendMessageWithPrefix(sender, TextUtils.decorateTextKeepMiniMessage("<gray>成功将玩家 <white>{player} <gray>的购物模式切换为 <white>{mode}.", null, new HashMap<>() {{
                                        put("shop", shop.getName());
                                        put("player", player.getDisplayName());
                                        put("mode", profile.getShoppingMode(shopId).name());
                                    }}));
                                }))
                );
    }
}
