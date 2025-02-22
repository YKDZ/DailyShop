package cn.encmys.ykdz.forest.hyphashop.utils;

import cn.encmys.ykdz.forest.hyphascript.context.Context;
import cn.encmys.ykdz.forest.hyphascript.script.EvaluateResult;
import cn.encmys.ykdz.forest.hyphascript.script.Script;
import cn.encmys.ykdz.forest.hyphascript.value.Value;
import org.jetbrains.annotations.NotNull;

import java.util.Map;

public class ScriptUtils {
    public static @NotNull Context linkContext(@NotNull Context... contexts) {
        if (contexts == null || contexts.length < 2) {
            throw new IllegalArgumentException("At least two Context is required.");
        }

        for (int i = 1; i < contexts.length; i++) {
            contexts[i].setParent(contexts[i - 1]);
        }

        return contexts[contexts.length - 1];
    }

    public static @NotNull Context extractContext(@NotNull String scriptStr) {
        Script script = new Script(scriptStr, new Context());
        EvaluateResult result = script.evaluate();

        if (result.type() != EvaluateResult.Type.SUCCESS) {
            LogUtils.warn("Error when extracting context from script. Use global context as fallback value.");
            LogUtils.warn(result.toString());
            return Context.GLOBAL_CONTEXT;
        }

        return script.getContext();
    }

    public static @NotNull Context buildContext(@NotNull Context parent, @NotNull Map<String, Object> vars) {
        Context context = new Context(Context.Type.NORMAL, parent);
        for (Map.Entry<String, Object> entry : vars.entrySet()) {
            String name = entry.getKey();
            context.declareReference(name, new Value(entry.getValue()), true, true);
        }
        return context;
    }

    public static boolean evaluateBoolean(@NotNull Context context, @NotNull String scriptStr) {
        Script script = new Script(scriptStr, context);
        EvaluateResult result = script.evaluate();

        if (result.type() != EvaluateResult.Type.SUCCESS) {
            LogUtils.warn("Error when evaluating script. Use false as fallback value.");
            LogUtils.warn(result.toString());
            return false;
        }

        if (!result.value().isType(Value.Type.BOOLEAN)) {
            LogUtils.warn("Result of script: " + scriptStr + " is not boolean. Use false as fallback value.");
            return false;
        }

        return result.value().getAsBoolean();
    }

    public static double evaluateDouble(@NotNull Context context, @NotNull String scriptStr) {
        Script script = new Script(scriptStr, context);
        EvaluateResult result = script.evaluate();

        if (result.type() != EvaluateResult.Type.SUCCESS) {
            LogUtils.warn("Error when evaluating script. Use -1 as fallback value.");
            LogUtils.warn(result.toString());
            return -1d;
        }

        if (!result.value().isType(Value.Type.BIG_DECIMAL)) {
            LogUtils.warn("Result of script: " + scriptStr + " is not boolean. Use -1 as fallback value.");
            return -1d;
        }

        return result.value().getAsBigDecimal().doubleValue();
    }

    public static int evaluateInt(@NotNull Context context, @NotNull String scriptStr) {
        Script script = new Script(scriptStr, context);
        EvaluateResult result = script.evaluate();

        if (result.type() != EvaluateResult.Type.SUCCESS) {
            LogUtils.warn("Error when evaluating script. Use -1 as fallback value.");
            LogUtils.warn(result.toString());
            return -1;
        }

        if (!result.value().isType(Value.Type.BIG_DECIMAL)) {
            LogUtils.warn("Result of script: " + scriptStr + " is not boolean. Use -1 as fallback value.");
            return -1;
        }

        return result.value().getAsBigDecimal().intValue();
    }

    public static @NotNull String evaluateString(@NotNull Context context, @NotNull String scriptStr) {
        Script script = new Script(scriptStr, context);
        EvaluateResult result = script.evaluate();

        if (result.type() != EvaluateResult.Type.SUCCESS) {
            LogUtils.warn("Error when evaluating script. Use empty string as fallback value.");
            LogUtils.warn(result.toString());
            return "";
        }

        if (!result.value().isType(Value.Type.STRING)) {
            LogUtils.warn("Result of script: " + scriptStr + " is not boolean. Use empty string as fallback value.");
            return "";
        }

        return result.value().getAsString();
    }
}
