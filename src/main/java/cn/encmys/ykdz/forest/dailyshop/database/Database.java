package cn.encmys.ykdz.forest.dailyshop.database;

import cn.encmys.ykdz.forest.dailyshop.DailyShop;
import cn.encmys.ykdz.forest.dailyshop.shop.Shop;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;

import java.io.*;
import java.util.HashMap;
import java.util.Map;

public class Database {
    private final DailyShop plugin = DailyShop.getInstance();
    private final File shopsData;
    private final File transitionLog;
    private final Gson gson = new GsonBuilder().excludeFieldsWithoutExposeAnnotation().create();

    public Database(String path) {
        shopsData = new File(path + "/data/shop-data.json");
        transitionLog = new File(path + "/data/transition-log.json");
        createDatabaseFileIfNotExists(shopsData, transitionLog);
    }

    private void createDatabaseFileIfNotExists(File... files) {
        for (File file : files) {
            if (!file.exists()) {
                file.getParentFile().mkdirs();
                plugin.saveResource("data/" + file.getName(), false);
            }
        }
    }

    public void saveShopData(Map<String, Shop> data) {
        try (FileWriter writer = new FileWriter(shopsData)) {
            gson.toJson(data, writer);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public Map<String, Shop> loadShopData() {
        Map<String, Shop> data = new HashMap<>();
        try (BufferedReader reader = new BufferedReader(new FileReader(shopsData))) {
            data = gson.fromJson(reader, new TypeToken<HashMap<String, Shop>>() {
            }.getType());
        } catch (IOException e) {
            e.printStackTrace();
        }
        return data != null ? data : new HashMap<>();
    }
}
