package cn.encmys.ykdz.forest.dailyshop.config;

import cn.encmys.ykdz.forest.dailyshop.DailyShop;
import cn.encmys.ykdz.forest.dailyshop.util.ItemUtils;
import org.bukkit.Material;
import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.inventory.ItemStack;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Set;

public class ShopConfig {
    private static final DailyShop plugin = DailyShop.getInstance();
    private static final HashMap<String, YamlConfiguration> configs = new HashMap<>();

    public static void load() {
        File directory = new File(plugin.getDataFolder() + "/shop");

        if (!directory.exists() || !directory.isDirectory()) {
            throw new IllegalArgumentException("ShopConfig config path not find.");
        }

        File[] files = directory.listFiles();
        if (files == null) {
            return;
        }

        for (File file : files) {
            if (file.isFile() && file.getName().endsWith(".yml")) {
                YamlConfiguration config = new YamlConfiguration();
                try {
                    config.load(file);
                    configs.put(file.getName().replace(".yml", ""), config);
                } catch (IOException | InvalidConfigurationException error) {
                    error.printStackTrace();
                }
            }
        }
    }

    public static YamlConfiguration getConfig(String id) {
        return configs.get(id);
    }

    public static List<String> getAllId() {
        return new ArrayList<>(configs.keySet());
    }

    public static String getTitle(String id) {
        return getConfig(id).getString("shop-gui.title");
    }

    public static String[] getLayout(String id) {
        return getConfig(id).getStringList("shop-gui.layout").toArray(new String[0]);
    }

    public static Set<String> getIcons(String id) {
        return getConfig(id).getConfigurationSection("shop-gui.icons").getKeys(false);
    }

    public static ItemStack getIcon(String shopId, char iconId) {
        Material material = Material.matchMaterial(getConfig(shopId).getString("shop-gui.icons." + iconId + ".material"));
        String displayName = getConfig(shopId).getString("shop-gui.icons." + iconId + ". name");
        List<String> lore = getConfig(shopId).getStringList("shop-gui.icons." + iconId + ".lore");

        ItemStack icon = new ItemStack(material, 1);
        ItemUtils.displayName(icon, displayName);
        ItemUtils.lore(icon, lore);

        return icon;
    }
}
