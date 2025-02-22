package cn.encmys.ykdz.forest.hyphashop.command.sub;

import cn.encmys.ykdz.forest.hyphashop.api.HyphaShop;
import cn.encmys.ykdz.forest.hyphashop.config.MessageConfig;
import cn.encmys.ykdz.forest.hyphashop.utils.MessageUtils;
import cn.encmys.ykdz.forest.hyphashop.utils.TextUtils;
import cn.encmys.ykdz.forest.hyphashop.utils.VarUtils;
import cn.encmys.ykdz.forest.hyphautils.HyphaAdventureUtils;
import com.mojang.brigadier.Command;
import com.mojang.brigadier.tree.CommandNode;
import io.papermc.paper.command.brigadier.CommandSourceStack;
import io.papermc.paper.command.brigadier.Commands;
import io.papermc.paper.command.brigadier.argument.ArgumentTypes;
import io.papermc.paper.command.brigadier.argument.resolvers.selector.PlayerSelectorArgumentResolver;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class OrderHistoryCommand {
    public static CommandNode<CommandSourceStack> getHistoryCommand() {
        return Commands.literal("history")
                .then(getHistoryOpenCommand())
                .build();
    }

    private static CommandNode<CommandSourceStack> getHistoryOpenCommand() {
        return Commands.literal("open")
                .requires(ctx -> ctx.getSender().hasPermission("hyphashop.history.open"))
                .then(Commands.argument("target", ArgumentTypes.player())
                        .executes((ctx) -> {
                            CommandSender sender = ctx.getSource().getSender();
                            Player target = ctx.getArgument("target", PlayerSelectorArgumentResolver.class).resolve(ctx.getSource()).getFirst();
                            if (target == null) {
                                MessageUtils.sendMessage(sender, MessageConfig.messages_command_history_open_failure_invalidPlayer, VarUtils.extractVars(sender, null));
                                return Command.SINGLE_SUCCESS;
                            }
                            HyphaShop.PROFILE_FACTORY.getProfile(target).getOrderHistoryGUI().open(target);
                            MessageUtils.sendMessage(sender, MessageConfig.messages_command_history_open_success, VarUtils.extractVars(sender, null));
                            return Command.SINGLE_SUCCESS;
                        })
                )
                .build();
    }

    // TODO 整理
//    private CommandNode<CommandSourceStack> getHistoryCleanCommand() {
//        return Commands.literal("clean")
//                .withOptionalArguments(
//                        new PlayerArgument("player"),
//                        new IntegerArgument("day-late-than")
//                )
//                .executes((sender, args) -> {
//                    Player player = (Player) args.get("player");
//                    if (player == null) {
//                        HyphaAdventureUtils.sendMessage(sender, TextUtils.decorateText(MessageConfig.messages_prefix + MessageConfig.messages_command_history_clean_failure_invalidPlayer, null, new HashMap<>()));
//                        return;
//                    }
//                    Object dayLateThanData = args.get("day-late-than");
//                    int dayLateThan = 31;
//                    if (dayLateThanData instanceof Integer) {
//                        dayLateThan = (Integer) dayLateThanData;
//                    } else {
//                        HyphaAdventureUtils.sendMessage(sender, TextUtils.decorateText(MessageConfig.messages_prefix + MessageConfig.messages_command_history_clean_failure_invalidDayLateThan, null, new HashMap<>() {{
//                            put("day_late_than", args.get("day-late-than"));
//                        }}));
//                    }
//                    HyphaShop.DATABASE_FACTORY.getSettlementLogDao().deleteLog(player.getUniqueId(), dayLateThan);
//                    int finalDayLateThan = dayLateThan;
//                    HyphaAdventureUtils.sendMessage(sender, TextUtils.decorateText(MessageConfig.messages_prefix + MessageConfig.messages_command_history_clean_success, null, new HashMap<>() {{
//                        putAll(VarUtils.extractVars(player, null));
//                        put("day_late_than", String.valueOf(finalDayLateThan));
//                    }}));
//                });
//    }
}
