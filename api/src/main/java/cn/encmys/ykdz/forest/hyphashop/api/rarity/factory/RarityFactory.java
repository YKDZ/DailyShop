package cn.encmys.ykdz.forest.hyphashop.api.rarity.factory;

import cn.encmys.ykdz.forest.hyphashop.api.rarity.Rarity;

public interface RarityFactory {
    void buildRarity(String id, String name, int weight);

    Rarity getRarity(String id);
}
