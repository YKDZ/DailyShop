package cn.encmys.ykdz.forest.dailyshop.api.utils;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class EnumUtils {
    @Nullable
    public static <T extends Enum<T>> T getEnumFromName(Class<T> type, @Nullable String name) {
        try {
            if (name != null) {
                return Enum.valueOf(type, name.toUpperCase());
            }
        } catch (IllegalArgumentException e) {
            return null;
        }
        return null;
    }

    @NotNull
    public static String toConfigName(Enum<?> enums) {
        if (enums == null) {
            return "";
        } else {
            return enums.name().toLowerCase().replace("_", "-");
        }
    }

    @NotNull
    public static String toConfigName(Class<? extends Enum<?>> type) {
        if (type == null) {
            return "";
        } else {
            return type.getSimpleName().replaceAll("([a-z])([A-Z])", "$1-$2").toLowerCase();
        }
    }
}
