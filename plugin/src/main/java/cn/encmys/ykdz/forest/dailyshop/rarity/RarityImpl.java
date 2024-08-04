package cn.encmys.ykdz.forest.dailyshop.rarity;

import cn.encmys.ykdz.forest.dailyshop.api.rarity.Rarity;

public record RarityImpl(String id, String name, int weight) implements Rarity {
}
