package cn.encmys.ykdz.forest.dailyshop.api.shop.counter;

import cn.encmys.ykdz.forest.dailyshop.api.shop.Shop;
import org.jetbrains.annotations.NotNull;

public interface ShopCounter {
    void cacheAmount(@NotNull String productId);

    int getAmount(@NotNull String productId);

    Shop getShop();
}
