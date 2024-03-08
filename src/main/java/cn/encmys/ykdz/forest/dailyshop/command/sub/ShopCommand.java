package cn.encmys.ykdz.forest.dailyshop.command.sub;

import cn.encmys.ykdz.forest.dailyshop.DailyShop;
import cn.encmys.ykdz.forest.dailyshop.config.ShopConfig;
import cn.encmys.ykdz.forest.dailyshop.shop.Shop;
import dev.jorel.commandapi.CommandAPICommand;
import dev.jorel.commandapi.arguments.ArgumentSuggestions;
import dev.jorel.commandapi.arguments.PlayerArgument;
import dev.jorel.commandapi.arguments.StringArgument;
import org.bukkit.entity.Player;

public class ShopCommand {
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
                    DailyShop.getShopFactory().getShop((String) args.get("shop")).restock();
                });
    }

    private CommandAPICommand getShopSaveCommand() {
        return new CommandAPICommand("save")
                .executes((sender, args) -> {
                    for(Shop shop : DailyShop.getShopFactory().getAllShops().values()) {
                        shop.saveData();
                    }
                });
    }
}
