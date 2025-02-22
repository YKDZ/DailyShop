package cn.encmys.ykdz.forest.hyphashop.database.dao.sqlite;

import cn.encmys.ykdz.forest.hyphashop.api.HyphaShop;
import cn.encmys.ykdz.forest.hyphashop.api.database.dao.ShopPricerDao;
import cn.encmys.ykdz.forest.hyphashop.api.database.schema.ShopPricerSchema;
import cn.encmys.ykdz.forest.hyphashop.price.PricePairImpl;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.sql.*;
import java.util.Map;

public class SQLiteShopPricerDao implements ShopPricerDao {
    private final static Gson gson = new Gson();

    public SQLiteShopPricerDao() {
        initDB();
    }

    @Override
    public void initDB() {
        try (
                Connection conn = HyphaShop.DATABASE_FACTORY.getConnection();
                Statement stmt = conn.createStatement()
        ) {
            stmt.execute("""
                CREATE TABLE IF NOT EXISTS dailyshop_shop_pricer (
                    shop_id TEXT NOT NULL PRIMARY KEY,
                    cached_prices TEXT NOT NULL
                )
            """);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public @Nullable ShopPricerSchema querySchema(@NotNull String shopId) {
        try (Connection conn = HyphaShop.DATABASE_FACTORY.getConnection()) {
            PreparedStatement pStmt = conn.prepareStatement("SELECT * FROM dailyshop_shop_pricer WHERE shop_id = ?");
            pStmt.setString(1, shopId);
            ResultSet rs = pStmt.executeQuery();
            if (rs.next()) {
                return new ShopPricerSchema(
                        shopId,
                        gson.fromJson(rs.getString("cached_prices"), new TypeToken<Map<String, PricePairImpl>>() {}.getType())
                );
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public void insertSchema(@NotNull ShopPricerSchema schema) {
        try (Connection conn = HyphaShop.DATABASE_FACTORY.getConnection()) {
            PreparedStatement pStmt = conn.prepareStatement("REPLACE INTO dailyshop_shop_pricer (shop_id, cached_prices) VALUES (?, ?)");
            pStmt.setString(1, schema.shopId());
            pStmt.setString(2, gson.toJson(schema.cachedPrices(), new TypeToken<Map<String, PricePairImpl>>() {}.getType()));
            pStmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void updateSchema(@NotNull ShopPricerSchema schema) {
        try (Connection conn = HyphaShop.DATABASE_FACTORY.getConnection()) {
            PreparedStatement pStmt = conn.prepareStatement("UPDATE dailyshop_shop_pricer SET shop_id = ?, cached_prices=? WHERE shop_id = ?");
            pStmt.setString(1, schema.shopId());
            pStmt.setString(2, gson.toJson(schema.cachedPrices(), new TypeToken<Map<String, PricePairImpl>>() {}.getType()));
            pStmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void deleteSchema(@NotNull ShopPricerSchema schema) {
        try (Connection conn = HyphaShop.DATABASE_FACTORY.getConnection()) {
            PreparedStatement pStmt = conn.prepareStatement("DELETE FROM dailyshop_shop_pricer WHERE shop_id = ?");
            pStmt.setString(1, schema.shopId());
            pStmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
