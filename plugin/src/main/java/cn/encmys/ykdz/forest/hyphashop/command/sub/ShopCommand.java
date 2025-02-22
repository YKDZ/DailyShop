package cn.encmys.ykdz.forest.hyphashop.command.sub;

import cn.encmys.ykdz.forest.hyphashop.api.HyphaShop;
import cn.encmys.ykdz.forest.hyphashop.api.shop.Shop;
import cn.encmys.ykdz.forest.hyphashop.config.MessageConfig;
import cn.encmys.ykdz.forest.hyphashop.utils.MessageUtils;
import cn.encmys.ykdz.forest.hyphashop.utils.VarUtils;
import com.mojang.brigadier.Command;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.tree.CommandNode;
import io.papermc.paper.command.brigadier.CommandSourceStack;
import io.papermc.paper.command.brigadier.Commands;
import io.papermc.paper.command.brigadier.argument.ArgumentTypes;
import io.papermc.paper.command.brigadier.argument.resolvers.selector.PlayerSelectorArgumentResolver;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class ShopCommand {
    public static CommandNode<CommandSourceStack> getShopCommand() {
        return Commands.literal("shop")
                .then(getShopOpenCommand())
                .then(getShopRestockCommand())
                .build();
    }

    private static CommandNode<CommandSourceStack> getShopOpenCommand() {
        return Commands.literal("open")
                .requires(ctx -> ctx.getSender().hasPermission("hyphashop.shop.open"))
                .then(Commands.argument("shop", StringArgumentType.string())
                        .suggests((ctx, builder) -> {
                            HyphaShop.SHOP_FACTORY.getShops().keySet().stream()
                                    .filter(shopId -> shopId.startsWith(builder.getRemainingLowerCase()))
                                    .forEach(builder::suggest);
                            return builder.buildFuture();
                        })
                        .then(Commands.argument("target", ArgumentTypes.player())
                                .executes((ctx) -> {
                                    CommandSender sender = ctx.getSource().getSender();
                                    String shopId = ctx.getArgument("shop", String.class);
                                    Player target = ctx.getArgument("target", PlayerSelectorArgumentResolver.class).resolve(ctx.getSource()).getFirst();
                                    Shop shop = HyphaShop.SHOP_FACTORY.getShop(shopId);
                                    if (target == null) {
                                        MessageUtils.sendMessage(sender, MessageConfig.messages_command_shop_open_failure_invalidPlayer, VarUtils.extractVars(sender, shop));
                                        return Command.SINGLE_SUCCESS;
                                    }
                                    if (!sender.hasPermission("hyphashop.shop.open." + shopId)) {
                                        MessageUtils.sendMessage(sender, MessageConfig.messages_noPermission, VarUtils.extractVars(sender, shop));
                                        return Command.SINGLE_SUCCESS;
                                    }
                                    if (shop == null) {
                                        MessageUtils.sendMessage(sender, MessageConfig.messages_command_shop_open_failure_invalidShop, VarUtils.extractVars(sender, shop));
                                        return Command.SINGLE_SUCCESS;
                                    }
                                    MessageUtils.sendMessage(sender, MessageConfig.messages_command_shop_open_success, VarUtils.extractVars(sender, shop));
                                    shop.getShopGUI().open(target);
                                    return Command.SINGLE_SUCCESS;
                                })
                        )
                )
                .build();
    }

    private static CommandNode<CommandSourceStack> getShopRestockCommand() {
        return Commands.literal("restock")
                .requires(ctx -> ctx.getSender().hasPermission("hyphashop.shop.restock"))
                .then(Commands.argument("shop", StringArgumentType.string())
                        .suggests((ctx, builder) -> {
                            HyphaShop.SHOP_FACTORY.getShops().keySet().stream()
                                    .filter(shopId -> shopId.startsWith(builder.getRemainingLowerCase()))
                                    .forEach(builder::suggest);
                            return builder.buildFuture();
                        })
                        .executes((ctx) -> {
                            CommandSender sender = ctx.getSource().getSender();
                            String shopId = ctx.getArgument("shop", String.class);
                            Shop shop = HyphaShop.SHOP_FACTORY.getShop(shopId);
                            if (shop == null) {
                                MessageUtils.sendMessage(sender, MessageConfig.messages_command_shop_restock_failure_invalidShop, VarUtils.extractVars(sender, shop));
                                return Command.SINGLE_SUCCESS;
                            }
                            shop.getShopStocker().stock();
                            MessageUtils.sendMessage(sender, MessageConfig.messages_command_shop_restock_success, VarUtils.extractVars(sender, shop));
                            return Command.SINGLE_SUCCESS;
                        })
                )
                .build();
    }
}
