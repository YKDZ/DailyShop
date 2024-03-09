package cn.encmys.ykdz.forest.dailyshop.util;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class TextUtils {
    public static String catLines(List<String> messages) {
        if(messages.isEmpty()) {
            return "";
        }

        StringBuffer buffer = new StringBuffer();
        for(String message : messages) {
            buffer.append(message)
                    .append("\n");
        }
        buffer.deleteCharAt(buffer.length() - 1);
        return buffer.toString();
    }

    public static List<String> parseVariables(List<String> lines, Map<String, String> map) {
        List<String> result = new ArrayList<>();
        for(String line : lines) {
            result.add(parseVariables(line, map));
        }
        return result;
    }

    public static String parseVariables(String line, Map<String, String> map) {
        for(Map.Entry<String, String> entry : map.entrySet()) {
            String key = entry.getKey();
            String value = entry.getValue();
            if (value != null) {
                line = line.replace("{" + key + "}", value);
            }
        }
        return line;
    }
}
