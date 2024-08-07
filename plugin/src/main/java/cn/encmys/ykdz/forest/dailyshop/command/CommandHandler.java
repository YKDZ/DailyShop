package cn.encmys.ykdz.forest.dailyshop.command;

import cn.encmys.ykdz.forest.dailyshop.api.DailyShop;
import cn.encmys.ykdz.forest.dailyshop.api.config.MessageConfig;
import cn.encmys.ykdz.forest.dailyshop.command.sub.CartCommand;
import cn.encmys.ykdz.forest.dailyshop.command.sub.ProductCommand;
import cn.encmys.ykdz.forest.dailyshop.command.sub.ShopCommand;
import dev.jorel.commandapi.CommandAPICommand;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import xyz.xenondevs.invui.gui.Gui;
import xyz.xenondevs.invui.item.builder.ItemBuilder;
import xyz.xenondevs.invui.item.impl.SimpleItem;
import xyz.xenondevs.invui.window.Window;

public class CommandHandler {
    public static void load() {
        new CommandAPICommand("dailyshop")
                .withSubcommands(
                        getReloadCommand(),
                        getSaveCommand(),
                        getTestCommand(),
                        ShopCommand.INSTANCE.getShopCommand(),
                        ProductCommand.INSTANCE.getShopCommand(),
                        CartCommand.INSTANCE.getCartCommand()
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

    private static CommandAPICommand getTestCommand() {
        return new CommandAPICommand("test")
                .executes((sender, args) -> {
                    Player player = (Player) sender;
                    Window window1 = Window.single()
                            .setGui(Gui.normal()
                                    .setStructure(
                                            "# # # # # # # # #",
                                            "# . . . . . . . #",
                                            "# . . . . . . . #",
                                            "# # # # # # # # #")
                                    .addIngredient('#', new SimpleItem(new ItemBuilder(Material.BLACK_STAINED_GLASS_PANE)))
                                    .build())
                            .addCloseHandler(() -> {
                                Window window2 = Window.single()
                                        .setGui(Gui.normal()
                                                .setStructure(
                                                        "# # # # # # # # #",
                                                        "# . . . . . . . #",
                                                        "# . . . . . . . #",
                                                        "# # # # # # # # #")
                                                .addIngredient('#', new SimpleItem(new ItemBuilder(Material.RED_STAINED_GLASS_PANE)))
                                                .build())
                                        .build(player);
                                window2.open();
                            })
                            .build(player);
                    window1.open();
                });
    }
}
