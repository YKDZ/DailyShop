package cn.encmys.ykdz.forest.dailyshop.api.rarity.factory;

import cn.encmys.ykdz.forest.dailyshop.rarity.RarityImpl;

public interface RarityFactory {
    void buildRarity(String id, String name, int weight);

    RarityImpl getRarity(String id);
}
