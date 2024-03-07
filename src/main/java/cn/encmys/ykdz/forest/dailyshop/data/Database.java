package cn.encmys.ykdz.forest.dailyshop.data;

import cn.encmys.ykdz.forest.dailyshop.DailyShop;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.io.*;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Database {
    private final DailyShop plugin = DailyShop.getInstance();
    private final File shopsData;
    private final Gson gson = new Gson();

    public Database(String path) {
        this.shopsData = new File(path + "/data/shop-data.json");
        createDatabaseFileIfNotExists(shopsData);
    }

    private void createDatabaseFileIfNotExists(File file) {
        if (!file.exists()) {
            file.getParentFile().mkdirs();
            plugin.saveResource("data/" + file.getName(), false);
        }
    }

    public void saveShopData(Map<String, List<String>> data) {
        try (FileWriter writer = new FileWriter(shopsData)) {
            gson.toJson(data, writer);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public Map<String, List<String>> loadShopData() {
        Map<String, List<String>> data = new HashMap<>();
        try (BufferedReader reader = new BufferedReader(new FileReader(shopsData))) {
            data = gson.fromJson(reader, new TypeToken<HashMap<String, List<String>>>(){}.getType());
        } catch (IOException e) {
            e.printStackTrace();
        }
        return data != null ? data : new HashMap<>();
    }
}
