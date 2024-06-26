package cn.encmys.ykdz.forest.dailyshop.api.database;

import cn.encmys.ykdz.forest.dailyshop.api.price.PricePair;
import cn.encmys.ykdz.forest.dailyshop.api.shop.Shop;
import cn.encmys.ykdz.forest.dailyshop.api.shop.cashier.log.SettlementLog;
import cn.encmys.ykdz.forest.dailyshop.api.shop.cashier.log.enums.SettlementLogType;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.Map;
import java.util.UUID;

public interface Database {
    void saveShopData(@NotNull Map<String, Shop> dataMap);

    @Nullable
    List<String> queryShopListedProducts(@NotNull String id);

    Map<String, PricePair> queryShopCachedPrices(@NotNull String id);

    long queryShopLastRestocking(@NotNull String shopName);

    void insertSettlementLog(@NotNull String shopId, @NotNull SettlementLog log);

    int queryHistoryAmountFromLogs(@NotNull String shopId, @NotNull String productId, double timeLimitInDay, int numEntries, @NotNull SettlementLogType... types);

    List<SettlementLog> queryLogInOrder(@NotNull String shopId, @NotNull UUID customer, double timeLimitInDay, int numEntries, @NotNull SettlementLogType... types);
}
