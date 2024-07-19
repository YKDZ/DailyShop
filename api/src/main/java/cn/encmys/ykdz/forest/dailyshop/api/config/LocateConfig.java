package cn.encmys.ykdz.forest.dailyshop.api.config;

import cn.encmys.ykdz.forest.dailyshop.api.DailyShop;
import com.google.gson.*;
import com.google.gson.reflect.TypeToken;
import org.bukkit.Bukkit;

import java.io.*;
import java.lang.reflect.Type;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;

public class LocateConfig {
    private final static Map<String, String> config = new HashMap<>();

    public static void load() {
        String serverVersion = Bukkit.getServer().getClass().getPackage().getName().split("\\.")[3];
        try {
            // version_manifest.json 部分
            URL versionsURL = new URL("https://launchermeta.mojang.com/mc/game/version_manifest.json");
            JsonObject versions = fetchJson(versionsURL);
            String latestVersion = versions.get("latest.release").getAsString();
            JsonArray versionArray = versions.get("versions").getAsJsonArray();
            String metaURLString = "https://piston-meta.mojang.com/v1/packages/177e49d3233cb6eac42f0495c0a48e719870c2ae/" + latestVersion + ".json";

            for (JsonElement el : versionArray) {
                if (el.getAsJsonObject().get("id").getAsString().equals(serverVersion)) {
                    metaURLString = el.getAsJsonObject().get("url").getAsString();
                    break;
                }
            }
            // version meta 部分
            URL metaURL = new URL(metaURLString);
            JsonObject meta = fetchJson(metaURL);
            // assets 部分
            URL assetsURL = new URL(meta.get("assetIndex.url").getAsString());
            JsonObject assets = fetchJson(assetsURL);
            String target = "minecraft/lang/" + Config.language.toLowerCase();
            String hash = assets.get("objects." + target + "hash").getAsString();
            // 下载文件部分
            URL langURL = new URL("https://resources.download.minecraft.net/" + hash.substring(0, 2) + "/" + hash);
            String fileDestination = DailyShop.INSTANCE.getDataFolder() + "/lang/minecraft/" + Config.language.toLowerCase() + ".json";
            downloadFile(langURL, fileDestination);
            // 从文件加载 Map 部分
            Gson gson = new Gson();
            Type type = new TypeToken<Map<String, String>>() {
            }.getType();
            try (FileReader reader = new FileReader(fileDestination)) {
                config.clear();
                config.putAll(gson.fromJson(reader, type));
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public static Map<String, String> getConfig() {
        return config;
    }

    public static String get(String key) {
        return config.get(key);
    }

    private static JsonObject fetchJson(URL url) throws IOException {
        HttpURLConnection connection = (HttpURLConnection) url.openConnection();
        connection.setRequestMethod("GET");
        connection.setRequestProperty("Accept", "application/json");

        int responseCode = connection.getResponseCode();
        if (responseCode != 200) {
            throw new RuntimeException("Failed : HTTP error code : " + responseCode);
        }

        BufferedReader br = new BufferedReader(new InputStreamReader((connection.getInputStream())));
        StringBuilder response = new StringBuilder();
        String output;
        while ((output = br.readLine()) != null) {
            response.append(output);
        }
        connection.disconnect();

        return JsonParser.parseString(response.toString()).getAsJsonObject();
    }

    private static void downloadFile(URL url, String destinationFile) throws IOException {
        HttpURLConnection connection = (HttpURLConnection) url.openConnection();
        connection.setRequestMethod("GET");
        connection.setConnectTimeout(5000); // 5 秒超时
        connection.setReadTimeout(5000); // 5 秒超时

        int responseCode = connection.getResponseCode();
        if (responseCode != HttpURLConnection.HTTP_OK) {
            throw new IOException("Failed to download file: " + responseCode);
        }

        // 若目录不存在则创建
        File file = new File(destinationFile);
        File parentDir = file.getParentFile();
        if (parentDir != null && !parentDir.exists()) {
            if (!parentDir.mkdirs()) {
                throw new IOException("Failed to create directory: " + parentDir.getAbsolutePath());
            }
        }

        try (InputStream inputStream = connection.getInputStream();
             BufferedInputStream bufferedInputStream = new BufferedInputStream(inputStream);
             FileOutputStream fileOutputStream = new FileOutputStream(file)) {

            byte[] buffer = new byte[1024];
            int bytesRead;
            while ((bytesRead = bufferedInputStream.read(buffer, 0, 1024)) != -1) {
                fileOutputStream.write(buffer, 0, bytesRead);
            }
        } finally {
            connection.disconnect();
        }
    }
}
