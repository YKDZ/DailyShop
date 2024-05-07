package cn.encmys.ykdz.forest.dailyshop.api.shop.pricer;

import cn.encmys.ykdz.forest.dailyshop.api.price.PricePair;
import org.jetbrains.annotations.NotNull;

import java.util.Map;

public interface ShopPricer {
    double getBuyPrice(@NotNull String productId);

    double getSellPrice(@NotNull String productId);

    void cachePrice(@NotNull String productId);

    // Todo Discount or something
    PricePair getModifiedPricePair(@NotNull String productId, @NotNull PricePair pricePair);

    void setCachedPrices(@NotNull Map<String, PricePair> cachedPrices);

    Map<String, PricePair> getCachedPrices();
}
