package cn.encmys.ykdz.forest.dailyshop.util;

import me.clip.placeholderapi.PlaceholderAPI;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.Nullable;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.IntStream;

public class CommandUtils {
    public static void dispatchCommands(Player player, @Nullable List<String> commands) {
        if (commands == null) {
            return;
        }

        for (String command : commands) {
            dispatchCommand(player, command);
        }
    }

    public static void dispatchCommand(Player player, String command) {
        Map<String, String> params = new HashMap<>();

        Matcher matcher = Pattern.compile("^(-\\w+:[^\\s]+\\s+)+").matcher(command);
        String paramsPart = matcher.find() ? matcher.group(0) : "";

        Matcher pairMatcher = Pattern.compile("-(\\w+):([^\\s]+)").matcher(paramsPart);
        while (pairMatcher.find()) {
            params.put(pairMatcher.group(1), pairMatcher.group(2));
        }

        String parsedCommand = PlaceholderAPI.setPlaceholders(player, command.substring(paramsPart.length()).trim());

        CommandSender commandSender = params.getOrDefault("p", "false").equals("true") ? player : Bukkit.getConsoleSender();
        // OP / Delay
        int repeat = params.containsKey("repeat") ? Integer.parseInt(params.get("repeat")) : 1;

        IntStream.range(0, repeat).forEach(i -> Bukkit.dispatchCommand(commandSender, parsedCommand));
    }

}
