package cn.encmys.ykdz.forest.dailyshop.api.database;

import cn.encmys.ykdz.forest.dailyshop.api.shop.cashier.log.enums.SettlementLogType;
import cn.encmys.ykdz.forest.dailyshop.price.PricePair;
import cn.encmys.ykdz.forest.dailyshop.shop.ShopImpl;
import cn.encmys.ykdz.forest.dailyshop.shop.cashier.log.SettlementLog;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.Map;
import java.util.UUID;

public interface Database {
    void saveShopData(@NotNull Map<String, ShopImpl> dataMap);

    @Nullable
    List<String> queryShopListedProducts(@NotNull String id);

    Map<String, PricePair> queryShopCachedPrices(@NotNull String id);

    long queryShopLastRestocking(@NotNull String shopName);

    void insertSettlementLog(@NotNull String shopId, @NotNull SettlementLog log);

    int queryHistoryAmountFromLogs(@NotNull String shopId, @NotNull String productId, double timeLimitInDay, int numEntries, @NotNull SettlementLogType... types);

    List<SettlementLog> queryLogInOrder(@NotNull String shopId, @NotNull UUID customer, double timeLimitInDay, int numEntries, @NotNull SettlementLogType... types);
}
