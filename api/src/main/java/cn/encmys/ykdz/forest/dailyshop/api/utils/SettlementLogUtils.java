package cn.encmys.ykdz.forest.dailyshop.api.utils;

import cn.encmys.ykdz.forest.dailyshop.api.DailyShop;
import cn.encmys.ykdz.forest.dailyshop.api.shop.cashier.log.SettlementLog;
import cn.encmys.ykdz.forest.dailyshop.api.shop.order.enums.OrderType;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutionException;

public class SettlementLogUtils {
    public static int getHistoryAmountFromLogs(@NotNull String shopId, @NotNull String productId, long timeLimitInDay, int numEntries, @NotNull OrderType... types) {
        int totalSales = 0;

        List<SettlementLog> logs;
        try {
            logs = DailyShop.DATABASE.queryLogs(shopId, null, null, timeLimitInDay, numEntries, types).get();
        } catch (InterruptedException | ExecutionException e) {
            throw new RuntimeException(e);
        }

        // 计算总销售量
        totalSales += logs.stream()
                .flatMap(log -> log.getOrderedProducts().entrySet().stream())
                .filter(entry -> entry.getKey().equals(productId))
                .mapToInt(Map.Entry::getValue)
                .sum();

        return totalSales;
    }
}
