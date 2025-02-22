package cn.encmys.ykdz.forest.hyphashop.database.dao.sqlite;

import cn.encmys.ykdz.forest.hyphashop.api.HyphaShop;
import cn.encmys.ykdz.forest.hyphashop.api.database.dao.ShopCashierDao;
import cn.encmys.ykdz.forest.hyphashop.api.database.schema.ShopCashierSchema;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.sql.*;

public class SQLiteShopCashierDao implements ShopCashierDao {
    public SQLiteShopCashierDao() {
        initDB();
    }

    @Override
    public void initDB() {
        try (
                Connection conn = HyphaShop.DATABASE_FACTORY.getConnection();
                Statement stmt = conn.createStatement()
        ) {
            stmt.execute("""
                CREATE TABLE IF NOT EXISTS dailyshop_shop_cashier (
                    shop_id TEXT NOT NULL PRIMARY KEY,
                    balance DOUBLE NOT NULL
                )
            """);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public @Nullable ShopCashierSchema querySchema(@NotNull String shopId) {
        try (Connection conn = HyphaShop.DATABASE_FACTORY.getConnection()) {
            PreparedStatement pStmt = conn.prepareStatement("SELECT * FROM dailyshop_shop_cashier WHERE shop_id = ?");
            pStmt.setString(1, shopId);
            ResultSet rs = pStmt.executeQuery();
            if (rs.next()) {
                return new ShopCashierSchema(
                        shopId,
                        rs.getDouble("balance")
                );
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public void insertSchema(@NotNull ShopCashierSchema schema) {
        try (Connection conn = HyphaShop.DATABASE_FACTORY.getConnection()) {
            PreparedStatement pStmt = conn.prepareStatement("REPLACE INTO dailyshop_shop_cashier (shop_id, balance) VALUES (?, ?)");
            pStmt.setString(1, schema.shopId());
            pStmt.setDouble(2, schema.balance());
            pStmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void updateSchema(@NotNull ShopCashierSchema schema) {
        try (Connection conn = HyphaShop.DATABASE_FACTORY.getConnection()) {
            PreparedStatement pStmt = conn.prepareStatement("UPDATE dailyshop_shop_cashier SET balance = ? WHERE shop_id = ?");
            pStmt.setString(1, schema.shopId());
            pStmt.setDouble(2, schema.balance());
            pStmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void deleteSchema(@NotNull ShopCashierSchema schema) {
        try (Connection conn = HyphaShop.DATABASE_FACTORY.getConnection()) {
            PreparedStatement pStmt = conn.prepareStatement("DELETE FROM dailyshop_shop_cashier WHERE shop_id = ?");
            pStmt.setString(1, schema.shopId());
            pStmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
