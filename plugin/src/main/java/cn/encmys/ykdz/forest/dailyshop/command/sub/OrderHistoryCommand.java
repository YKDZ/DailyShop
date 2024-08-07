package cn.encmys.ykdz.forest.dailyshop.command.sub;

import cn.encmys.ykdz.forest.dailyshop.api.DailyShop;
import cn.encmys.ykdz.forest.dailyshop.api.config.MessageConfig;
import cn.encmys.ykdz.forest.dailyshop.api.utils.TextUtils;
import dev.jorel.commandapi.CommandAPICommand;
import dev.jorel.commandapi.arguments.PlayerArgument;
import org.bukkit.entity.Player;

import java.util.HashMap;

public class OrderHistoryCommand {
    public static OrderHistoryCommand INSTANCE = new OrderHistoryCommand();

    public CommandAPICommand getHistoryCommand() {
        return new CommandAPICommand("history")
                .withSubcommands(
                        getHistoryOpenCommand()
                );
    }

    private CommandAPICommand getHistoryOpenCommand() {
        return new CommandAPICommand("open")
                .withPermission("dailyshop.command.history.open")
                .withArguments(
                        new PlayerArgument("player")
                )
                .executes((sender, args) -> {
                    Player player = (Player) args.get("player");
                    if (player == null) {
                        DailyShop.ADVENTURE_MANAGER.sendMessageWithPrefix(sender, TextUtils.decorateTextKeepMiniMessage(MessageConfig.messages_command_shop_cart_failure_invalidPlayer, player, new HashMap<>() {{

                        }}));
                        return;
                    }
                    DailyShop.PROFILE_FACTORY.getProfile(player).getOrderHistoryGUI().open();
                });
    }
}
