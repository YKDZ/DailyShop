package cn.encmys.ykdz.forest.hyphashop.database.dao.sqlite;

import cn.encmys.ykdz.forest.hyphashop.api.HyphaShop;
import cn.encmys.ykdz.forest.hyphashop.api.database.dao.SettlementLogDao;
import cn.encmys.ykdz.forest.hyphashop.api.shop.cashier.log.SettlementLog;
import cn.encmys.ykdz.forest.hyphashop.api.shop.cashier.log.amount.AmountPair;
import cn.encmys.ykdz.forest.hyphashop.api.shop.order.enums.OrderType;
import cn.encmys.ykdz.forest.hyphashop.shop.cashier.log.SettlementLogImpl;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import org.jetbrains.annotations.NotNull;

import java.sql.*;
import java.util.*;

public class SQLiteSettlementLogDao implements SettlementLogDao {
    private final static Gson gson = new Gson();

    public SQLiteSettlementLogDao() {
        initDB();
    }

    @Override
    public void initDB() {
        try (
                Connection conn = HyphaShop.DATABASE_FACTORY.getConnection();
                Statement stmt = conn.createStatement()
        ) {
            stmt.execute("""
                CREATE TABLE IF NOT EXISTS dailyshop_settlement_log (
                    id INTEGER PRIMARY KEY AUTOINCREMENT,
                    customer_uuid TEXT NOT NULL,
                    type TEXT NOT NULL,
                    transition_time TIMESTAMP NOT NULL,
                    price DOUBLE NOT NULL,
                    ordered_products TEXT NOT NULL,
                    settled_shop TEXT NOT NULL
                )
            """);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public List<SettlementLog> queryLogs(UUID customerUUID, long dayLimit, long amountLimit, OrderType... types) {
        List<SettlementLog> logs = new ArrayList<>();
        String query = """
            SELECT * FROM dailyshop_settlement_log
            WHERE customer_uuid = ?
        """;

        List<Object> params = new ArrayList<>();
        params.add(customerUUID.toString());

        if (types.length > 0) {
            query += " AND type IN (%s)".formatted(
                    String.join(", ", Collections.nCopies(types.length, "?"))
            );
            for (OrderType type : types) {
                params.add(type.name());
            }
        }

        if (dayLimit > 0) {
            query += " AND transition_time >= datetime('now', ? || ' day')";
            params.add(-dayLimit);
        }

        query += " ORDER BY transition_time DESC";

        if (amountLimit > 0) {
            query += " LIMIT ?";
            params.add(amountLimit);
        }

        try (
                Connection conn = HyphaShop.DATABASE_FACTORY.getConnection();
                PreparedStatement pStmt = conn.prepareStatement(query)
        ) {
            for (int i = 0; i < params.size(); i++) {
                pStmt.setObject(i + 1, params.get(i));
            }
            ResultSet rs = pStmt.executeQuery();
            while (rs.next()) {
                logs.add(parseSettlementLog(rs));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return logs;
    }

    @Override
    public List<SettlementLog> queryLogs(String settledShop, long dayLimit, long amountLimit, OrderType... types) {
        List<SettlementLog> logs = new ArrayList<>();
        String baseQuery = """
            SELECT * FROM dailyshop_settlement_log
            WHERE settled_shop = ?
        """;
        List<Object> params = new ArrayList<>();
        params.add(settledShop);

        if (types.length > 0) {
            baseQuery += " AND type IN (%s)".formatted(
                    String.join(", ", Collections.nCopies(types.length, "?"))
            );
            for (OrderType type : types) {
                params.add(type.name());
            }
        }

        if (dayLimit > 0) {
            baseQuery += " AND transition_time >= datetime('now', ? || ' day')";
            params.add(-dayLimit);
        }

        baseQuery += " ORDER BY transition_time DESC";
        if (amountLimit > 0) {
            baseQuery += " LIMIT ?";
            params.add(amountLimit);
        }

        try (
                Connection conn = HyphaShop.DATABASE_FACTORY.getConnection();
                PreparedStatement pStmt = conn.prepareStatement(baseQuery)
        ) {
            for (int i = 0; i < params.size(); i++) {
                pStmt.setObject(i + 1, params.get(i));
            }
            ResultSet rs = pStmt.executeQuery();
            while (rs.next()) {
                logs.add(parseSettlementLog(rs));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return logs;
    }

    @Override
    public List<SettlementLog> queryLogs(String settledShop, UUID customerUUID, long dayLimit, long amountLimit, OrderType... types) {
        List<SettlementLog> logs = new ArrayList<>();
        String baseQuery = """
            SELECT * FROM dailyshop_settlement_log
            WHERE settled_shop = ? AND customer_uuid = ?
        """;
        List<Object> params = new ArrayList<>();
        params.add(settledShop);
        params.add(customerUUID.toString());

        if (types.length > 0) {
            baseQuery += " AND type IN (%s)".formatted(
                    String.join(", ", Collections.nCopies(types.length, "?"))
            );
            for (OrderType type : types) {
                params.add(type.name());
            }
        }

        if (dayLimit > 0) {
            baseQuery += " AND transition_time >= datetime('now', ? || ' day')";
            params.add(-dayLimit);
        }

        baseQuery += " ORDER BY transition_time DESC";
        if (amountLimit > 0) {
            baseQuery += " LIMIT ?";
            params.add(amountLimit);
        }

        try (
                Connection conn = HyphaShop.DATABASE_FACTORY.getConnection();
                PreparedStatement pStmt = conn.prepareStatement(baseQuery)
        ) {
            for (int i = 0; i < params.size(); i++) {
                pStmt.setObject(i + 1, params.get(i));
            }
            ResultSet rs = pStmt.executeQuery();
            while (rs.next()) {
                logs.add(parseSettlementLog(rs));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return logs;
    }

    @Override
    public List<SettlementLog> queryLogs(UUID customerUUID, int pageIndex, int pageSize, OrderType... types) {
        List<SettlementLog> logs = new ArrayList<>();
        String baseQuery = """
            SELECT * FROM dailyshop_settlement_log
            WHERE customer_uuid = ?
        """;
        List<Object> params = new ArrayList<>();
        params.add(customerUUID.toString());

        if (types.length > 0) {
            baseQuery += " AND type IN (%s)".formatted(
                    String.join(", ", Collections.nCopies(types.length, "?"))
            );
            for (OrderType type : types) {
                params.add(type.name());
            }
        }

        baseQuery += " ORDER BY transition_time DESC LIMIT ? OFFSET ?";
        params.add(pageSize);
        params.add(pageSize * pageIndex);

        try (
                Connection conn = HyphaShop.DATABASE_FACTORY.getConnection();
                PreparedStatement pStmt = conn.prepareStatement(baseQuery)
        ) {
            for (int i = 0; i < params.size(); i++) {
                pStmt.setObject(i + 1, params.get(i));
            }
            ResultSet rs = pStmt.executeQuery();
            while (rs.next()) {
                logs.add(parseSettlementLog(rs));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return logs;
    }

    @Override
    public List<SettlementLog> queryLogs(String settledShop, int pageIndex, int pageSize, OrderType... types) {
        List<SettlementLog> logs = new ArrayList<>();
        String baseQuery = """
            SELECT * FROM dailyshop_settlement_log
            WHERE settled_shop = ?
        """;
        List<Object> params = new ArrayList<>();
        params.add(settledShop);

        if (types.length > 0) {
            baseQuery += " AND type IN (%s)".formatted(
                    String.join(", ", Collections.nCopies(types.length, "?"))
            );
            for (OrderType type : types) {
                params.add(type.name());
            }
        }

        baseQuery += " ORDER BY transition_time DESC LIMIT ? OFFSET ?";
        params.add(pageSize);
        params.add(pageSize * pageIndex);

        try (
                Connection conn = HyphaShop.DATABASE_FACTORY.getConnection();
                PreparedStatement pStmt = conn.prepareStatement(baseQuery)
        ) {
            for (int i = 0; i < params.size(); i++) {
                pStmt.setObject(i + 1, params.get(i));
            }
            ResultSet rs = pStmt.executeQuery();
            while (rs.next()) {
                logs.add(parseSettlementLog(rs));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return logs;
    }

    @Override
    public List<SettlementLog> queryLogs(String settledShop, UUID customerUUID, int pageIndex, int pageSize, OrderType... types) {
        List<SettlementLog> logs = new ArrayList<>();
        String baseQuery = """
            SELECT * FROM dailyshop_settlement_log
            WHERE settled_shop = ? AND customer_uuid = ?
        """;
        List<Object> params = new ArrayList<>();
        params.add(settledShop);
        params.add(customerUUID.toString());

        if (types.length > 0) {
            baseQuery += " AND type IN (%s)".formatted(
                    String.join(", ", Collections.nCopies(types.length, "?"))
            );
            for (OrderType type : types) {
                params.add(type.name());
            }
        }

        baseQuery += " ORDER BY transition_time DESC LIMIT ? OFFSET ?";
        params.add(pageSize);
        params.add(pageSize * pageIndex);

        try (
                Connection conn = HyphaShop.DATABASE_FACTORY.getConnection();
                PreparedStatement pStmt = conn.prepareStatement(baseQuery)
        ) {
            for (int i = 0; i < params.size(); i++) {
                pStmt.setObject(i + 1, params.get(i));
            }
            ResultSet rs = pStmt.executeQuery();
            while (rs.next()) {
                logs.add(parseSettlementLog(rs));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return logs;
    }

    @Override
    public void insertLog(@NotNull SettlementLog log) {
        try (Connection conn = HyphaShop.DATABASE_FACTORY.getConnection()) {
            PreparedStatement pStmt = conn.prepareStatement("""
                REPLACE INTO dailyshop_settlement_log
                (customer_uuid, type, transition_time, price, ordered_products, settled_shop)
                VALUES (?, ?, ?, ?, ?, ?)
            """);
            pStmt.setString(1, log.getCustomerUUID().toString());
            pStmt.setString(2, log.getType().toString());
            pStmt.setTimestamp(3, new Timestamp(log.getTransitionTime().getTime()));
            pStmt.setDouble(4, log.getTotalPrice());
            pStmt.setString(5, gson.toJson(log.getOrderedProducts(), new TypeToken<Map<String, AmountPair>>() {}.getType()));
            pStmt.setString(6, log.getSettledShopId());
            pStmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void deleteLog(UUID customerUUID, long daysLateThan, OrderType... types) {
        String deleteSQL = """
            DELETE FROM dailyshop_settlement_log
            WHERE customer_uuid = ?
        """;
        List<Object> params = new ArrayList<>();
        params.add(customerUUID.toString());

        if (types.length > 0) {
            deleteSQL += " AND type IN (%s)".formatted(
                    String.join(", ", Collections.nCopies(types.length, "?"))
            );
            for (OrderType type : types) {
                params.add(type.name());
            }
        }

        deleteSQL += " AND transition_time < datetime('now', ? || ' day')";
        params.add(-daysLateThan);

        try (
                Connection conn = HyphaShop.DATABASE_FACTORY.getConnection();
                PreparedStatement pStmt = conn.prepareStatement(deleteSQL)
        ) {
            for (int i = 0; i < params.size(); i++) {
                pStmt.setObject(i + 1, params.get(i));
            }
            pStmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Override
    public int countLog(UUID customerUUID, long dayLimit, long amountLimit, OrderType... types) {
        String countSQL = """
            SELECT COUNT(*) FROM dailyshop_settlement_log
            WHERE customer_uuid = ?
        """;
        List<Object> params = new ArrayList<>();
        params.add(customerUUID.toString());

        if (types.length > 0) {
            countSQL += " AND type IN (%s)".formatted(
                    String.join(", ", Collections.nCopies(types.length, "?"))
            );
            for (OrderType type : types) {
                params.add(type.name());
            }
        }

        if (dayLimit > 0) {
            countSQL += " AND transition_time >= datetime('now', ? || ' day')";
            params.add(-dayLimit);
        }

        int count = 0;
        try (
                Connection conn = HyphaShop.DATABASE_FACTORY.getConnection();
                PreparedStatement pStmt = conn.prepareStatement(countSQL)
        ) {
            for (int i = 0; i < params.size(); i++) {
                pStmt.setObject(i + 1, params.get(i));
            }
            ResultSet rs = pStmt.executeQuery();
            if (rs.next()) {
                count = rs.getInt(1);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return count;
    }

    private static SettlementLog parseSettlementLog(@NotNull ResultSet rs) {
        try {
            SettlementLog log;
            UUID customerUUID = UUID.fromString(rs.getString("customer_uuid"));
            OrderType type = OrderType.valueOf(rs.getString("type"));
            switch (type) {
                case SELL_TO -> log = SettlementLogImpl.sellToLog(customerUUID);
                case BUY_FROM -> log = SettlementLogImpl.buyFromLog(customerUUID);
                default -> log = SettlementLogImpl.buyAllFromLog(customerUUID);
            }
            return log
                    .setOrderedProducts(gson.fromJson(rs.getString("ordered_products"), new TypeToken<Map<String, AmountPair>>() {}.getType()))
                    .setTotalPrice(rs.getDouble("price"))
                    .setTransitionTime(rs.getTimestamp("transition_time"))
                    .setSettledShopId(rs.getString("settled_shop"));
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }
}
