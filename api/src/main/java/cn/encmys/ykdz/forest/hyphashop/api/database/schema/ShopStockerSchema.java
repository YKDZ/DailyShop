package cn.encmys.ykdz.forest.hyphashop.api.database.schema;

import cn.encmys.ykdz.forest.hyphashop.api.shop.stocker.ShopStocker;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public record ShopStockerSchema(@NotNull String shopId,
                                @NotNull List<String> listedProducts,
                                long lastRestocking) {
    public static ShopStockerSchema of(ShopStocker stocker) {
        return new ShopStockerSchema(stocker.getShop().getId(), stocker.getListedProducts(), stocker.getLastRestocking());
    }
}
