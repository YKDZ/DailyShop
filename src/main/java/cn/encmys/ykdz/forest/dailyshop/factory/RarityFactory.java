package cn.encmys.ykdz.forest.dailyshop.factory;

import cn.encmys.ykdz.forest.dailyshop.config.RaritiesConfig;
import cn.encmys.ykdz.forest.dailyshop.rarity.Rarity;
import org.bukkit.configuration.file.YamlConfiguration;

import java.util.HashMap;

public class RarityFactory {
    private static HashMap<String, Rarity> rarities;

    public RarityFactory() {
        YamlConfiguration config = RaritiesConfig.getConfig();
        for(String id : RaritiesConfig.getAllId()) {
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
