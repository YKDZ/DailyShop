package cn.encmys.ykdz.forest.dailyshop.util;

import cn.encmys.ykdz.forest.dailyshop.DailyShop;
import me.clip.placeholderapi.PlaceholderAPI;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;
import javax.script.ScriptException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class TextUtils {
    private static final String listMarker = "|";

    public static List<String> decorateText(List<String> text, @Nullable Player player) {
        if (text == null) {
            return null;
        }

        List<String> result = new ArrayList<>();
        for (String line : text) {
            if (line != null) {
                result.add(decorateText(line, player));
            }
        }
        return result;
    }

    public static String decorateText(String text, @Nullable Player player) {
        if (text == null) {
            return null;
        }
        return DailyShop.ADVENTURE_MANAGER.componentToLegacy(DailyShop.ADVENTURE_MANAGER.getComponentFromMiniMessage(PlaceholderAPI.setPlaceholders(player, text)));
    }

    public static String decorateTextWithVar(String text, @Nullable Player player, @NotNull Map<String, String> vars) {
        text = parseInternalVariables(text, vars);

        if (text == null) {
            return null;
        }

        return DailyShop.ADVENTURE_MANAGER.componentToLegacy(DailyShop.ADVENTURE_MANAGER.getComponentFromMiniMessage(PlaceholderAPI.setPlaceholders(player, text)));
    }

    public static String decorateTextInMiniMessage(String text, @Nullable Player player, @NotNull Map<String, String> vars) {
        if (text == null) {
            return null;
        }
        return PlaceholderAPI.setPlaceholders(player, parseInternalVariables(text, vars));
    }

    public static List<String> decorateTextWithVar(List<String> text, @Nullable Player player, @NotNull Map<String, String> vars) {
        if (text == null) {
            return null;
        }

        List<String> result = new ArrayList<>();
        for (String line : text) {
            if (line != null) {
                result.add(decorateTextWithVar(line, player, vars));
            }
        }
        return result;
    }

    public static List<String> decorateTextWithListVar(List<String> text, @Nullable Player player, @NotNull Map<String, List<String>> listVars, @NotNull Map<String, String> normalVars ) {
        if (text == null) {
            return null;
        }

        text = insertListInternalVariables(text, listVars);

        return decorateTextWithVar(text, player, normalVars);
    }

    public static String parseInternalVariables(String line, Map<String, String> vars) {
        if (line != null && line.startsWith("?")) {
            for (String key : vars.keySet()) {
                if (line.contains("{" + key + "}") && (vars.get(key) == null || vars.get(key).isEmpty())) {
                    return null;
                }
            }
            line = line.substring(1);
        }
        if (line != null) {
            for (Map.Entry<String, String> entry : vars.entrySet()) {
                line = line.replace("{" + entry.getKey() + "}", entry.getValue() != null ? entry.getValue() : "");
            }
        }
        return line;
    }

    public static List<String> insertListInternalVariables(List<String> lines, Map<String, List<String>> vars) {
        if (lines == null) {
            return null;
        }

        List<String> result = new ArrayList<>();
        Set<Map.Entry<String, List<String>>> entries = vars.entrySet();

        for (String line : lines) {
            if (line == null) {
                continue;
            }

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

    public static double evaluateFormula(String formula, Map<String, String> vars) {
        ScriptEngineManager manager = new ScriptEngineManager();
        ScriptEngine engine = manager.getEngineByName("JavaScript");
        try {
            Object result = engine.eval(parseInternalVariables(formula, vars));
            if (result instanceof Double) {
                return (Double) result;
            } else if (result instanceof Integer) {
                return (Integer) result;
            } else if (result instanceof Long) {
                return (Long) result;
            } else {
                return -1d;
            }
        } catch (ScriptException e) {
            throw new IllegalArgumentException("Failed to evaluate formula " + formula + ": " + e.getMessage());
        }
    }
}
