package cn.encmys.ykdz.forest.hyphashop.database.dao.sqlite;

import cn.encmys.ykdz.forest.hyphashop.api.HyphaShop;
import cn.encmys.ykdz.forest.hyphashop.api.database.dao.ShopCounterDao;
import cn.encmys.ykdz.forest.hyphashop.api.database.schema.ShopCounterSchema;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.sql.*;
import java.util.Map;

public class SQLiteShopCounterDao implements ShopCounterDao {
    private final static Gson gson = new Gson();

    public SQLiteShopCounterDao() {
        initDB();
    }

    @Override
    public void initDB() {
        try (
                Connection conn = HyphaShop.DATABASE_FACTORY.getConnection();
                Statement stmt = conn.createStatement()
        ) {
            stmt.execute("""
                CREATE TABLE IF NOT EXISTS dailyshop_shop_counter (
                    shop_id TEXT NOT NULL PRIMARY KEY,
                    cached_amounts TEXT NOT NULL
                )
            """);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public @Nullable ShopCounterSchema querySchema(@NotNull String shopId) {
        try (Connection conn = HyphaShop.DATABASE_FACTORY.getConnection()) {
            PreparedStatement pStmt = conn.prepareStatement("SELECT * FROM dailyshop_shop_counter WHERE shop_id = ?");
            pStmt.setString(1, shopId);
            ResultSet rs = pStmt.executeQuery();
            if (rs.next()) {
                return new ShopCounterSchema(
                        shopId,
                        gson.fromJson(rs.getString("cached_amounts"), new TypeToken<Map<String, Integer>>() {}.getType())
                );
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public void insertSchema(@NotNull ShopCounterSchema schema) {
        try (Connection conn = HyphaShop.DATABASE_FACTORY.getConnection()) {
            PreparedStatement pStmt = conn.prepareStatement("REPLACE INTO dailyshop_shop_counter (shop_id, cached_amounts) VALUES (?, ?)");
            pStmt.setString(1, schema.shopId());
            pStmt.setString(2, gson.toJson(schema.cachedAmounts(), new TypeToken<Map<String, Integer>>() {}.getType()));
            pStmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void updateSchema(@NotNull ShopCounterSchema schema) {
        try (Connection conn = HyphaShop.DATABASE_FACTORY.getConnection()) {
            PreparedStatement pStmt = conn.prepareStatement("UPDATE dailyshop_shop_counter SET shop_id = ?, cached_amounts=? WHERE shop_id = ?");
            pStmt.setString(1, schema.shopId());
            pStmt.setString(2, gson.toJson(schema.cachedAmounts(), new TypeToken<Map<String, Integer>>() {}.getType()));
            pStmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void deleteSchema(@NotNull ShopCounterSchema schema) {
        try (Connection conn = HyphaShop.DATABASE_FACTORY.getConnection()) {
            PreparedStatement pStmt = conn.prepareStatement("DELETE FROM dailyshop_shop_counter WHERE shop_id = ?");
            pStmt.setString(1, schema.shopId());
            pStmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
