package cn.encmys.ykdz.forest.hyphashop.command;

import cn.encmys.ykdz.forest.hyphashop.api.HyphaShop;
import cn.encmys.ykdz.forest.hyphashop.command.sub.CartCommand;
import cn.encmys.ykdz.forest.hyphashop.command.sub.OrderHistoryCommand;
import cn.encmys.ykdz.forest.hyphashop.command.sub.ShopCommand;
import cn.encmys.ykdz.forest.hyphashop.config.MessageConfig;
import cn.encmys.ykdz.forest.hyphautils.HyphaAdventureUtils;
import com.mojang.brigadier.Command;
import com.mojang.brigadier.tree.CommandNode;
import com.mojang.brigadier.tree.LiteralCommandNode;
import io.papermc.paper.command.brigadier.CommandSourceStack;
import io.papermc.paper.command.brigadier.Commands;

public class CommandHandler {
    public static LiteralCommandNode<CommandSourceStack> load() {
        return Commands.literal("hyphashop")
                .then(getReloadCommand())
                .then(getSaveCommand())
                .then(CartCommand.getCartCommand())
                .then(OrderHistoryCommand.getHistoryCommand())
                .then(ShopCommand.getShopCommand())
                .build();
    }

    private static CommandNode<CommandSourceStack> getReloadCommand() {
        return Commands.literal("reload")
                .executes((ctx) -> {
                    HyphaShop.INSTANCE.reload();
                    HyphaAdventureUtils.sendMessage(ctx.getSource().getSender(), MessageConfig.messages_prefix + MessageConfig.messages_command_reload_success);
                    return Command.SINGLE_SUCCESS;
                })
                .build();
    }

    private static CommandNode<CommandSourceStack> getSaveCommand() {
        return Commands.literal("save")
                .executes((ctx) -> {
                    HyphaShop.PROFILE_FACTORY.save();
                    HyphaShop.PRODUCT_FACTORY.save();
                    HyphaShop.SHOP_FACTORY.save();
                    HyphaAdventureUtils.sendMessage(ctx.getSource().getSender(), MessageConfig.messages_prefix + MessageConfig.messages_command_save_success);
                    return Command.SINGLE_SUCCESS;
                })
                .build();
    }
}
