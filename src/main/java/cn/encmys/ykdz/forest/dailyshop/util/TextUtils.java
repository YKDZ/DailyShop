package cn.encmys.ykdz.forest.dailyshop.util;

import cn.encmys.ykdz.forest.dailyshop.DailyShop;
import cn.encmys.ykdz.forest.dailyshop.adventure.AdventureManager;
import me.clip.placeholderapi.PlaceholderAPI;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class TextUtils {
    private static final AdventureManager adventureManager = DailyShop.getAdventureManager();
    private static final String listMarker = "|";

    public static List<String> decorateText(List<String> text, @Nullable Player player) {
        List<String> result = new ArrayList<>();
        for (String line : text) {
            result.add(decorateText(line, player));
        }
        return result;
    }

    public static String decorateText(String text, @Nullable Player player) {
        return adventureManager.componentToLegacy(adventureManager.getComponentFromMiniMessage(PlaceholderAPI.setPlaceholders(player, text)));
    }

    public static List<String> parseInternalVariables(List<String> lines, Map<String, String> vars) {
        if (lines == null) {
            return null;
        }

        List<String> result = new ArrayList<>();
        for (String line : lines) {
            result.add(parseInternalVariables(line, vars));
        }
        return result;
    }

    public static String parseInternalVariables(String line, Map<String, String> vars) {
        if (line != null && line.startsWith("?")) {
            for (String key : vars.keySet()) {
                if (line.contains("{" + key + "}") && vars.get(key) == null) {
                    return null;
                }
            }
        }
        if (line != null) {
            for (Map.Entry<String, String> entry : vars.entrySet()) {
                line = line.replace("{" + entry.getKey() + "}", entry.getValue() != null ? entry.getValue() : "");
            }
        }
        return line;
    }

    public static List<String> insertListInternalVariables(List<String> lines, Map<String, List<String>> vars) {
        List<String> result = new ArrayList<>();
        Set<Map.Entry<String, List<String>>> entries = vars.entrySet();

        for (String line : lines) {
            boolean keyLine = false;

            for (Map.Entry<String, List<String>> entry : entries) {
                String key = entry.getKey();
                List<String> value = entry.getValue();
                String placeholder = "{" + key + "}";

                if (line.contains(placeholder)) {
                    keyLine = true;
                    int j = result.size();

                    if (value == null || value.isEmpty()) {
                        while (j >= 2 && result.get(j - 1).startsWith(listMarker)) {
                            result.remove(j - 1);
                            j--;
                        }
                    } else {
                        while (j >= 2 && result.get(j - 1).startsWith(listMarker)) {
                            String raw = result.get(j - 1).substring(1);
                            result.set(j - 1, raw.isEmpty() ? " " : raw);
                            j--;
                        }
                        result.addAll(value);
                    }
                    break;
                }
            }

            if (!keyLine) {
                result.add(line);
            }
        }
        return result;
    }
}
