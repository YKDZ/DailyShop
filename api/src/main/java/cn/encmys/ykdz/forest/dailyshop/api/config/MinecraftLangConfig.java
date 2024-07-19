package cn.encmys.ykdz.forest.dailyshop.api.config;

import cn.encmys.ykdz.forest.dailyshop.api.DailyShop;
import cn.encmys.ykdz.forest.dailyshop.api.utils.LogUtils;
import com.google.gson.*;
import com.google.gson.reflect.TypeToken;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.jetbrains.annotations.NotNull;

import java.io.*;
import java.lang.reflect.Type;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

/**
 * Use to translate the display name of the vanilla item in gui to specific language.
 * Inspired by <a href="https://github.com/ManyouTeam/UltimateShop">UltimateShop</a>.
 */
public class MinecraftLangConfig {
    /**
     * Format like 1.21 or 1.16.5
     */
    private final static String serverVersion = Bukkit.getVersion().split("-")[0];
    private final static String fileDestination = DailyShop.INSTANCE.getDataFolder() + "/lang/minecraft/" + Config.language_minecraftLang.toLowerCase() + ".json";
    private final static Map<String, String> config = new HashMap<>();

    public static void load() {
        try {
            File locateFile = loadLangFile();
            loadMapFromLangFile(locateFile);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static Map<String, String> getConfig() {
        return Collections.unmodifiableMap(config);
    }

    public static String get(String key) {
        return config.get(key);
    }

    private static File loadLangFile() throws IOException {
        File locateFile = new File(fileDestination);
        if (!locateFile.exists()) {
            if (locateFile.mkdirs()) {
                return useFallbackLang();
            }
            if (downloadLangFileFromMcAssets() || downloadLangFileFromOfficial()) {
                return locateFile;
            } else {
                return useFallbackLang();
            }
        }
        return locateFile;
    }

    private static boolean downloadLangFileFromMcAssets() {
        LogUtils.info("Try to download lang file " + Config.language_minecraftLang + ".json from mcasset.cloud.");
        try {
            downloadFile(new URL("https://raw.githubusercontent.com/InventivetalentDev/minecraft-assets/" + serverVersion + "/assets/minecraft/lang/" + Config.language_minecraftLang + ".json"));
            return true;
        } catch (IOException e) {
            return false;
        }
    }

    private static boolean downloadLangFileFromOfficial() {
        LogUtils.info("Try to download lang file " + Config.language_minecraftLang + ".json from official api.");
        try {
            JsonObject versions = fetchJson(new URL("https://launchermeta.mojang.com/mc/game/version_manifest.json"));
            if (versions == null) return false;

            String latestVersion = getLatestVersion(versions);
            if (latestVersion == null) return false;

            String metaURLString = getMetaURLString(versions, latestVersion);
            JsonObject meta = fetchJson(new URL(metaURLString));
            if (meta == null) return false;

            JsonObject assetIndex = meta.getAsJsonObject("assetIndex");
            if (assetIndex == null || !assetIndex.has("url")) return false;

            JsonObject assets = fetchJson(new URL(assetIndex.get("url").getAsString()));
            if (assets == null) return false;

            String target = "minecraft/lang/" + Config.language_minecraftLang.toLowerCase() + ".json";

            JsonObject targetAsset = getTargetAsset(assets, target);
            if (targetAsset == null) return false;

            String hash = targetAsset.get("hash").getAsString();
            downloadFile(new URL("https://resources.download.minecraft.net/" + hash.substring(0, 2) + "/" + hash));
            return true;
        } catch (IOException e) {
            return false;
        }
    }

    @NotNull
    public static String translate(@NotNull Material material) {
        return config.getOrDefault("item.minecraft." + material.name().toLowerCase(), config.getOrDefault("block.minecraft." + material.name().toLowerCase(), "<red>Name not found!>"));
    }

    private static File useFallbackLang() {
        DailyShop.INSTANCE.saveResource("lang/minecraft/en_us.json", false);
        return new File(DailyShop.INSTANCE.getDataFolder() + "/lang/minecraft/en_us.json");
    }

    private static String getLatestVersion(JsonObject versions) {
        JsonObject latest = versions.getAsJsonObject("latest");
        if (latest == null || !latest.has("release")) return null;
        return latest.get("release").getAsString();
    }

    @NotNull
    private static String getMetaURLString(@NotNull JsonObject versions, @NotNull String latestVersion) {
        JsonArray versionArray = versions.getAsJsonArray("versions");
        if (versionArray == null) return "";
        String metaURLString = "https://piston-meta.mojang.com/v1/packages/177e49d3233cb6eac42f0495c0a48e719870c2ae/" + latestVersion + ".json";
        for (JsonElement el : versionArray) {
            JsonObject versionObj = el.getAsJsonObject();
            if (versionObj != null && versionObj.has("id") && versionObj.get("id").getAsString().equals(serverVersion)) {
                if (versionObj.has("url")) {
                    metaURLString = versionObj.get("url").getAsString();
                    break;
                }
            }
        }
        return metaURLString;
    }

    private static JsonObject getTargetAsset(JsonObject assets, String target) {
        JsonObject objects = assets.getAsJsonObject("objects");
        if (objects == null || !objects.has(target)) return null;
        return objects.getAsJsonObject(target);
    }

    private static void loadMapFromLangFile(@NotNull File file) throws IOException {
        Gson gson = new Gson();
        Type type = new TypeToken<Map<String, String>>() {
        }.getType();
        try (FileReader reader = new FileReader(file)) {
            config.clear();
            Map<String, String> loadedConfig = gson.fromJson(reader, type);
            if (loadedConfig != null) {
                config.putAll(loadedConfig);
            }
        }
    }

    private static JsonObject fetchJson(URL url) throws IOException {
        HttpURLConnection connection = (HttpURLConnection) url.openConnection();
        connection.setRequestMethod("GET");
        connection.setRequestProperty("Accept", "application/json");

        int responseCode = connection.getResponseCode();
        if (responseCode != HttpURLConnection.HTTP_OK) {
            throw new RuntimeException("Failed : HTTP error code : " + responseCode);
        }

        try (BufferedReader br = new BufferedReader(new InputStreamReader(connection.getInputStream()))) {
            StringBuilder response = new StringBuilder();
            String output;
            while ((output = br.readLine()) != null) {
                response.append(output);
            }
            return JsonParser.parseString(response.toString()).getAsJsonObject();
        } finally {
            connection.disconnect();
        }
    }

    private static void downloadFile(URL url) throws IOException {
        HttpURLConnection connection = (HttpURLConnection) url.openConnection();
        connection.setRequestMethod("GET");
        connection.setConnectTimeout(5000); // 5 秒超时
        connection.setReadTimeout(5000); // 5 秒超时

        int responseCode = connection.getResponseCode();
        if (responseCode != HttpURLConnection.HTTP_OK) {
            throw new IOException("Failed to download file: " + responseCode);
        }

        // 若目录不存在则创建
        File file = new File(fileDestination);
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
            while ((bytesRead = bufferedInputStream.read(buffer)) != -1) {
                fileOutputStream.write(buffer, 0, bytesRead);
            }
        } finally {
            connection.disconnect();
        }
    }
}
