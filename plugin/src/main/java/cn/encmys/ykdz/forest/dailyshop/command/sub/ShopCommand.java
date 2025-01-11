package cn.encmys.ykdz.forest.dailyshop.command.sub;

import cn.encmys.ykdz.forest.dailyshop.api.DailyShop;
import cn.encmys.ykdz.forest.dailyshop.api.config.MessageConfig;
import cn.encmys.ykdz.forest.dailyshop.api.config.ShopConfig;
import cn.encmys.ykdz.forest.dailyshop.api.profile.Profile;
import cn.encmys.ykdz.forest.dailyshop.api.profile.enums.ShoppingMode;
import cn.encmys.ykdz.forest.dailyshop.api.shop.Shop;
import cn.encmys.ykdz.forest.dailyshop.api.utils.PlayerUtils;
import cn.encmys.ykdz.forest.dailyshop.api.utils.TextUtils;
import cn.encmys.ykdz.forest.hyphautils.HyphaAdventureUtils;
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
                        getShopRestockCommand(),
                        getShopCacheCommand(),
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
                        HyphaAdventureUtils.sendMessage(sender, TextUtils.decorateText(MessageConfig.messages_prefix + MessageConfig.messages_command_shop_open_failure_invalidPlayer, null, new HashMap<>() {{
                            put("shop-id", shopId);
                        }}));
                        return;
                    }
                    if (!sender.hasPermission("dailyshop.shop.open." + shopId)) {
                        HyphaAdventureUtils.sendMessage(sender, MessageConfig.messages_prefix + MessageConfig.messages_noPermission);
                        return;
                    }
                    if (shop == null) {
                        HyphaAdventureUtils.sendMessage(sender, MessageConfig.messages_prefix + TextUtils.decorateText(MessageConfig.messages_command_shop_open_failure_invalidShop, player, new HashMap<>() {{
                            put("shop-id", (String) args.get("shop"));
                        }}));
                        return;
                    }
                    PlayerUtils.sendMessage(MessageConfig.messages_command_shop_open_success, player, new HashMap<>() {{
                        put("shop-id", shopId);
                        put("shop-name", shop.getName());
                    }});
                    shop.getShopGUI().open(player);
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
                    Player player = (Player) sender;
                    if (shop == null) {
                        HyphaAdventureUtils.sendMessage(sender, TextUtils.decorateText(MessageConfig.messages_prefix + MessageConfig.messages_command_shop_restock_failure_invalidShop, player, new HashMap<>() {{
                            put("shop-id", shopId);
                        }}));
                        return;
                    }
                    shop.getShopStocker().stock();
                    HyphaAdventureUtils.sendMessage(sender, TextUtils.decorateText(MessageConfig.messages_prefix + MessageConfig.messages_command_shop_restock_success, player, new HashMap<>() {{
                        put("shop-name", shop.getName());
                        put("shop-id", shop.getId());
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
                                    Player player = (Player) sender;
                                    if (shop == null) {
                                        HyphaAdventureUtils.sendMessage(sender, TextUtils.decorateText(MessageConfig.messages_prefix + MessageConfig.messages_command_shop_cache_clear_failure_invalidShop, player, new HashMap<>() {{
                                            put("shop-id", shopId);
                                        }}));
                                        return;
                                    }
                                    shop.getCachedProductItems().clear();
                                    HyphaAdventureUtils.sendMessage(sender, TextUtils.decorateText(MessageConfig.messages_prefix + MessageConfig.messages_command_shop_cache_clear_success, player, new HashMap<>() {{
                                        put("shop-id", shopId);
                                        put("shop-name", shop.getName());
                                    }}));
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
                                        HyphaAdventureUtils.sendMessage(sender, TextUtils.decorateText(MessageConfig.messages_command_shop_misc_switchShoppingMode_failure_invalidPlayer, null, new HashMap<>() {{
                                            put("shop-id", shopId);
                                        }}));
                                        return;
                                    }
                                    if (shop == null) {
                                        HyphaAdventureUtils.sendMessage(sender, TextUtils.decorateText(MessageConfig.messages_command_shop_misc_switchShoppingMode_failure_invalidShop, player, new HashMap<>() {{
                                            put("shop-id", (String) args.get("shop"));
                                        }}));
                                        return;
                                    }
                                    profile.setShoppingMode(shopId, profile.getShoppingMode(shopId) == ShoppingMode.DIRECT ? ShoppingMode.CART : ShoppingMode.DIRECT);
                                    HyphaAdventureUtils.sendMessage(sender, TextUtils.decorateText(MessageConfig.messages_command_shop_misc_switchShoppingMode_success, null, new HashMap<>() {{
                                        put("shop-id", shopId);
                                        put("shop-name", shop.getName());
                                        put("player-name", player.getDisplayName());
                                        put("mode", profile.getShoppingMode(shopId).name());
                                    }}));
                                }))
                );
    }
}
