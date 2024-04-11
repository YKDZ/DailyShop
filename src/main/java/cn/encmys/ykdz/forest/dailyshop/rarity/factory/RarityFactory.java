package cn.encmys.ykdz.forest.dailyshop.rarity.factory;

import cn.encmys.ykdz.forest.dailyshop.config.RarityConfig;
import cn.encmys.ykdz.forest.dailyshop.rarity.Rarity;
import org.bukkit.configuration.file.YamlConfiguration;

import java.util.HashMap;

public class RarityFactory {
    private static final HashMap<String, Rarity> rarities = new HashMap<>();

    public RarityFactory() {
        YamlConfiguration config = RarityConfig.getConfig();
        for (String id : RarityConfig.getAllId()) {
            buildRarity(
                    id,
                    config.getString("rarities." + id + ".name"),
                    config.getInt("rarities." + id + ".weight")
            );
        }
    }

    public void buildRarity(String id, String name, int weight) {
        rarities.put(id, new Rarity(id, name, weight));
    }

    public Rarity getRarity(String id) {
        return rarities.get(id);
    }
}
