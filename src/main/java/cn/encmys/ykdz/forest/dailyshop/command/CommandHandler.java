package cn.encmys.ykdz.forest.dailyshop.command;

import cn.encmys.ykdz.forest.dailyshop.DailyShop;
import cn.encmys.ykdz.forest.dailyshop.command.sub.ShopCommand;
import dev.jorel.commandapi.CommandAPI;
import dev.jorel.commandapi.CommandAPIBukkitConfig;
import dev.jorel.commandapi.CommandAPICommand;

public class CommandHandler {

    public CommandHandler(DailyShop plugin) {
        CommandAPI.onLoad(new CommandAPIBukkitConfig(plugin).silentLogs(true));
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
                .executes((sender, args) -> {
                });
    }
}
