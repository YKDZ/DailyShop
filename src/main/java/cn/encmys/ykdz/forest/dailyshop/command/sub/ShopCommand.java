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
                .withArguments(
                        new StringArgument("shop")
                                .replaceSuggestions(ArgumentSuggestions.strings(ShopConfig.getAllId())),
                        new PlayerArgument("player")
                )
                .executes((sender, args) -> {
                    DailyShop.getShopFactory().getShop((String) args.get("shop")).openGUI((Player) args.get("player"));
                });
    }

    private CommandAPICommand getShopRestockCommand() {
        return new CommandAPICommand("restock")
                .withArguments(
                        new StringArgument("shop")
                                .replaceSuggestions(ArgumentSuggestions.strings(ShopConfig.getAllId()))
                )
                .executes((sender, args) -> {
                    Shop shop = DailyShop.getShopFactory().getShop((String) args.get("shop"));
                    shop.restock();
                    adventureManager.sendMessageWithPrefix(sender, TextUtils.parseInternalVariables(MessageConfig.messages_command_restock, new HashMap<>() {{
                        put("shop", shop.getName());
                    }}));
                });
    }

    private CommandAPICommand getShopSaveCommand() {
        return new CommandAPICommand("save")
                .executes((sender, args) -> {
                    for (Shop shop : DailyShop.getShopFactory().getAllShops().values()) {
                        shop.saveData();
                    }
                    adventureManager.sendMessageWithPrefix(sender, MessageConfig.messages_command_save);
                });
    }
}
