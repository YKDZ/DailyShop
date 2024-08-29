package cn.encmys.ykdz.forest.dailyshop.api.utils;

import cn.encmys.ykdz.forest.dailyshop.api.DailyShop;
import cn.encmys.ykdz.forest.dailyshop.api.adventure.AdventureManager;
import me.clip.placeholderapi.PlaceholderAPI;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.mozilla.javascript.Context;
import org.mozilla.javascript.EvaluatorException;
import org.mozilla.javascript.Scriptable;

import java.math.BigDecimal;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class TextUtils {
    private static final String listMarker = "|";
    private static final String optionalMarker = "?";
    private static final String singleMarker = "~";

    public static List<String> decorateText(@NotNull List<String> text, Player player, @Nullable Map<String, String> normalVars, @Nullable Map<String, List<String>> listVars) {
        AdventureManager adventureManager = DailyShop.ADVENTURE_MANAGER;
        return adventureManager.componentToLegacy(
                adventureManager.getComponentFromMiniMessage(
                        decorateTextKeepMiniMessage(text, player, normalVars, listVars)
                )
        );
    }

    public static String decorateText(@Nullable String text, Player player, @Nullable Map<String, String> normalVars) {
        if (text == null) {
            return null;
        }
        String internalResult = parseInternalVar(text, normalVars);
        if (internalResult == null) {
            return null;
        }
        AdventureManager adventureManager = DailyShop.ADVENTURE_MANAGER;
        return adventureManager.componentToLegacy(
                adventureManager.getComponentFromMiniMessage(
                        decorateTextKeepMiniMessage(text, player, normalVars)
                )
        );
    }

    public static List<String> decorateTextKeepMiniMessage(@NotNull List<String> text, Player player, @Nullable Map<String, String> normalVars, @Nullable Map<String, List<String>> listVars) {
        return parsePlaceholder(parseInternalVar(parseInternalListVar(text, listVars), normalVars), player);
    }

    public static String decorateTextKeepMiniMessage(@Nullable String text, Player player, @Nullable Map<String, String> normalVars) {
        if (text == null) {
            return null;
        }
        String internalResult = parseInternalVar(text, normalVars);
        if (internalResult == null) {
            return null;
        }
        return parsePlaceholder(internalResult, player);
    }

    public static List<String> parsePlaceholder(@NotNull List<String> text, Player player) {
        return text.stream()
                .map((line) -> parsePlaceholder(line, player))
                .toList();
    }

    public static String parsePlaceholder(@NotNull String text, Player player) {
        return PlaceholderAPI.setPlaceholders(player, text);
    }

    public static List<String> parseInternalVar(@NotNull List<String> text, @Nullable Map<String, String> vars) {
        List<String> result = new ArrayList<>();
        for (String line : text) {
            if (line != null) {
                String parsed = parseInternalVar(line, vars);
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

    @Nullable
    public static String parseInternalVar(@NotNull String line, @Nullable Map<String, String> vars) {
        if (vars == null) {
            return line;
        }
        // 正则表达式匹配所有 {} 包裹的变量
        Pattern pattern = Pattern.compile("\\{(.*?)\\}");
        Matcher matcher = pattern.matcher(line);
        // StringBuilder 用于高效地构建最终字符串
        StringBuilder result = new StringBuilder();
        int lastMatchEnd = 0;
        boolean hasInvalidValue = false;
        // 遍历所有匹配的变量
        while (matcher.find()) {
            String key = matcher.group(1);
            String value = vars.get(key);
            // 检查变量的值是否为 "-1"、null 或空字符串
            if (value == null || value.equals("-1") || value.isBlank()) {
                hasInvalidValue = true;
            }
            // 将前面的内容添加到 result
            result.append(line, lastMatchEnd, matcher.start());
            // 替换变量
            result.append(value != null ? value : "");
            // 更新 lastMatchEnd 为当前匹配结束位置
            lastMatchEnd = matcher.end();
        }
        // 将最后一部分内容添加到 result
        result.append(line.substring(lastMatchEnd));
        // 如果发现有无效值且行以 optionalMarker 开头，则返回 null
        if (hasInvalidValue && line.startsWith(optionalMarker)) {
            return null;
        }
        // 如果行以 optionalMarker 开头，则去除它
        if (line.startsWith(optionalMarker)) {
            return result.substring(optionalMarker.length());
        }
        return result.toString();
    }

    public static List<String> parseInternalListVar(@NotNull List<String> lines, @Nullable Map<String, List<String>> vars) {
        if (vars == null) {
            return lines;
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

    public static double evaluateNumberFormula(String formula, Map<String, String> vars, @Nullable Player player) {
        formula = parseInternalVar(optionalMarker + formula, vars);
        if (formula == null || formula.isEmpty()) {
            return -1d;
        }
        formula = parsePlaceholder(formula, player);

        try (Context ctx = Context.enter()) {
            Scriptable scope = ctx.initStandardObjects();
            try {
                Object result = ctx.evaluateString(scope, formula, "formula", 1, null);
                if (result instanceof Number) {
                    BigDecimal bigDecimalResult = new BigDecimal(result.toString());
                    if (Objects.equals(bigDecimalResult, BigDecimal.ZERO)) {
                        return -1d;
                    }
                    return bigDecimalResult.doubleValue();
                } else {
                    LogUtils.warn("Result of formula " + formula + " is not a number");
                }
            } catch (EvaluatorException e) {
                LogUtils.warn("Evaluation error for formula: " + formula + ". " + e.getMessage());
            } catch (Exception e) {
                LogUtils.warn("Unexpected error for formula: " + formula + ". " + e.getMessage());
            }
        }
        return -1d;
    }

    public static boolean evaluateBooleanFormula(String formula, Map<String, String> vars, @Nullable Player player) {
        formula = parseInternalVar(optionalMarker + formula, vars);
        if (formula == null || formula.isEmpty()) {
            return false;
        }
        formula = parsePlaceholder(formula, player);

        try (Context ctx = Context.enter()) {
            Scriptable scope = ctx.initStandardObjects();
            try {
                Object result = ctx.evaluateString(scope, formula, "formula", 1, null);
                if (result instanceof Boolean) {
                    return (Boolean) result;
                } else {
                    LogUtils.warn("Result of formula " + formula + " is not a boolean");
                }
            } catch (EvaluatorException e) {
                LogUtils.warn("Evaluation error for formula: " + formula + ". " + e.getMessage());
            } catch (Exception e) {
                LogUtils.warn("Unexpected error for formula: " + formula + ". " + e.getMessage());
            }
        }
        return false;
    }

    public List<String> legacyToMiniMessage(@NotNull List<String> text) {
        return text.stream()
                .map(this::legacyToMiniMessage)
                .toList();
    }

    public String legacyToMiniMessage(@NotNull String text) {
        return DailyShop.ADVENTURE_MANAGER.legacyToMiniMessage(text);
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
            double value;
            try {
                value = Double.parseDouble(valueStr);
            } catch (NumberFormatException e) {
                throw new IllegalArgumentException("Invalid number: " + valueStr);
            }

            ticks += (long) (value * timeUnits.get(unit));
        }

        return ticks;
    }
}
