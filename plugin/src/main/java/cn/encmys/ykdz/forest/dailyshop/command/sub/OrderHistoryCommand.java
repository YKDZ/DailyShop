package cn.encmys.ykdz.forest.dailyshop.command.sub;

import cn.encmys.ykdz.forest.dailyshop.api.DailyShop;
import cn.encmys.ykdz.forest.dailyshop.api.config.MessageConfig;
import cn.encmys.ykdz.forest.dailyshop.api.utils.TextUtils;
import dev.jorel.commandapi.CommandAPICommand;
import dev.jorel.commandapi.arguments.IntegerArgument;
import dev.jorel.commandapi.arguments.PlayerArgument;
import org.bukkit.entity.Player;

import java.util.HashMap;
import java.util.UUID;
import java.util.concurrent.ExecutionException;

public class OrderHistoryCommand {
    public static OrderHistoryCommand INSTANCE = new OrderHistoryCommand();

    public CommandAPICommand getHistoryCommand() {
        return new CommandAPICommand("history")
                .withSubcommands(
                        getHistoryOpenCommand(),
                        getHistoryCleanCommand()
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

    private CommandAPICommand getHistoryCleanCommand() {
        return new CommandAPICommand("clean")
                .withPermission("dailyshop.command.history.clean")
                .withOptionalArguments(
                        new PlayerArgument("player"),
                        new IntegerArgument("day")
                )
                .executes((sender, args) -> {
                    UUID targetUUID = null;
                    int day;
                    Player player = (Player) args.get("player");
                    if (player == null) {
                        DailyShop.ADVENTURE_MANAGER.sendMessageWithPrefix(sender, TextUtils.decorateTextKeepMiniMessage(MessageConfig.messages_command_shop_cart_failure_invalidPlayer, player, new HashMap<>() {{

                        }}));
                        return;
                    }
                    targetUUID = player.getUniqueId();
                    if (args.get("day") != null) {
                        day = Integer.parseInt(args.get("day").toString());
                    } else {
                        day = Integer.MAX_VALUE;
                    }
                    UUID finalTargetUUID = targetUUID;
                    DailyShop.INSTANCE.getServer().getScheduler().runTaskAsynchronously(DailyShop.INSTANCE, () -> {
                        try {
                            int amount = DailyShop.DATABASE.cleanLogs(finalTargetUUID, day).get();
                        } catch (InterruptedException | ExecutionException e) {
                            throw new RuntimeException(e);
                        }
                    });
                });
    }
}
