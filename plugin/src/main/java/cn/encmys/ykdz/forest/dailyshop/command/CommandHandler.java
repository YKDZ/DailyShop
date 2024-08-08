package cn.encmys.ykdz.forest.dailyshop.command;

import cn.encmys.ykdz.forest.dailyshop.api.DailyShop;
import cn.encmys.ykdz.forest.dailyshop.api.config.MessageConfig;
import cn.encmys.ykdz.forest.dailyshop.command.sub.CartCommand;
import cn.encmys.ykdz.forest.dailyshop.command.sub.OrderHistoryCommand;
import cn.encmys.ykdz.forest.dailyshop.command.sub.ProductCommand;
import cn.encmys.ykdz.forest.dailyshop.command.sub.ShopCommand;
import dev.jorel.commandapi.CommandAPICommand;

public class CommandHandler {
    public static void load() {
        new CommandAPICommand("dailyshop")
                .withSubcommands(
                        getReloadCommand(),
                        getSaveCommand(),
                        ShopCommand.INSTANCE.getShopCommand(),
                        ProductCommand.INSTANCE.getShopCommand(),
                        CartCommand.INSTANCE.getCartCommand(),
                        OrderHistoryCommand.INSTANCE.getHistoryCommand()
                )
                .register();
    }

    private static CommandAPICommand getReloadCommand() {
        return new CommandAPICommand("reload")
                .withPermission("dailyshop.command.reload")
                .executes((sender, args) -> {
                    DailyShop.INSTANCE.reload();
                    DailyShop.ADVENTURE_MANAGER.sendMessageWithPrefix(sender, MessageConfig.messages_command_reload_success);
                });
    }

    private static CommandAPICommand getSaveCommand() {
        return new CommandAPICommand("save")
                .withPermission("dailyshop.command.save")
                .executes((sender, args) -> {
                    DailyShop.PROFILE_FACTORY.save();
                    DailyShop.PRODUCT_FACTORY.save();
                    DailyShop.SHOP_FACTORY.save();
                    DailyShop.ADVENTURE_MANAGER.sendMessageWithPrefix(sender, MessageConfig.messages_command_save_success);
                });
    }
}
