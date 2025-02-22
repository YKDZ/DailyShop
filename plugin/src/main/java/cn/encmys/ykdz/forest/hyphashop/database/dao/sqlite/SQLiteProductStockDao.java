package cn.encmys.ykdz.forest.hyphashop.database.dao.sqlite;

import cn.encmys.ykdz.forest.hyphashop.api.HyphaShop;
import cn.encmys.ykdz.forest.hyphashop.api.database.dao.ProductStockDao;
import cn.encmys.ykdz.forest.hyphashop.api.database.schema.ProductStockSchema;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.sql.*;
import java.util.Map;
import java.util.UUID;

public class SQLiteProductStockDao implements ProductStockDao {
    private final static Gson gson = new Gson();

    public SQLiteProductStockDao() {
        initDB();
    }

    @Override
    public void initDB() {
        try (
                Connection conn = HyphaShop.DATABASE_FACTORY.getConnection();
                Statement stmt = conn.createStatement()
        ) {
            stmt.execute("""
                CREATE TABLE IF NOT EXISTS dailyshop_product_stock (
                    product_id TEXT NOT NULL PRIMARY KEY,
                    current_player_amount TEXT NOT NULL,
                    current_global_amount INTEGER NOT NULL
                )
            """);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public @Nullable ProductStockSchema querySchema(@NotNull String productId) {
        try (Connection conn = HyphaShop.DATABASE_FACTORY.getConnection()) {
            PreparedStatement pStmt = conn.prepareStatement("SELECT * FROM dailyshop_product_stock WHERE product_id = ?");
            pStmt.setString(1, productId);
            ResultSet rs = pStmt.executeQuery();
            if (rs.next()) {
                return new ProductStockSchema(
                        productId,
                        gson.fromJson(rs.getString("current_player_amount"), new TypeToken<Map<UUID, Integer>>() {}.getType()),
                        rs.getInt("current_global_amount")
                );
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public void insertSchema(@NotNull ProductStockSchema schema) {
        try (Connection conn = HyphaShop.DATABASE_FACTORY.getConnection()) {
            PreparedStatement pStmt = conn.prepareStatement("REPLACE INTO dailyshop_product_stock VALUES (?, ?, ?)");
            pStmt.setString(1, schema.productId());
            pStmt.setString(2, gson.toJson(schema.currentPlayerAmount(), new TypeToken<Map<UUID, Integer>>() {}.getType()));
            pStmt.setInt(3, schema.currentGlobalAmount());
            pStmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void updateSchema(@NotNull ProductStockSchema schema) {
        try (Connection conn = HyphaShop.DATABASE_FACTORY.getConnection()) {
            PreparedStatement pStmt = conn.prepareStatement("UPDATE dailyshop_product_stock SET current_player_amount = ?, current_global_amount = ? WHERE product_id = ?");
            pStmt.setString(1, gson.toJson(schema.currentPlayerAmount(), new TypeToken<Map<UUID, Integer>>() {}.getType()));
            pStmt.setInt(2, schema.currentGlobalAmount());
            pStmt.setString(3, schema.productId());
            pStmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void deleteSchema(@NotNull ProductStockSchema schema) {
        try (Connection conn = HyphaShop.DATABASE_FACTORY.getConnection()) {
            PreparedStatement pStmt = conn.prepareStatement("DELETE FROM dailyshop_product_stock WHERE product_id = ?");
            pStmt.setString(1, schema.productId());
            pStmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
