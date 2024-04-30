package cn.encmys.ykdz.forest.dailyshop.command;

import cn.encmys.ykdz.forest.dailyshop.DailyShop;
import cn.encmys.ykdz.forest.dailyshop.command.sub.ProductCommand;
import cn.encmys.ykdz.forest.dailyshop.command.sub.ShopCommand;
import cn.encmys.ykdz.forest.dailyshop.config.MessageConfig;
import dev.jorel.commandapi.CommandAPICommand;

public class CommandHandler {
    public CommandHandler(DailyShop plugin) {
    }

    public void load() {
        new CommandAPICommand("dailyshop")
                .withSubcommands(
                        getReloadCommand(),
                        ShopCommand.INSTANCE.getShopCommand(),
                        ProductCommand.INSTANCE.getShopCommand()
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
                    DailyShop.ADVENTURE_MANAGER.sendMessageWithPrefix(sender, MessageConfig.messages_command_reload_success);
                });
    }
}
