package cn.encmys.ykdz.forest.dailyshop.command;

import cn.encmys.ykdz.forest.dailyshop.DailyShop;
import cn.encmys.ykdz.forest.dailyshop.adventure.AdventureManager;
import cn.encmys.ykdz.forest.dailyshop.command.sub.ShopCommand;
import cn.encmys.ykdz.forest.dailyshop.config.MessageConfig;
import dev.jorel.commandapi.CommandAPICommand;

public class CommandHandler {
    private static final AdventureManager adventureManager = DailyShop.getAdventureManager();

    public CommandHandler(DailyShop plugin) {
    }

    public void load() {
        new CommandAPICommand("dailyshop")
                .withPermission("dailyshop.admin")
                .withSubcommands(
                        getReloadCommand(),
                        ShopCommand.INSTANCE.getShopCommand()
                )
                .register();
    }

    public void unload() {

    }

    public CommandAPICommand getReloadCommand() {
        return new CommandAPICommand("reload")
                .withPermission("dailyshop.command.reload")
                .executes((sender, args) -> {
                    DailyShop.reload();
                    adventureManager.sendMessageWithPrefix(sender, MessageConfig.messages_command_reload_success);
                });
    }
}
