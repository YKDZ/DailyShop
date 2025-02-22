package cn.encmys.ykdz.forest.hyphashop.rarity.factory;

import cn.encmys.ykdz.forest.hyphashop.api.rarity.Rarity;
import cn.encmys.ykdz.forest.hyphashop.api.rarity.factory.RarityFactory;
import cn.encmys.ykdz.forest.hyphashop.config.RarityConfig;
import cn.encmys.ykdz.forest.hyphashop.rarity.RarityImpl;
import org.bukkit.configuration.file.YamlConfiguration;
import org.jetbrains.annotations.NotNull;

import java.util.HashMap;

public class RarityFactoryImpl implements RarityFactory {
    @NotNull
    private static final HashMap<String, Rarity> rarities = new HashMap<>();

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
    public Rarity getRarity(String id) {
        return rarities.get(id);
    }
}
