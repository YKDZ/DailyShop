package cn.encmys.ykdz.forest.dailyshop.database.dao.sqlite;

import cn.encmys.ykdz.forest.dailyshop.api.DailyShop;
import cn.encmys.ykdz.forest.dailyshop.api.database.dao.ShopCashierDao;
import cn.encmys.ykdz.forest.dailyshop.api.database.schema.ShopCashierSchema;
import com.google.gson.Gson;

import java.sql.*;

public class SQLiteShopCashierDao implements ShopCashierDao {
    private final static Gson gson = new Gson();

    public SQLiteShopCashierDao() {
        initDB();
    }

    @Override
    public void initDB() {
        try (
                Connection conn = DailyShop.DATABASE_FACTORY.getConnection();
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
    public ShopCashierSchema querySchema(String shopId) {
        try (Connection conn = DailyShop.DATABASE_FACTORY.getConnection()) {
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
    public void insertSchema(ShopCashierSchema schema) {
        try (Connection conn = DailyShop.DATABASE_FACTORY.getConnection()) {
            PreparedStatement pStmt = conn.prepareStatement("REPLACE INTO dailyshop_shop_cashier (shop_id, balance) VALUES (?, ?)");
            pStmt.setString(1, schema.shopId());
            pStmt.setDouble(2, schema.balance());
            pStmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void updateSchema(ShopCashierSchema schema) {
        try (Connection conn = DailyShop.DATABASE_FACTORY.getConnection()) {
            PreparedStatement pStmt = conn.prepareStatement("UPDATE dailyshop_shop_cashier SET balance = ? WHERE shop_id = ?");
            pStmt.setString(1, schema.shopId());
            pStmt.setDouble(2, schema.balance());
            pStmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void deleteSchema(ShopCashierSchema schema) {
        try (Connection conn = DailyShop.DATABASE_FACTORY.getConnection()) {
            PreparedStatement pStmt = conn.prepareStatement("DELETE FROM dailyshop_shop_cashier WHERE shop_id = ?");
            pStmt.setString(1, schema.shopId());
            pStmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
