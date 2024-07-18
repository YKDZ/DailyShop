package cn.encmys.ykdz.forest.dailyshop.api.utils;

import cn.encmys.ykdz.forest.dailyshop.api.DailyShop;
import cn.encmys.ykdz.forest.dailyshop.api.hook.PlaceholderAPIHook;
import me.clip.placeholderapi.PlaceholderAPI;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;
import javax.script.ScriptException;
import java.util.*;

public class TextUtils {
    private static final String listMarker = "|";
    private static final String optionalMarker = "?";
    private static final String singleMarker = "~";

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
        if (PlaceholderAPIHook.isHooked()) {
            text = PlaceholderAPI.setPlaceholders(player, text);
        }
        return DailyShop.ADVENTURE_MANAGER.componentToLegacy(DailyShop.ADVENTURE_MANAGER.getComponentFromMiniMessage(text));
    }

    public static String parseVar(String text, @Nullable Player player, @NotNull Map<String, String> vars) {
        text = insertVar(text, vars);
        return decorateText(text, player);
    }

    public static String decorateTextInMiniMessage(String text, @Nullable Player player, @NotNull Map<String, String> vars) {
        if (text == null) {
            return null;
        }
        return PlaceholderAPIHook.isHooked() ? PlaceholderAPI.setPlaceholders(player, insertVar(text, vars)) : insertVar(text, vars);
    }

    public static List<String> parseVar(@Nullable List<String> text, @Nullable Player player, @NotNull Map<String, String> vars) {
        if (text == null) {
            return null;
        }

        List<String> result = new ArrayList<>();
        for (String line : text) {
            if (line != null) {
                String parsed = parseVar(line, player, vars);
                if (parsed != null) {
                    result.add(parsed);
                }
            }
        }

        // 以 singleMarker 开头的行会顺序消除其下方的若干个空行或以 singleMarker 开头的行，直到遇见不满足条件的行
        // 之后将自己变为空行
        for (int i = 0; i < result.size(); i++) {
            String line = result.get(i);
            if (line != null && line.startsWith(singleMarker)) {
                int j = i + 1;
                while (j < result.size() && (result.get(j).isBlank() || result.get(j).startsWith(singleMarker))) {
                    result.remove(j);
                }
                result.set(i, "");
            }
        }

        return result;
    }

    public static List<String> parseVar(@Nullable List<String> text, @Nullable Player player, @NotNull Map<String, List<String>> listVars, @NotNull Map<String, String> normalVars ) {
        List<String> result = null;
        if (text != null) {
            result = insertListVar(text, listVars);
            result = parseVar(result, player, normalVars);
        }
        return result;
    }

    public static List<String> insertListVar(List<String> lines, Map<String, List<String>> vars) {
        List<String> res = null;
        if (lines != null) {
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
            res = result;
        }
        return res;
    }

    public static String insertVar(String line, Map<String, String> vars) {
        if (line != null) {
            // 以 optionalMarker 的行是一个可选行
            // 如果该行包含某变量，但此变量的值是："-1"、null、空，则抹除整行
            if (line.startsWith(optionalMarker)) {
                for (String key : vars.keySet()) {
                    if (line.contains("{" + key + "}") && (vars.get(key).equals("-1") || vars.get(key) == null || vars.get(key).isBlank())) {
                        return null;
                    }
                }
                line = line.substring(optionalMarker.length());
            }
            for (Map.Entry<String, String> entry : vars.entrySet()) {
                line = line.replace("{" + entry.getKey() + "}", entry.getValue() != null ? entry.getValue() : "");
            }
        }
        return line;
    }

    public static double evaluateFormula(String formula, Map<String, String> vars) {
        ScriptEngineManager manager = new ScriptEngineManager();
        ScriptEngine engine = manager.getEngineByName("JavaScript");
        try {
            Object result = engine.eval(insertVar(formula, vars));
            if (result instanceof Double) {
                return (Double) result;
            } else if (result instanceof Integer) {
                return (Integer) result;
            } else if (result instanceof Long) {
                return (Long) result;
            } else {
                // 禁用此价格
                return -1d;
            }
        } catch (ScriptException e) {
            throw new IllegalArgumentException("Failed to evaluate formula " + formula + ": " + e.getMessage());
        }
    }

    public static long parseTimeToTicks(@Nullable String time) {
        if (time == null || time.isEmpty()) {
            return 0L;
        }

        Map<Character, Integer> timeUnits = new HashMap<>();
        // 1s = 20 ticks
        timeUnits.put('s', 20); // 秒
        timeUnits.put('m', 60 * 20); // 分钟
        timeUnits.put('h', 3600 * 20); // 小时
        timeUnits.put('d', 86400 * 20); // 天

        long ticks = 0;

        String[] parts = time.split("\\s+");
        for (String part : parts) {
            if (part.length() < 2) {
                throw new IllegalArgumentException("Invalid time format: " + part);
            }

            char unit = part.charAt(part.length() - 1);
            if (!timeUnits.containsKey(unit)) {
                throw new IllegalArgumentException("Unknown time unit: " + unit);
            }

            String valueStr = part.substring(0, part.length() - 1);
            long value;
            try {
                value = Long.parseLong(valueStr);
            } catch (NumberFormatException e) {
                throw new IllegalArgumentException("Invalid number: " + valueStr);
            }

            ticks += value * timeUnits.get(unit);
        }

        return ticks;
    }
}
