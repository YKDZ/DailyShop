package cn.encmys.ykdz.forest.dailyshop.rarity.factory;

import cn.encmys.ykdz.forest.dailyshop.api.rarity.factory.RarityFactory;
import cn.encmys.ykdz.forest.dailyshop.config.RarityConfig;
import cn.encmys.ykdz.forest.dailyshop.rarity.RarityImpl;
import org.bukkit.configuration.file.YamlConfiguration;

import java.util.HashMap;

public class RarityFactoryImpl implements RarityFactory {
    private static final HashMap<String, RarityImpl> rarities = new HashMap<>();

    public RarityFactoryImpl() {
        YamlConfiguration config = RarityConfig.getConfig();
        for (String id : RarityConfig.getAllId()) {
            buildRarity(
                    id,
                    config.getString("rarities." + id + ".name"),
                    config.getInt("rarities." + id + ".weight")
            );
        }
    }

    @Override
    public void buildRarity(String id, String name, int weight) {
        rarities.put(id, new RarityImpl(id, name, weight));
    }

    @Override
    public RarityImpl getRarity(String id) {
        return rarities.get(id);
    }
}
