package cn.encmys.ykdz.forest.hyphashop.api.database.schema;

import cn.encmys.ykdz.forest.hyphashop.api.shop.counter.ShopCounter;
import org.jetbrains.annotations.NotNull;

import java.util.Map;

public record ShopCounterSchema(@NotNull String shopId,
                                @NotNull Map<String, Integer> cachedAmounts) {
    public static ShopCounterSchema of(@NotNull ShopCounter counter) {
        return new ShopCounterSchema(counter.getShop().getId(), counter.getCachedAmounts());
    }
}
