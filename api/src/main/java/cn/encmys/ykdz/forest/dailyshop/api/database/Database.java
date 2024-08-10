package cn.encmys.ykdz.forest.dailyshop.api.database;

import cn.encmys.ykdz.forest.dailyshop.api.database.schema.ProductData;
import cn.encmys.ykdz.forest.dailyshop.api.database.schema.ProfileData;
import cn.encmys.ykdz.forest.dailyshop.api.database.schema.ShopData;
import cn.encmys.ykdz.forest.dailyshop.api.product.Product;
import cn.encmys.ykdz.forest.dailyshop.api.profile.Profile;
import cn.encmys.ykdz.forest.dailyshop.api.shop.Shop;
import cn.encmys.ykdz.forest.dailyshop.api.shop.cashier.log.SettlementLog;
import cn.encmys.ykdz.forest.dailyshop.api.shop.order.enums.OrderType;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;

public interface Database {
    void saveShopData(@NotNull List<Shop> dataMap);

    CompletableFuture<ShopData> queryShopData(@NotNull String id);

    void saveProfileData(@NotNull List<Profile> profiles);

    CompletableFuture<ProfileData> queryProfileData(@NotNull UUID ownerUUID);

    void saveProductData(List<Product> data);

    CompletableFuture<ProductData> queryProductData(@NotNull String id);

    void insertSettlementLog(@NotNull String shopId, @NotNull SettlementLog log);

    CompletableFuture<List<SettlementLog>> queryLogs(@Nullable String shopId, @Nullable UUID customer, @Nullable String productId, double timeLimitInDay, int numEntries, @NotNull OrderType... types);

    CompletableFuture<List<SettlementLog>> queryLogs(@Nullable String shopId, @Nullable UUID customer, @Nullable String productId, double timeLimitInDay, int pageIndex, int pageSize, @NotNull OrderType... types);
}
