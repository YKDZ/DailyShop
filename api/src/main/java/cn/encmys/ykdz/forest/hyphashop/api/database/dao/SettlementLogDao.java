package cn.encmys.ykdz.forest.hyphashop.api.database.dao;

import cn.encmys.ykdz.forest.hyphashop.api.shop.cashier.log.SettlementLog;
import cn.encmys.ykdz.forest.hyphashop.api.shop.order.enums.OrderType;

import java.util.List;
import java.util.UUID;

public interface SettlementLogDao {
    void initDB();

    List<SettlementLog> queryLogs(UUID playerUUID, long dayLimit, long amountLimit, OrderType... types);

    List<SettlementLog> queryLogs(String shopId, long dayLimit, long amountLimit, OrderType... types);

    List<SettlementLog> queryLogs(String shopId, UUID playerUUID, long dayLimit, long amountLimit, OrderType... types);

    List<SettlementLog> queryLogs(UUID playerUUID, int pageIndex, int pageSize, OrderType... types);

    List<SettlementLog> queryLogs(String shopId, int pageIndex, int pageSize, OrderType... types);

    List<SettlementLog> queryLogs(String shopId, UUID playerUUID, int pageIndex, int pageSize, OrderType... types);

    void insertLog(SettlementLog log);

    void deleteLog(UUID customerUUID, long daysLateThan, OrderType... types);

    int countLog(UUID customerUUID, long dayLimit, long amountLimit, OrderType... types);
}
