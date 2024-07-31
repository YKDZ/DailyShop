package cn.encmys.ykdz.forest.dailyshop.api.utils;

import org.jetbrains.annotations.Nullable;

public class EnumUtils {
    @Nullable
    public static <T extends Enum<T>> T getEnumFromName(Class<T> type, @Nullable String name) {
        try {
            if (name != null) {
                return Enum.valueOf(type, name);
            }
        } catch (IllegalArgumentException e) {
            return null;
        }
        return null;
    }
}
