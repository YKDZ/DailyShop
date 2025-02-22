package cn.encmys.ykdz.forest.hyphashop.rarity;

import cn.encmys.ykdz.forest.hyphashop.api.rarity.Rarity;
import org.jetbrains.annotations.NotNull;

public record RarityImpl(@NotNull String id, @NotNull String name, int weight) implements Rarity {
}
