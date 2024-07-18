package cn.encmys.ykdz.forest.dailyshop.database;

import cn.encmys.ykdz.forest.dailyshop.api.DailyShop;
import cn.encmys.ykdz.forest.dailyshop.api.database.Database;
import cn.encmys.ykdz.forest.dailyshop.api.database.schema.ProductData;
import cn.encmys.ykdz.forest.dailyshop.api.database.schema.ShopData;
import cn.encmys.ykdz.forest.dailyshop.api.price.PricePair;
import cn.encmys.ykdz.forest.dailyshop.api.product.Product;
import cn.encmys.ykdz.forest.dailyshop.api.product.stock.ProductStock;
import cn.encmys.ykdz.forest.dailyshop.api.shop.Shop;
import cn.encmys.ykdz.forest.dailyshop.api.shop.cashier.log.SettlementLog;
import cn.encmys.ykdz.forest.dailyshop.api.shop.cashier.log.enums.SettlementLogType;
import cn.encmys.ykdz.forest.dailyshop.price.PricePairImpl;
import cn.encmys.ykdz.forest.dailyshop.product.stock.ProductStockImpl;
import cn.encmys.ykdz.forest.dailyshop.shop.cashier.log.SettlementLogImpl;
import cn.encmys.ykdz.forest.dailyshop.api.utils.LogUtils;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.sqlite.SQLiteDataSource;

import java.io.File;
import java.sql.*;
import java.util.Date;
import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class SQLiteDatabase implements Database {
    private static final Gson gson = new GsonBuilder().excludeFieldsWithoutExposeAnnotation().create();
    private static final String path = DailyShop.INSTANCE.getDataFolder() + "/data/database.db";
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
                    CREATE TABLE IF NOT EXISTS dailyshop_product (
                        id TEXT NOT NULL PRIMARY KEY,
                        stock_data TEXT
                    )""");
            stmt.execute("""
                    CREATE TABLE IF NOT EXISTS dailyshop_shop (
                        id TEXT NOT NULL PRIMARY KEY,
                        listed_products TEXT NOT NULL,
                        cached_prices TEXT NOT NULL,
                        last_restocking INTEGER NOT NULL
                    )""");
            stmt.execute("""
                    CREATE TABLE IF NOT EXISTS dailyshop_settlement_logs (
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
                        FOREIGN KEY (shop_id) REFERENCES dailyshop_shop (id)
                    );""");
        } catch (SQLException e) {
            e.fillInStackTrace();
        }
    }

    @Override
    public void saveProductData(@NotNull List<Product> data) {
        CompletableFuture.runAsync(() -> {
            try (Connection conn = dataSource.getConnection();
                 PreparedStatement stmt = conn.prepareStatement("REPLACE INTO dailyshop_product (id, stock_data) VALUES (?, ?)")
            ) {
                conn.setAutoCommit(false);
                for (Product product : data) {
                    stmt.setString(1, product.getId());
                    stmt.setString(2, gson.toJson(product.getProductStock()));
                    stmt.addBatch();
                }
                stmt.executeBatch();
                conn.commit();
            } catch (SQLException e) {
                e.printStackTrace();
                LogUtils.error("Error saving product data: " + e.getMessage());
            }
        });
    }

    @Override
    public CompletableFuture<ProductData> queryProductData(@NotNull String id) {
        return CompletableFuture.supplyAsync(() -> {
            try (Connection conn = dataSource.getConnection();
                 PreparedStatement stmt = conn.prepareStatement("SELECT id, stock_data FROM dailyshop_product WHERE id = ?")) {
                stmt.setString(1, id);
                try (ResultSet rs = stmt.executeQuery()) {
                    if (rs.next()) {
                        String stockData = rs.getString("stock_data");
                        ProductStock stock = gson.fromJson(stockData, ProductStockImpl.class);
                        return new ProductData(id, stock);
                    }
                }
            } catch (SQLException e) {
                e.printStackTrace();
                LogUtils.error("Error querying product data for id " + id + ": " + e.getMessage());
            }
            return null;
        });
    }

    @Override
    public void saveShopData(@NotNull List<Shop> data) {
        CompletableFuture.runAsync(() -> {
            try (Connection conn = dataSource.getConnection();
                 PreparedStatement stmt = conn.prepareStatement("REPLACE INTO dailyshop_shop (id, listed_products, cached_prices, last_restocking) VALUES (?, ?, ?, ?)")
            ) {
                conn.setAutoCommit(false);
                for (Shop shop : data) {
                    stmt.setString(1, shop.getId());
                    stmt.setString(2, gson.toJson(shop.getShopStocker().getListedProducts()));
                    stmt.setString(3, gson.toJson(shop.getShopPricer().getCachedPrices()));
                    stmt.setLong(4, shop.getShopStocker().getLastRestocking());
                    stmt.addBatch();
                }
                stmt.executeBatch();
                conn.commit();
            } catch (SQLException e) {
                e.printStackTrace();
                LogUtils.error("Error saving shop data: " + e.getMessage());
            }
        });
    }

    @Override
    public CompletableFuture<ShopData> queryShopData(@NotNull String id) {
        return CompletableFuture.supplyAsync(() -> {
            try (Connection conn = dataSource.getConnection();
                 PreparedStatement stmt = conn.prepareStatement("SELECT id, listed_products, cached_prices, last_restocking FROM dailyshop_shop WHERE id = ?")) {
                stmt.setString(1, id);
                try (ResultSet rs = stmt.executeQuery()) {
                    if (rs.next()) {
                        List<String> listedProducts = gson.fromJson(rs.getString("listed_products"), new TypeToken<List<String>>() {}.getType());
                        Map<String, PricePair> cachedPrices = gson.fromJson(rs.getString("cached_prices"), new TypeToken<Map<String, PricePairImpl>>() {}.getType());
                        long lastRestocking = rs.getLong("last_restocking");

                        return new ShopData(id, listedProducts, cachedPrices, lastRestocking);
                    }
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }
            return null;
        });
    }

    @Override
    public void insertSettlementLog(@NotNull String shopId, @NotNull SettlementLog log) {
        CompletableFuture.runAsync(() -> {
            String sql = "INSERT INTO dailyshop_settlement_logs (shop_id, customer, type, transition_time, price, ordered_product_ids, ordered_product_names, ordered_product_stacks, total_stack) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)";
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
                e.fillInStackTrace();
            }
        });
    }

    @Override
    public CompletableFuture<List<SettlementLog>> queryLogs(@NotNull String shopId, @Nullable UUID customer, @Nullable String productId, double timeLimitInDay, int numEntries, @NotNull SettlementLogType... types) {
        return CompletableFuture.supplyAsync(() -> {
            List<SettlementLog> logs = new ArrayList<>();
            String typeList = Stream.of(types)
                    .map(Enum::name)
                    .collect(Collectors.joining("','", "'", "'"));
            long sevenDaysAgo = System.currentTimeMillis() - (long) (timeLimitInDay * 24 * 60 * 60 * 1000);
            Timestamp sevenDaysAgoTimestamp = new Timestamp(sevenDaysAgo);

            String sql = "SELECT type, transition_time, price, ordered_product_ids, ordered_product_names, ordered_product_stacks, total_stack FROM dailyshop_settlement_logs WHERE shop_id = ? AND type IN (" + typeList + ") AND transition_time > ? ";
            if (customer != null) {
                sql += "AND customer = ? ";
            }
            sql += "LIMIT ?";

            try (Connection conn = dataSource.getConnection()) {
                try (PreparedStatement stmt = conn.prepareStatement(sql)) {
                    stmt.setString(1, shopId);
                    stmt.setTimestamp(2, sevenDaysAgoTimestamp);
                    int paramIndex = 3;
                    if (customer != null) {
                        stmt.setString(paramIndex++, customer.toString());
                    }
                    stmt.setInt(paramIndex, numEntries);
                    try (ResultSet rs = stmt.executeQuery()) {
                        while (rs.next()) {
                            List<String> productIds = gson.fromJson(rs.getString("ordered_product_ids"), new TypeToken<List<String>>() {}.getType());
                            if (productId != null && !productIds.contains(productId)) {
                                continue; // 跳过不包含指定商品ID的日志
                            }
                            SettlementLogType type = SettlementLogType.valueOf(rs.getString("type"));
                            Date transitionTime = rs.getTimestamp("transition_time");
                            double price = rs.getDouble("price");
                            List<String> names = gson.fromJson(rs.getString("ordered_product_names"), new TypeToken<List<String>>() {}.getType());
                            List<Integer> stacks = gson.fromJson(rs.getString("ordered_product_stacks"), new TypeToken<List<Integer>>() {}.getType());
                            int totalStack = rs.getInt("total_stack");

                            logs.add(SettlementLogImpl.of(type, customer)
                                    .setTransitionTime(transitionTime)
                                    .setPrice(price)
                                    .setOrderedProductIds(productIds)
                                    .setOrderedProductNames(names)
                                    .setOrderedProductStacks(stacks)
                                    .setTotalStack(totalStack));
                        }
                    }
                }
            } catch (SQLException e) {
                e.fillInStackTrace();
            }
            return logs;
        });
    }
}