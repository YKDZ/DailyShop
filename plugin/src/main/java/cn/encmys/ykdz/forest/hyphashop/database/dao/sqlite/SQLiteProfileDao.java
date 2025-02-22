package cn.encmys.ykdz.forest.hyphashop.database.dao.sqlite;

import cn.encmys.ykdz.forest.hyphashop.api.HyphaShop;
import cn.encmys.ykdz.forest.hyphashop.api.database.dao.ProfileDao;
import cn.encmys.ykdz.forest.hyphashop.api.database.schema.ProfileSchema;
import cn.encmys.ykdz.forest.hyphashop.api.profile.enums.ShoppingMode;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.sql.*;
import java.util.Map;
import java.util.UUID;

public class SQLiteProfileDao implements ProfileDao {
    private final static Gson gson = new Gson();

    public SQLiteProfileDao() {
        initDB();
    }

    @Override
    public void initDB() {
        try (Connection conn = HyphaShop.DATABASE_FACTORY.getConnection();
             Statement stmt = conn.createStatement()
        ) {
            stmt.execute("""
                CREATE TABLE IF NOT EXISTS dailyshop_profile (
                    owner_uuid VARCHAR(32) PRIMARY KEY,
                    shopping_modes TEXT NOT NULL
                )
            """);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public @Nullable ProfileSchema querySchema(@NotNull UUID playerUUID) {
        try (Connection conn = HyphaShop.DATABASE_FACTORY.getConnection()) {
            PreparedStatement pStmt = conn.prepareStatement("SELECT * FROM dailyshop_profile WHERE owner_uuid = ?");
            pStmt.setString(1, playerUUID.toString());
            ResultSet rs = pStmt.executeQuery();
            if (rs.next()) {
                return new ProfileSchema(
                        playerUUID,
                        gson.fromJson(rs.getString("shopping_modes"), new TypeToken<Map<String, ShoppingMode>>() {}.getType())
                );
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public void insertSchema(@NotNull ProfileSchema schema) {
        try (Connection conn = HyphaShop.DATABASE_FACTORY.getConnection()) {
            PreparedStatement pStmt = conn.prepareStatement("REPLACE INTO dailyshop_profile VALUES (?,?)");
            pStmt.setString(1, schema.ownerUUID().toString());
            pStmt.setString(2, gson.toJson(schema.shoppingModes(), new TypeToken<Map<String, ShoppingMode>>() {}.getType()));
            pStmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void updateSchema(@NotNull ProfileSchema schema) {
        try (Connection conn = HyphaShop.DATABASE_FACTORY.getConnection()) {
            PreparedStatement pStmt = conn.prepareStatement("UPDATE dailyshop_profile SET shopping_modes = ? WHERE owner_uuid = ?");
            pStmt.setString(1, gson.toJson(schema.shoppingModes(), new TypeToken<Map<String, ShoppingMode>>() {}.getType()));
            pStmt.setString(2, schema.ownerUUID().toString());
            pStmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void deleteSchema(@NotNull ProfileSchema schema) {
        try (Connection conn = HyphaShop.DATABASE_FACTORY.getConnection()) {
            PreparedStatement pStmt = conn.prepareStatement("DELETE FROM dailyshop_profile WHERE owner_uuid = ?");
            pStmt.setString(1, schema.ownerUUID().toString());
            pStmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
