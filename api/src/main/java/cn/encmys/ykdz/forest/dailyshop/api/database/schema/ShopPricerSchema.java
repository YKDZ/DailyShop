package cn.encmys.ykdz.forest.dailyshop.api.database.schema;

import cn.encmys.ykdz.forest.dailyshop.api.price.PricePair;
import cn.encmys.ykdz.forest.dailyshop.api.shop.pricer.ShopPricer;
import org.jetbrains.annotations.NotNull;

import java.util.Map;

public record ShopPricerSchema(@NotNull String shopId,
                               @NotNull Map<String, PricePair> cachedPrices) {
    public static ShopPricerSchema of(ShopPricer pricer) {
        return new ShopPricerSchema(pricer.getShop().getId(), pricer.getCachedPrices());
    }
}
