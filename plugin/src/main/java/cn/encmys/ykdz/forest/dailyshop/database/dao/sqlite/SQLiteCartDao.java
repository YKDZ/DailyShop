package cn.encmys.ykdz.forest.dailyshop.database.dao.sqlite;

import cn.encmys.ykdz.forest.dailyshop.api.DailyShop;
import cn.encmys.ykdz.forest.dailyshop.api.database.dao.CartDao;
import cn.encmys.ykdz.forest.dailyshop.api.database.schema.CartSchema;
import cn.encmys.ykdz.forest.dailyshop.api.shop.order.enums.OrderType;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.sql.*;
import java.util.Map;
import java.util.UUID;

public class SQLiteCartDao implements CartDao {
    private final static Gson gson = new Gson();

    public SQLiteCartDao() {
        initDB();
    }

    @Override
    public void initDB() {
        try (
                Connection conn = DailyShop.DATABASE_FACTORY.getConnection();
                Statement stmt = conn.createStatement()
        ) {
            stmt.execute("""
                CREATE TABLE IF NOT EXISTS dailyshop_cart (
                    owner_uuid VARCHAR(32) NOT NULL PRIMARY KEY,
                    orders TEXT NOT NULL,
                    mode TEXT NOT NULL
                )
            """);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    @Nullable
    public CartSchema querySchema(@NotNull UUID playerUUID) {
        try (Connection conn = DailyShop.DATABASE_FACTORY.getConnection()) {
            PreparedStatement pStmt = conn.prepareStatement("SELECT * FROM dailyshop_cart WHERE owner_uuid = ?");
            pStmt.setString(1, playerUUID.toString());
            ResultSet rs = pStmt.executeQuery();
            if (rs.next()) {
                return new CartSchema(
                        playerUUID,
                        gson.fromJson(rs.getString("orders"), new TypeToken<Map<String, Integer>>() {}.getType()),
                        gson.fromJson(rs.getString("mode"), OrderType.class)
                );
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public void insertSchema(@NotNull CartSchema schema) {
        try (Connection conn = DailyShop.DATABASE_FACTORY.getConnection()) {
            PreparedStatement pStmt = conn.prepareStatement("REPLACE INTO dailyshop_cart (owner_uuid, orders, mode) VALUES (?, ?, ?)");
            pStmt.setString(1, schema.ownerUUID().toString());
            pStmt.setString(2, gson.toJson(schema.orders(), new TypeToken<Map<String, Integer>>() {}.getType()));
            pStmt.setString(3, schema.mode().name());
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void updateSchema(@NotNull CartSchema schema) {
        try (Connection conn = DailyShop.DATABASE_FACTORY.getConnection()) {
            PreparedStatement pStmt = conn.prepareStatement("UPDATE dailyshop_cart SET orders = ?, mode = ? WHERE owner_uuid = ?");
            pStmt.setString(1, gson.toJson(schema.orders()));
            pStmt.setString(2, schema.mode().name());
            pStmt.setString(3, schema.ownerUUID().toString());
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void deleteSchema(@NotNull CartSchema schema) {
        try (Connection conn = DailyShop.DATABASE_FACTORY.getConnection()) {
            PreparedStatement pStmt = conn.prepareStatement("DELETE FROM dailyshop_cart WHERE owner_uuid = ?");
            pStmt.setString(1, gson.toJson(schema.orders()));
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
