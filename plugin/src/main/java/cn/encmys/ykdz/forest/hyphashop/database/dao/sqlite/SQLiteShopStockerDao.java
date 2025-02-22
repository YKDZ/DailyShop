package cn.encmys.ykdz.forest.hyphashop.database.dao.sqlite;

import cn.encmys.ykdz.forest.hyphashop.api.HyphaShop;
import cn.encmys.ykdz.forest.hyphashop.api.database.dao.ShopStockerDao;
import cn.encmys.ykdz.forest.hyphashop.api.database.schema.ShopStockerSchema;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.sql.*;
import java.util.List;

public class SQLiteShopStockerDao implements ShopStockerDao {
    private final static Gson gson = new Gson();

    public SQLiteShopStockerDao() {
        initDB();
    }

    @Override
    public void initDB() {
        try (
                Connection conn = HyphaShop.DATABASE_FACTORY.getConnection();
                Statement stmt = conn.createStatement()
        ) {
            stmt.execute("""
                CREATE TABLE IF NOT EXISTS dailyshop_shop_stocker (
                    shop_id TEXT NOT NULL PRIMARY KEY,
                    listed_products TEXT NOT NULL,
                    last_restocking INTEGER NOT NULL
                )
            """);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public @Nullable ShopStockerSchema querySchema(@NotNull String shopId) {
        try (Connection conn = HyphaShop.DATABASE_FACTORY.getConnection()) {
            PreparedStatement pStmt = conn.prepareStatement("SELECT * FROM dailyshop_shop_stocker WHERE shop_id = ?");
            pStmt.setString(1, shopId);
            ResultSet rs = pStmt.executeQuery();
            if (rs.next()) {
                return new ShopStockerSchema(
                        rs.getString("shop_id"),
                        gson.fromJson(rs.getString("listed_products"), new TypeToken<List<String>>() {}.getType()),
                        rs.getLong("last_restocking")
                );
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public void insertSchema(@NotNull ShopStockerSchema schema) {
        try (Connection conn = HyphaShop.DATABASE_FACTORY.getConnection()) {
            PreparedStatement pStmt = conn.prepareStatement("REPLACE INTO dailyshop_shop_stocker (shop_id, listed_products, last_restocking) VALUES (?, ?, ?)");
            pStmt.setString(1, schema.shopId());
            pStmt.setString(2, gson.toJson(schema.listedProducts(), new TypeToken<List<String>>() {}.getType()));
            pStmt.setLong(3, schema.lastRestocking());
            pStmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void updateSchema(@NotNull ShopStockerSchema schema) {
        try (Connection conn = HyphaShop.DATABASE_FACTORY.getConnection()) {
            PreparedStatement pStmt = conn.prepareStatement("UPDATE dailyshop_shop_stocker SET listed_products = ?, last_restocking = ? WHERE shop_id = ?");
            pStmt.setString(1, gson.toJson(schema.listedProducts(), new TypeToken<List<String>>() {}.getType()));
            pStmt.setLong(2, schema.lastRestocking());
            pStmt.setString(3, schema.shopId());
            pStmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void deleteSchema(@NotNull ShopStockerSchema schema) {
        try (Connection conn = HyphaShop.DATABASE_FACTORY.getConnection()) {
            PreparedStatement pStmt = conn.prepareStatement("DELETE FROM dailyshop_shop_stocker WHERE shop_id = ?");
            pStmt.setString(1, schema.shopId());
            pStmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
