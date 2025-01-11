package cn.encmys.ykdz.forest.dailyshop.api.utils;

import org.jetbrains.annotations.NotNull;

import java.util.Random;

public class NumberUtils {
    private static final Random RANDOM = new Random();

    public static int intInRange(int from, int to) {
        return RANDOM.nextInt(to - from + 1) + from;
    }

    public static boolean isInt(@NotNull String config) {
        try {
            Integer.parseInt(config);
            return true;
        } catch (Exception ignored) {
            return false;
        }
    }
}
