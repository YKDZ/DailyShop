package cn.encmys.ykdz.forest.hyphashop.utils;

import cn.encmys.ykdz.forest.hyphashop.api.HyphaShop;
import cn.encmys.ykdz.forest.hyphashop.api.shop.cashier.log.SettlementLog;
import cn.encmys.ykdz.forest.hyphashop.api.shop.order.enums.OrderType;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public class SettlementLogUtils {
    public static long getHistoryAmountFromLogs(@NotNull String shopId, @NotNull String productId, long timeLimitInDay, int numEntries, @NotNull OrderType... types) {
        List<SettlementLog> logs = HyphaShop.DATABASE_FACTORY.getSettlementLogDao().queryLogs(shopId, timeLimitInDay, numEntries, types);

        // 计算总销售量
        return logs.stream()
                .flatMap(log -> log.getOrderedProducts().entrySet().stream())
                .filter(entry -> entry.getKey().equals(productId))
                .mapToInt(entry -> entry.getValue().stack() * entry.getValue().amount())
                .sum();
    }

    public static long getHistoryStackAmountFromLogs(@NotNull String shopId, @NotNull String productId, long timeLimitInDay, int numEntries, @NotNull OrderType... types) {
        List<SettlementLog> logs = HyphaShop.DATABASE_FACTORY.getSettlementLogDao().queryLogs(shopId, timeLimitInDay, numEntries, types);

        // 计算总销售量
        return logs.stream()
                .flatMap(log -> log.getOrderedProducts().entrySet().stream())
                .filter(entry -> entry.getKey().equals(productId))
                .mapToInt(entry -> entry.getValue().stack())
                .sum();
    }
}
