package cn.encmys.ykdz.forest.dailyshop.database;

import cn.encmys.ykdz.forest.dailyshop.api.DailyShop;
import cn.encmys.ykdz.forest.dailyshop.api.database.Database;
import cn.encmys.ykdz.forest.dailyshop.api.database.schema.ProductData;
import cn.encmys.ykdz.forest.dailyshop.api.database.schema.ProfileData;
import cn.encmys.ykdz.forest.dailyshop.api.database.schema.ShopData;
import cn.encmys.ykdz.forest.dailyshop.api.price.PricePair;
import cn.encmys.ykdz.forest.dailyshop.api.product.Product;
import cn.encmys.ykdz.forest.dailyshop.api.product.stock.ProductStock;
import cn.encmys.ykdz.forest.dailyshop.api.profile.Profile;
import cn.encmys.ykdz.forest.dailyshop.api.profile.enums.ShoppingMode;
import cn.encmys.ykdz.forest.dailyshop.api.shop.Shop;
import cn.encmys.ykdz.forest.dailyshop.api.shop.cashier.log.SettlementLog;
import cn.encmys.ykdz.forest.dailyshop.api.shop.order.ShopOrder;
import cn.encmys.ykdz.forest.dailyshop.api.shop.order.enums.OrderType;
import cn.encmys.ykdz.forest.dailyshop.api.utils.LogUtils;
import cn.encmys.ykdz.forest.dailyshop.price.PricePairImpl;
import cn.encmys.ykdz.forest.dailyshop.product.stock.ProductStockImpl;
import cn.encmys.ykdz.forest.dailyshop.shop.cashier.log.SettlementLogImpl;
import cn.encmys.ykdz.forest.dailyshop.shop.order.ShopOrderImpl;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import org.bukkit.scheduler.BukkitScheduler;
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
    private static final BukkitScheduler scheduler = DailyShop.INSTANCE.getServer().getScheduler();
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
                    CREATE TABLE IF NOT EXISTS dailyshop_profile (
                        owner_uuid TEXT NOT NULL PRIMARY KEY,
                        cart TEXT NOT NULL,
                        cart_mode TEXT NOT NULL,
                        shopping_modes TEXT NOT NULL
                    );""");
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
                        last_restocking INTEGER NOT NULL,
                        balance REAL NOT NULL
                    )""");
            stmt.execute("""
                    CREATE TABLE IF NOT EXISTS dailyshop_settlement_log (
                        id INTEGER NOT NULL PRIMARY KEY AUTOINCREMENT,
                        customer TEXT NOT NULL,
                        shop_id TEXT NOT NULL,
                        type TEXT NOT NULL,
                        transition_time DATETIME NOT NULL,
                        price DOUBLE NOT NULL,
                        ordered_product_ids TEXT NOT NULL,
                        ordered_product_names TEXT NOT NULL,
                        ordered_product_stacks TEXT NOT NULL,
                        FOREIGN KEY (shop_id) REFERENCES dailyshop_shop (id)
                    );""");
        } catch (SQLException e) {
            e.fillInStackTrace();
        }
    }

    @Override
    public void saveProfileData(@NotNull List<Profile> data) {
        try (Connection conn = dataSource.getConnection();
             PreparedStatement stmt = conn.prepareStatement("REPLACE INTO dailyshop_profile (owner_uuid, cart, cart_mode, shopping_modes) VALUES (?, ?, ?, ?)")
        ) {
            conn.setAutoCommit(false);
            for (Profile profile : data) {
                stmt.setString(1, profile.getOwner().getUniqueId().toString());
                stmt.setString(2, gson.toJson(profile.getCart(), new TypeToken<Map<String, ShopOrderImpl>>() {
                }.getType()));
                stmt.setString(3, gson.toJson(profile.getCart().getMode()));
                stmt.setString(4, gson.toJson(profile.getShoppingModes(), new TypeToken<Map<String, ShoppingMode>>() {
                }.getType()));
                stmt.addBatch();
            }
            stmt.executeBatch();
            conn.commit();
        } catch (SQLException e) {
            LogUtils.error("Error saving profile data: " + e.getMessage());
        }
    }

    @Override
    public CompletableFuture<ProfileData> queryProfileData(@NotNull UUID ownerUUID) {
        return CompletableFuture.supplyAsync(() -> {
            try (Connection conn = dataSource.getConnection();
                 PreparedStatement stmt = conn.prepareStatement("SELECT owner_uuid, cart, cart_mode, shopping_modes FROM dailyshop_profile WHERE owner_uuid = ?")) {
                stmt.setString(1, ownerUUID.toString());
                try (ResultSet rs = stmt.executeQuery()) {
                    if (rs.next()) {
                        Map<String, ShopOrder> cart = gson.fromJson(rs.getString("cart"), new TypeToken<Map<String, ShopOrderImpl>>() {
                        }.getType());
                        OrderType cartMode = gson.fromJson(rs.getString("cart_mode"), OrderType.class);
                        Map<String, ShoppingMode> shoppingModes = gson.fromJson(rs.getString("shopping_modes"), new TypeToken<Map<String, ShoppingMode>>() {
                        }.getType());
                        return new ProfileData(ownerUUID, cart, cartMode, shoppingModes);
                    }
                }
            } catch (SQLException e) {
                LogUtils.error("Error querying profile data for owner uuid " + ownerUUID + ": " + e.getMessage());
            }
            return null;
        });
    }

    @Override
    public void saveProductData(@NotNull List<Product> data) {
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
            LogUtils.error("Error saving product data: " + e.getMessage());
        }
    }

    @Override
    public CompletableFuture<ProductData> queryProductData(@NotNull String id) {
        return CompletableFuture.supplyAsync(() -> {
            try (Connection conn = dataSource.getConnection();
                 PreparedStatement stmt = conn.prepareStatement("SELECT id, stock_data FROM dailyshop_product WHERE id = ?")) {
                stmt.setString(1, id);
                try (ResultSet rs = stmt.executeQuery()) {
                    if (rs.next()) {
                        ProductStock stock = gson.fromJson(rs.getString("stock_data"), ProductStockImpl.class);
                        return new ProductData(id, stock);
                    }
                }
            } catch (SQLException e) {
                LogUtils.error("Error querying product data for id " + id + ": " + e.getMessage());
            }
            return null;
        });
    }

    @Override
    public void saveShopData(@NotNull List<Shop> data) {
        try (Connection conn = dataSource.getConnection();
             PreparedStatement stmt = conn.prepareStatement("REPLACE INTO dailyshop_shop (id, listed_products, cached_prices, last_restocking, balance) VALUES (?, ?, ?, ?, ?)")
        ) {
            conn.setAutoCommit(false);
            for (Shop shop : data) {
                stmt.setString(1, shop.getId());
                stmt.setString(2, gson.toJson(shop.getShopStocker().getListedProducts()));
                stmt.setString(3, gson.toJson(shop.getShopPricer().getCachedPrices()));
                stmt.setLong(4, shop.getShopStocker().getLastRestocking());
                stmt.setDouble(5, shop.getShopCashier().getBalance());
                stmt.addBatch();
            }
            stmt.executeBatch();
            conn.commit();
        } catch (SQLException e) {
            LogUtils.error("Error saving shop data: " + e.getMessage());
        }
    }

    @Override
    public CompletableFuture<ShopData> queryShopData(@NotNull String id) {
        return CompletableFuture.supplyAsync(() -> {
            try (Connection conn = dataSource.getConnection();
                 PreparedStatement stmt = conn.prepareStatement("SELECT id, listed_products, cached_prices, last_restocking, balance FROM dailyshop_shop WHERE id = ?")) {
                stmt.setString(1, id);
                try (ResultSet rs = stmt.executeQuery()) {
                    if (rs.next()) {
                        List<String> listedProducts = gson.fromJson(rs.getString("listed_products"), new TypeToken<List<String>>() {
                        }.getType());
                        Map<String, PricePair> cachedPrices = gson.fromJson(rs.getString("cached_prices"), new TypeToken<Map<String, PricePairImpl>>() {
                        }.getType());
                        long lastRestocking = rs.getLong("last_restocking");
                        double balance = rs.getDouble("balance");

                        return new ShopData(id, listedProducts, cachedPrices, lastRestocking, balance);
                    }
                }
            } catch (SQLException e) {
                LogUtils.error("Error querying shop data for id " + id + ": " + e.getMessage());
            }
            return null;
        });
    }

    @Override
    public void insertSettlementLog(@NotNull String shopId, @NotNull SettlementLog log) {
        scheduler.runTaskAsynchronously(DailyShop.INSTANCE, () -> {
            String sql = "INSERT INTO dailyshop_settlement_log (customer, shop_id, type, transition_time, price, ordered_product_ids, ordered_product_names, ordered_product_stacks) VALUES (?, ?, ?, ?, ?, ?, ?, ?)";
            try (Connection conn = dataSource.getConnection();
                 PreparedStatement stmt = conn.prepareStatement(sql)) {
                stmt.setString(1, log.getCustomerUUID().toString());
                stmt.setString(2, shopId);
                stmt.setString(3, log.getType().name());
                stmt.setTimestamp(4, Timestamp.from(log.getTransitionTime().toInstant()));
                stmt.setDouble(5, log.getTotalPrice());
                stmt.setString(6, gson.toJson(log.getOrderedProductIds()));
                stmt.setString(7, gson.toJson(log.getOrderedProductNames()));
                stmt.setString(8, gson.toJson(log.getOrderedProductStacks()));
                stmt.executeUpdate();
            } catch (SQLException e) {
                e.fillInStackTrace();
            }
        });
    }

    @Override
    public CompletableFuture<List<SettlementLog>> queryLogs(@Nullable String shopId, @Nullable UUID customer, @Nullable String productId, double timeLimitInDay, int numEntries, @NotNull OrderType... types) {
        return CompletableFuture.supplyAsync(() -> {
            List<SettlementLog> logs = new ArrayList<>();
            String typeList = Stream.of(types)
                    .map(Enum::name)
                    .collect(Collectors.joining("','", "'", "'"));
            long timeLimitMillis = (long) (timeLimitInDay * 24 * 60 * 60 * 1000);
            Timestamp timeLimitTimestamp = new Timestamp(System.currentTimeMillis() - timeLimitMillis);

            StringBuilder sql = new StringBuilder("SELECT type, transition_time, price, ordered_product_ids, ordered_product_names, ordered_product_stacks FROM dailyshop_settlement_log WHERE transition_time > ? ");
            if (shopId != null) {
                sql.append("AND shop_id = ? ");
            }
            sql.append("AND type IN (").append(typeList).append(") ");
            if (customer != null) {
                sql.append("AND customer = ? ");
            }
            sql.append("ORDER BY transition_time DESC "); // 按 transition_time 降序排序
            sql.append("LIMIT ?");

            try (Connection conn = dataSource.getConnection();
                 PreparedStatement stmt = conn.prepareStatement(sql.toString())) {

                int paramIndex = 1;
                stmt.setTimestamp(paramIndex++, timeLimitTimestamp);
                if (shopId != null) {
                    stmt.setString(paramIndex++, shopId);
                }
                if (customer != null) {
                    stmt.setString(paramIndex++, customer.toString());
                }
                stmt.setInt(paramIndex, numEntries);

                try (ResultSet rs = stmt.executeQuery()) {
                    while (rs.next()) {
                        List<String> productIds = gson.fromJson(rs.getString("ordered_product_ids"), new TypeToken<List<String>>() {
                        }.getType());
                        if (productId != null && !productIds.contains(productId)) {
                            continue; // 跳过不包含指定商品ID的日志
                        }
                        OrderType type = OrderType.valueOf(rs.getString("type"));
                        Date transitionTime = rs.getTimestamp("transition_time");
                        double price = rs.getDouble("price");
                        List<String> names = gson.fromJson(rs.getString("ordered_product_names"), new TypeToken<List<String>>() {
                        }.getType());
                        List<Integer> stacks = gson.fromJson(rs.getString("ordered_product_stacks"), new TypeToken<List<Integer>>() {
                        }.getType());

                        logs.add(SettlementLogImpl.of(type, customer)
                                .setTransitionTime(transitionTime)
                                .setTotalPrice(price)
                                .setOrderedProductIds(productIds)
                                .setOrderedProductNames(names)
                                .setOrderedProductStacks(stacks));
                    }
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }
            return logs;
        });
    }

    @Override
    public CompletableFuture<List<SettlementLog>> queryLogs(@Nullable String shopId, @Nullable UUID customer, @Nullable String productId, double timeLimitInDay, int pageIndex, int pageSize, @NotNull OrderType... types) {
        return CompletableFuture.supplyAsync(() -> {
            List<SettlementLog> logs = new ArrayList<>();
            String typeList = Stream.of(types)
                    .map(Enum::name)
                    .collect(Collectors.joining("','", "'", "'"));
            long timeLimitMillis = (long) (timeLimitInDay * 24 * 60 * 60 * 1000);
            Timestamp timeLimitTimestamp = new Timestamp(System.currentTimeMillis() - timeLimitMillis);

            StringBuilder sql = new StringBuilder("SELECT type, transition_time, price, ordered_product_ids, ordered_product_names, ordered_product_stacks FROM dailyshop_settlement_log WHERE transition_time > ? ");
            if (shopId != null) {
                sql.append("AND shop_id = ? ");
            }
            sql.append("AND type IN (").append(typeList).append(") ");
            if (customer != null) {
                sql.append("AND customer = ? ");
            }
            sql.append("ORDER BY transition_time DESC "); // 按 transition_time 降序排序
            sql.append("LIMIT ? OFFSET ?");

            try (Connection conn = dataSource.getConnection();
                 PreparedStatement stmt = conn.prepareStatement(sql.toString())) {

                int paramIndex = 1;
                stmt.setTimestamp(paramIndex++, timeLimitTimestamp);
                if (shopId != null) {
                    stmt.setString(paramIndex++, shopId);
                }
                if (customer != null) {
                    stmt.setString(paramIndex++, customer.toString());
                }
                stmt.setInt(paramIndex++, pageSize); // 设置每页的条目数量
                stmt.setInt(paramIndex, (pageIndex - 1) * pageSize); // 设置偏移量

                try (ResultSet rs = stmt.executeQuery()) {
                    while (rs.next()) {
                        List<String> productIds = gson.fromJson(rs.getString("ordered_product_ids"), new TypeToken<List<String>>() {
                        }.getType());
                        if (productId != null && !productIds.contains(productId)) {
                            continue; // 跳过不包含指定商品ID的日志
                        }
                        OrderType type = OrderType.valueOf(rs.getString("type"));
                        Date transitionTime = rs.getTimestamp("transition_time");
                        double price = rs.getDouble("price");
                        List<String> names = gson.fromJson(rs.getString("ordered_product_names"), new TypeToken<List<String>>() {
                        }.getType());
                        List<Integer> stacks = gson.fromJson(rs.getString("ordered_product_stacks"), new TypeToken<List<Integer>>() {
                        }.getType());

                        logs.add(SettlementLogImpl.of(type, customer)
                                .setTransitionTime(transitionTime)
                                .setTotalPrice(price)
                                .setOrderedProductIds(productIds)
                                .setOrderedProductNames(names)
                                .setOrderedProductStacks(stacks));
                    }
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }
            return logs;
        });
    }
}