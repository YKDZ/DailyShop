package cn.encmys.ykdz.forest.hyphashop.utils;

import cn.encmys.ykdz.forest.hyphashop.api.HyphaShop;
import me.clip.placeholderapi.PlaceholderAPI;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.IntStream;

public class CommandUtils {
    public static void dispatchCommands(@NotNull Player player, @NotNull List<String> commands, @Nullable Map<String, Object> vars) {
        dispatchCommands(player, TextUtils.parseInternalVar(TextUtils.parsePlaceholder(commands, player), vars));
    }

    public static void dispatchCommands(@NotNull Player player, @Nullable List<String> commands) {
        if (commands == null) {
            return;
        }

        for (String command : commands) {
            dispatchCommand(player, command);
        }
    }

    public static void dispatchCommand(@NotNull Player player, @NotNull String command) {
        command = PlaceholderAPI.setPlaceholders(player, command);

        Map<String, String> params = new HashMap<>();

        Matcher matcher = Pattern.compile("^(-\\w+:[^\\s]+\\s+)+").matcher(command);
        String paramsPart = matcher.find() ? matcher.group(0) : "";

        Matcher pairMatcher = Pattern.compile("-(\\w+):([^\\s]+)").matcher(paramsPart);
        while (pairMatcher.find()) {
            params.put(pairMatcher.group(1), pairMatcher.group(2));
        }

        String parsedCommand = command.substring(paramsPart.length()).trim();

        CommandSender commandSender;

        if (params.getOrDefault("p", "false").equals("true")) {
            commandSender = player;
            if (params.getOrDefault("op", "false").equals("true")) {
                commandSender.setOp(true);
            }
        } else {
            commandSender = Bukkit.getConsoleSender();
        }

        int repeat = params.containsKey("r") ? Integer.parseInt(params.get("r")) : 1;
        int delay = params.containsKey("d") ? Integer.parseInt(params.get("d")) : 0;

        CommandSender finalCommandSender = commandSender;
        if (delay > 0) {
            Bukkit.getScheduler().runTaskLaterAsynchronously(
                    HyphaShop.INSTANCE,
                    () -> IntStream.range(0, repeat).forEach(i -> Bukkit.dispatchCommand(finalCommandSender, parsedCommand)),
                    delay
            );
        } else {
            IntStream.range(0, repeat).forEach(i -> Bukkit.dispatchCommand(finalCommandSender, parsedCommand));
        }
    }
}
