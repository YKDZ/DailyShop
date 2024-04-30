package cn.encmys.ykdz.forest.dailyshop.command.sub;

import cn.encmys.ykdz.forest.dailyshop.DailyShop;
import cn.encmys.ykdz.forest.dailyshop.config.MessageConfig;
import cn.encmys.ykdz.forest.dailyshop.config.ShopConfig;
import cn.encmys.ykdz.forest.dailyshop.shop.Shop;
import cn.encmys.ykdz.forest.dailyshop.util.TextUtils;
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
                        getShopSaveCommand()
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
                    if (!sender.hasPermission("dailyshop.shop.open." + shopId)) {
                        DailyShop.ADVENTURE_MANAGER.sendMessageWithPrefix(sender, MessageConfig.messages_noPermission);
                        return;
                    }
                    if (shop == null) {
                        DailyShop.ADVENTURE_MANAGER.sendMessageWithPrefix(sender, TextUtils.parseInternalVariables(MessageConfig.messages_command_shop_open_failure_invalidShop, new HashMap<>() {{
                            put("shop", (String) args.get("shop"));
                        }}));
                        return;
                    }
                    shop.getShopGUI().open((Player) args.get("player"));
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
                    if (!sender.hasPermission("dailyshop.shop.history." + shopId)) {
                        DailyShop.ADVENTURE_MANAGER.sendMessageWithPrefix(sender, MessageConfig.messages_noPermission);
                        return;
                    }
                    if (shop == null) {
                        DailyShop.ADVENTURE_MANAGER.sendMessageWithPrefix(sender, TextUtils.parseInternalVariables(MessageConfig.messages_command_shop_history_failure_invalidShop, new HashMap<>() {{
                            put("shop", (String) args.get("shop"));
                        }}));
                        return;
                    }
                    shop.getHistoryGUI().open((Player) args.get("player"));
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
                    Shop shop = DailyShop.SHOP_FACTORY.getShop((String) args.get("shop"));
                    if (shop == null) {
                        DailyShop.ADVENTURE_MANAGER.sendMessageWithPrefix(sender, TextUtils.parseInternalVariables(MessageConfig.messages_command_shop_restock_failure_invalidShop, new HashMap<>() {{
                            put("shop", (String) args.get("shop"));
                        }}));
                        return;
                    }
                    shop.restock();
                    DailyShop.ADVENTURE_MANAGER.sendMessageWithPrefix(sender, TextUtils.parseInternalVariables(MessageConfig.messages_command_shop_restock_success, new HashMap<>() {{
                        put("shop", shop.getName());
                    }}));
                });
    }

    private CommandAPICommand getShopSaveCommand() {
        return new CommandAPICommand("save")
                .withPermission("dailyshop.command.shop.save")
                .executes((sender, args) -> {
                    DailyShop.ADVENTURE_MANAGER.sendMessageWithPrefix(sender, MessageConfig.messages_command_shop_save_success);
                });
    }
}
