package cn.encmys.ykdz.forest.hyphashop.command.sub;

import cn.encmys.ykdz.forest.hyphashop.api.HyphaShop;
import cn.encmys.ykdz.forest.hyphashop.api.profile.Profile;
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

public class CartCommand {
    public static CommandNode<CommandSourceStack> getCartCommand() {
        return Commands.literal("cart")
                .then(getCartOpenCommand())
                .build();
    }

    private static CommandNode<CommandSourceStack> getCartOpenCommand() {
        return Commands.literal("open")
                .requires(ctx -> ctx.getSender().hasPermission("hyphashop.cart.open"))
                .then(Commands.argument("target", ArgumentTypes.player())
                        .executes((ctx) -> {
                            CommandSender sender = ctx.getSource().getSender();
                            Player target = ctx.getArgument("target", PlayerSelectorArgumentResolver.class).resolve(ctx.getSource()).getFirst();
                            if (target == null) {
                                MessageUtils.sendMessage(sender, MessageConfig.messages_command_cart_open_failure_invalidPlayer, VarUtils.extractVars(sender, null));
                                return Command.SINGLE_SUCCESS;
                            }
                            Profile profile = HyphaShop.PROFILE_FACTORY.getProfile(target);
                            profile.getCartGUI().open(target);
                            MessageUtils.sendMessage(sender, MessageConfig.messages_command_cart_open_success, VarUtils.extractVars(sender, null));
                            return Command.SINGLE_SUCCESS;
                        })
                )
                .build();
    }
}
