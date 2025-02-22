package cn.encmys.ykdz.forest.hyphashop.api.shop.pricer;

import cn.encmys.ykdz.forest.hyphashop.api.price.PricePair;
import cn.encmys.ykdz.forest.hyphashop.api.shop.Shop;
import org.jetbrains.annotations.NotNull;

import java.util.Map;

public interface ShopPricer {
    double getBuyPrice(@NotNull String productId);

    double getSellPrice(@NotNull String productId);

    void cachePrice(@NotNull String productId);

    // TODO 打折功能入口
    PricePair getModifiedPricePair(@NotNull String productId, @NotNull PricePair pricePair);

    void setCachedPrices(@NotNull Map<String, PricePair> cachedPrices);

    Map<String, PricePair> getCachedPrices();

    Shop getShop();
}
