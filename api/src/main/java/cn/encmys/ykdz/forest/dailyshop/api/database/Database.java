package cn.encmys.ykdz.forest.dailyshop.api.database;

import cn.encmys.ykdz.forest.dailyshop.api.database.schema.ProductData;
import cn.encmys.ykdz.forest.dailyshop.api.database.schema.ShopData;
import cn.encmys.ykdz.forest.dailyshop.api.product.Product;
import cn.encmys.ykdz.forest.dailyshop.api.shop.Shop;
import cn.encmys.ykdz.forest.dailyshop.api.shop.cashier.log.SettlementLog;
import cn.encmys.ykdz.forest.dailyshop.api.shop.cashier.log.enums.SettlementLogType;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;

public interface Database {
    void saveShopData(@NotNull List<Shop> dataMap);

    CompletableFuture<ShopData> queryShopData(@NotNull String id);

    void saveProductData(List<Product> data);

    CompletableFuture<ProductData> queryProductData(@NotNull String id);

    void insertSettlementLog(@NotNull String shopId, @NotNull SettlementLog log);

    List<SettlementLog> queryLogs(@NotNull String shopId, @Nullable UUID customer, @Nullable String productId, double timeLimitInDay, int numEntries, @NotNull SettlementLogType... types);
}
