package cn.encmys.ykdz.forest.dailyshop.command.sub;

import cn.encmys.ykdz.forest.dailyshop.api.DailyShop;
import cn.encmys.ykdz.forest.dailyshop.api.config.MessageConfig;
import cn.encmys.ykdz.forest.dailyshop.api.utils.TextUtils;
import cn.encmys.ykdz.forest.hyphautils.HyphaAdventureUtils;
import dev.jorel.commandapi.CommandAPICommand;
import dev.jorel.commandapi.arguments.IntegerArgument;
import dev.jorel.commandapi.arguments.PlayerArgument;
import org.bukkit.entity.Player;

import java.util.HashMap;

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
                        HyphaAdventureUtils.sendMessage(sender, TextUtils.decorateText(MessageConfig.messages_prefix + MessageConfig.messages_command_history_open_failure_invalidPlayer, null, new HashMap<>()));
                        return;
                    }
                    DailyShop.PROFILE_FACTORY.getProfile(player).getOrderHistoryGUI().open(player);
                    HyphaAdventureUtils.sendMessage(sender, MessageConfig.messages_prefix + TextUtils.decorateText(MessageConfig.messages_command_history_open_success, player, new HashMap<>() {{
                        put("player-name", player.getName());
                    }}));
                });
    }

    private CommandAPICommand getHistoryCleanCommand() {
        return new CommandAPICommand("clean")
                .withPermission("dailyshop.command.history.clean")
                .withOptionalArguments(
                        new PlayerArgument("player"),
                        new IntegerArgument("day-late-than")
                )
                .executes((sender, args) -> {
                    Player player = (Player) args.get("player");
                    if (player == null) {
                        HyphaAdventureUtils.sendMessage(sender, TextUtils.decorateText(MessageConfig.messages_prefix + MessageConfig.messages_command_history_clean_failure_invalidPlayer, null, new HashMap<>()));
                        return;
                    }
                    Object dayLateThanData = args.get("day-late-than");
                    int dayLateThan = 31;
                    if (dayLateThanData instanceof Integer) {
                        dayLateThan = (Integer) dayLateThanData;
                    } else {
                        HyphaAdventureUtils.sendMessage(sender, TextUtils.decorateText(MessageConfig.messages_prefix + MessageConfig.messages_command_history_clean_failure_invalidDayLateThan, null, new HashMap<>() {{
                            put("day-late-than", (String) args.get("day-late-than"));
                        }}));
                    }
                    DailyShop.DATABASE_FACTORY.getSettlementLogDao().deleteLog(player.getUniqueId(), dayLateThan);
                    int finalDayLateThan = dayLateThan;
                    HyphaAdventureUtils.sendMessage(sender, TextUtils.decorateText(MessageConfig.messages_prefix + MessageConfig.messages_command_history_clean_success, null, new HashMap<>() {{
                        put("player-name", player.getName());
                        put("day-late-than", String.valueOf(finalDayLateThan));
                    }}));
                });
    }
}
