package cn.encmys.ykdz.forest.dailyshop.util;

import cn.encmys.ykdz.forest.dailyshop.DailyShop;
import cn.encmys.ykdz.forest.dailyshop.adventure.AdventureManager;
import me.clip.placeholderapi.PlaceholderAPI;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

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
        for (Map.Entry<String, String> entry : vars.entrySet()) {
            String key = entry.getKey();
            String value = entry.getValue();
            if (value != null && line != null) {
                line = line.replace("{" + key + "}", value);
            }
        }
        return line;
    }

    public static List<String> insertListInternalVariables(List<String> lines, Map<String, List<String>> vars) {
        List<String> result = new ArrayList<>();
        for (String line : lines) {
            boolean keyLine = false;
            for (Map.Entry<String, List<String>> entry : vars.entrySet()) {
                String key = entry.getKey();
                List<String> value = entry.getValue();
                if (line.contains("{" + key + "}")) {
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
                }
            }
            if (!keyLine) {
                result.add(line);
            }
        }
        return result;
    }
}
