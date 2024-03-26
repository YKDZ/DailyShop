package cn.encmys.ykdz.forest.dailyshop.command.sub;

import cn.encmys.ykdz.forest.dailyshop.DailyShop;
import cn.encmys.ykdz.forest.dailyshop.adventure.AdventureManager;
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
    private static final AdventureManager adventureManager = DailyShop.getAdventureManager();
    public static ShopCommand INSTANCE = new ShopCommand();

    public CommandAPICommand getShopCommand() {
        return new CommandAPICommand("shop")
                .withSubcommands(
                        getShopOpenCommand(),
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
                    Shop shop = DailyShop.getShopFactory().getShop(shopId);
                    if (!sender.hasPermission("dailyshop.shop.open." + shopId)) {
                        adventureManager.sendMessageWithPrefix(sender, MessageConfig.messages_noPermission);
                        return;
                    }
                    if (shop == null) {
                        adventureManager.sendMessageWithPrefix(sender, TextUtils.parseInternalVariables(MessageConfig.messages_command_shop_open_failure_invalidShop, new HashMap<>() {{
                            put("shop", (String) args.get("shop"));
                        }}));
                        return;
                    }
                    shop.open((Player) args.get("player"));
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
                    Shop shop = DailyShop.getShopFactory().getShop((String) args.get("shop"));
                    if (shop == null) {
                        adventureManager.sendMessageWithPrefix(sender, TextUtils.parseInternalVariables(MessageConfig.messages_command_shop_restock_failure_invalidShop, new HashMap<>() {{
                            put("shop", (String) args.get("shop"));
                        }}));
                        return;
                    }
                    shop.restock();
                    adventureManager.sendMessageWithPrefix(sender, TextUtils.parseInternalVariables(MessageConfig.messages_command_shop_restock_success, new HashMap<>() {{
                        put("shop", shop.getName());
                    }}));
                });
    }

    private CommandAPICommand getShopSaveCommand() {
        return new CommandAPICommand("save")
                .withPermission("dailyshop.command.shop.save")
                .executes((sender, args) -> {
                    adventureManager.sendMessageWithPrefix(sender, MessageConfig.messages_command_shop_save_success);
                });
    }
}
