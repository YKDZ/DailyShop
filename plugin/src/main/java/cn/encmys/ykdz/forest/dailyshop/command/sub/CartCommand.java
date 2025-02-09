package cn.encmys.ykdz.forest.dailyshop.command.sub;

import cn.encmys.ykdz.forest.dailyshop.api.DailyShop;
import cn.encmys.ykdz.forest.dailyshop.api.config.MessageConfig;
import cn.encmys.ykdz.forest.dailyshop.api.profile.Profile;
import cn.encmys.ykdz.forest.dailyshop.api.utils.TextUtils;
import cn.encmys.ykdz.forest.hyphautils.HyphaAdventureUtils;
import dev.jorel.commandapi.CommandAPICommand;
import dev.jorel.commandapi.arguments.PlayerArgument;
import org.bukkit.entity.Player;

import java.util.HashMap;

public class CartCommand {
    public static CartCommand INSTANCE = new CartCommand();

    public CommandAPICommand getCartCommand() {
        return new CommandAPICommand("cart")
                .withSubcommands(
                        getCartOpenCommand()
                );
    }

    private CommandAPICommand getCartOpenCommand() {
        return new CommandAPICommand("open")
                .withPermission("dailyshop.command.cart.open")
                .withArguments(
                        new PlayerArgument("player")
                )
                .executes((sender, args) -> {
                    Player player = (Player) args.get("player");
                    if (player == null) {
                        HyphaAdventureUtils.sendMessage(sender, MessageConfig.messages_prefix + TextUtils.decorateText(MessageConfig.messages_command_cart_open_failure_invalidPlayer, null, new HashMap<>()));
                        return;
                    }
                    Profile profile = DailyShop.PROFILE_FACTORY.getProfile(player);
                    profile.getCartGUI().open(player);
                    HyphaAdventureUtils.sendMessage(sender, TextUtils.decorateText(MessageConfig.messages_prefix + MessageConfig.messages_command_cart_open_success, null, new HashMap<>() {{
                        put("player-name", player.getName());
                    }}));
                });
    }
}
