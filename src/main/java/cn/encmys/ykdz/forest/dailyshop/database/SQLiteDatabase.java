package cn.encmys.ykdz.forest.dailyshop.database;

import cn.encmys.ykdz.forest.dailyshop.DailyShop;
import cn.encmys.ykdz.forest.dailyshop.api.database.Database;
import cn.encmys.ykdz.forest.dailyshop.price.PricePair;
import cn.encmys.ykdz.forest.dailyshop.shop.Shop;
import cn.encmys.ykdz.forest.dailyshop.shop.cashier.log.SettlementLog;
import cn.encmys.ykdz.forest.dailyshop.shop.cashier.log.enums.SettlementLogType;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.sqlite.SQLiteDataSource;

import java.io.File;
import java.sql.*;
import java.util.Date;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class SQLiteDatabase implements Database {
    private static Gson gson = new Gson();
    private static String path = DailyShop.INSTANCE.getDataFolder() + "/data/database.db";
    private final SQLiteDataSource dataSource;

    public SQLiteDatabase() {
        File dbFile = new File(path);

        if (!dbFile.exists()) {
            dbFile.getParentFile().mkdirs();
            DailyShop.INSTANCE.saveResource("data/database.db", false);
        }

        dataSource = new SQLiteDataSource();
        dataSource.setUrl("jdbc:sqlite:" + path);

        try (Connection conn = dataSource.getConnection();
             Statement stmt = conn.createStatement()) {
            stmt.execute("""
                    CREATE TABLE IF NOT EXISTS shop_data (
                        id TEXT NOT NULL PRIMARY KEY,
                        listed_products TEXT NOT NULL,
                        cached_prices TEXT NOT NULL,
                        last_restocking INTEGER NOT NULL
                    )""");
            stmt.execute("""
                    CREATE TABLE IF NOT EXISTS settlement_logs (
                        id INTEGER NOT NULL PRIMARY KEY AUTOINCREMENT,
                        customer TEXT NOT NULL,
                        shop_id TEXT NOT NULL,
                        type TEXT NOT NULL,
                        transition_time DATETIME NOT NULL,
                        price DOUBLE NOT NULL,
                        ordered_product_ids TEXT NOT NULL,
                        ordered_product_names TEXT NOT NULL,
                        ordered_product_stacks TEXT NOT NULL,
                        total_stack INTEGER NOT NULL,
                        FOREIGN KEY (shop_id) REFERENCES shop_data (id)
                    );""");
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void saveShopData(@NotNull Map<String, Shop> data) {
        try (Connection conn = dataSource.getConnection();
             PreparedStatement stmt = conn.prepareStatement("REPLACE INTO shop_data (id, listed_products, cached_prices, last_restocking) VALUES (?, ?, ?, ?)")) {
            for (Map.Entry<String, Shop> entry : data.entrySet()) {
                Shop shop = entry.getValue();
                stmt.setString(1, entry.getKey());
                stmt.setString(2, gson.toJson(shop.getListedProducts()));
                stmt.setString(3, gson.toJson(shop.getShopPricer().getCachedPrices()));
                stmt.setLong(4, shop.getLastRestocking());
                stmt.addBatch();
            }
            stmt.executeBatch();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Override
    @Nullable
    public List<String> queryShopListedProducts(@NotNull String id) {
        String sql = "SELECT id, listed_products, cached_prices, last_restocking FROM shop_data WHERE id = ?";
        try (Connection conn = dataSource.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, id);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return gson.fromJson(rs.getString("listed_products"), new TypeToken<List<String>>() {}.getType());
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return new ArrayList<>();
    }

    @Override
    public Map<String, PricePair> queryShopCachedPrices(@NotNull String id) {
        String sql = "SELECT id, listed_products, cached_prices, last_restocking FROM shop_data WHERE id = ?";
        try (Connection conn = dataSource.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, id);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return gson.fromJson(rs.getString("cached_prices"), new TypeToken<Map<String, PricePair>>() {}.getType());
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return new HashMap<>();
    }

    @Override
    public long queryShopLastRestocking(@NotNull String id) {
        String sql = "SELECT id, listed_products, cached_prices, last_restocking FROM shop_data WHERE id = ?";
        try (Connection conn = dataSource.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, id);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return rs.getLong("last_restocking");
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return System.currentTimeMillis();
    }

    @Override
    public void insertSettlementLog(@NotNull String shopId, @NotNull SettlementLog log) {
        String sql = "INSERT INTO settlement_logs (shop_id, customer, type, transition_time, price, ordered_product_ids, ordered_product_names, ordered_product_stacks, total_stack) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)";
        try (Connection conn = dataSource.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, shopId);
            stmt.setString(2, log.getCustomerUUID().toString());
            stmt.setString(3, log.getType().name());
            stmt.setTimestamp(4, Timestamp.from(log.getTransitionTime().toInstant()));
            stmt.setDouble(5, log.getPrice());
            stmt.setString(6, gson.toJson(log.getOrderedProductIds()));
            stmt.setString(7, gson.toJson(log.getOrderedProductNames()));
            stmt.setString(8, gson.toJson(log.getOrderedProductStacks()));
            stmt.setInt(9, log.getTotalStack());
            stmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Override
    public int queryHistoryAmountFromLogs(String shopId, String productId, double timeLimitInDay, int numEntries, SettlementLogType... types) {
        int totalSales = 0;
        String typeList = Stream.of(types)
                .map(Enum::name)
                .collect(Collectors.joining("','", "'", "'"));
        long sevenDaysAgo = System.currentTimeMillis() - (long) (timeLimitInDay * 24 * 60 * 60 * 1000);
        Timestamp sevenDaysAgoTimestamp = new Timestamp(sevenDaysAgo);
        String sql = "SELECT ordered_product_ids, ordered_product_stacks, total_stack FROM settlement_logs WHERE shop_id = ? AND type IN (" + typeList + ") AND transition_time > ? LIMIT ?";

        try (Connection conn = dataSource.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)
        ) {
            stmt.setString(1, shopId);
            stmt.setTimestamp(2, sevenDaysAgoTimestamp);
            stmt.setInt(3, numEntries);

            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    List<String> productIds = gson.fromJson(rs.getString("ordered_product_ids"), new TypeToken<List<String>>() {}.getType());
                    List<Integer> productStacks = gson.fromJson(rs.getString("ordered_product_stacks"), new TypeToken<List<Integer>>() {}.getType());
                    int totalStack = rs.getInt("total_stack");

                    for (int i = 0; i < productIds.size(); i++) {
                        if (productIds.get(i).equals(productId)) {
                            totalSales += productStacks.get(i) * totalStack;
                        }
                    }
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return totalSales;
    }

    @Override
    public List<SettlementLog> queryLogInOrder(@NotNull String shopId, UUID customer, double timeLimitInDay, int numEntries, SettlementLogType... types) {
        List<SettlementLog> logs = new ArrayList<>();
        String typeList = Stream.of(types)
                .map(Enum::name)
                .collect(Collectors.joining("','", "'", "'"));
        long sevenDaysAgo = System.currentTimeMillis() - (long) (timeLimitInDay * 24 * 60 * 60 * 1000);
        Timestamp sevenDaysAgoTimestamp = new Timestamp(sevenDaysAgo);
        String sql = "SELECT type, transition_time, price, ordered_product_ids, ordered_product_names, ordered_product_stacks, total_stack FROM settlement_logs WHERE shop_id = ? AND customer = ? AND type IN (" + typeList + ") AND transition_time > ? LIMIT ?";

        try (Connection conn = dataSource.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)
        ) {
            stmt.setString(1, shopId);
            stmt.setString(2, customer.toString());
            stmt.setTimestamp(3, sevenDaysAgoTimestamp);
            stmt.setInt(4, numEntries);
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    SettlementLogType type = SettlementLogType.valueOf(rs.getString("type"));
                    Date transitionTime = rs.getTimestamp("transition_time");
                    double price = rs.getDouble("price");
                    List<String> ids = gson.fromJson(rs.getString("ordered_product_ids"), new TypeToken<List<String>>() {}.getType());
                    List<String> names = gson.fromJson(rs.getString("ordered_product_names"), new TypeToken<List<String>>() {}.getType());
                    List<Integer> stacks = gson.fromJson(rs.getString("ordered_product_stacks"), new TypeToken<List<Integer>>() {}.getType());
                    int totalStack = rs.getInt("total_stack");

                    logs.add(SettlementLog.of(type, customer)
                            .setTransitionTime(transitionTime)
                            .setPrice(price)
                            .setOrderedProductIds(ids)
                            .setOrderedProductNames(names)
                            .setOrderedProductStacks(stacks)
                            .setTotalStack(totalStack));
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return logs;
    }
}