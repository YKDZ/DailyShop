package cn.encmys.ykdz.forest.hyphashop.utils;

import cn.encmys.ykdz.forest.hyphashop.config.MessageConfig;
import cn.encmys.ykdz.forest.hyphautils.HyphaAdventureUtils;
import org.bukkit.command.CommandSender;
import org.jetbrains.annotations.NotNull;

import java.util.Map;

public class MessageUtils {
    public static void sendMessage(@NotNull CommandSender sender, @NotNull String message, @NotNull Map<String, Object> vars) {
        if (message.isEmpty()) return;
        HyphaAdventureUtils.sendMessage(sender, MessageConfig.messages_prefix + TextUtils.decorateText(message, sender, vars));
    }
}
