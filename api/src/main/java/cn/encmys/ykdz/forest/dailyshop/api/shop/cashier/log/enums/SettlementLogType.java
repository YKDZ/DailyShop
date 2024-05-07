package cn.encmys.ykdz.forest.dailyshop.api.shop.cashier.log.enums;

import cn.encmys.ykdz.forest.dailyshop.api.shop.order.enums.OrderType;
import org.jetbrains.annotations.NotNull;

public enum SettlementLogType {
    BUY_FROM,
    BUY_ALL_FROM,
    SELL_TO;

    public static SettlementLogType getFromOrderType(@NotNull OrderType orderType) {
        return valueOf(orderType.name());
    }

    public static boolean contains(SettlementLogType type, SettlementLogType... types) {
        for (SettlementLogType settlementLogType : types) {
            if (settlementLogType.name().equals(type.name())) {
                return true;
            }
        }
        return false;
    }
}
