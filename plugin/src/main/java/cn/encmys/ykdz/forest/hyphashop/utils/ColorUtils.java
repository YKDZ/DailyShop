package cn.encmys.ykdz.forest.hyphashop.utils;

import org.bukkit.Color;

public class ColorUtils {
    public static String getHex(Color color) {
        return String.format("#%02x%02x%02x", color.getRed(), color.getGreen(), color.getBlue());
    }

    public static Color getFromHex(String hex) {
        int color = Integer.parseInt(hex.replace("#", ""), 16);
        int r = (color >> 16) & 0xFF;
        int g = (color >> 8) & 0xFF;
        int b = color & 0xFF;
        return Color.fromRGB(r, g, b);
    }
}
