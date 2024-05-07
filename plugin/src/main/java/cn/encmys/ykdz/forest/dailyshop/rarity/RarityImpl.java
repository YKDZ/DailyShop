package cn.encmys.ykdz.forest.dailyshop.rarity;

import cn.encmys.ykdz.forest.dailyshop.api.rarity.Rarity;

public class RarityImpl implements Rarity {
    private final String id;
    private final String name;
    private final int weight;

    public RarityImpl(String id, String name, int weight) {
        this.id = id;
        this.name = name;
        this.weight = weight;
    }

    @Override
    public String getId() {
        return id;
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public int getWeight() {
        return weight;
    }
}
