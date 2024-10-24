package cn.encmys.ykdz.forest.dailyshop.database.factory;

import cn.encmys.ykdz.forest.dailyshop.api.DailyShop;
import cn.encmys.ykdz.forest.dailyshop.api.config.Config;
import cn.encmys.ykdz.forest.dailyshop.api.database.dao.*;
import cn.encmys.ykdz.forest.dailyshop.api.database.factory.DatabaseFactory;
import cn.encmys.ykdz.forest.dailyshop.api.database.factory.enums.DBType;
import cn.encmys.ykdz.forest.dailyshop.database.dao.sqlite.*;
import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;

import javax.sql.DataSource;
import java.io.File;
import java.sql.Connection;
import java.sql.SQLException;

public class DatabaseFactoryImpl implements DatabaseFactory {
    private DataSource dataSource;
    private DBType dbType;
    private CartDao cartDao;
    private ProductStockDao productStockDao;
    private ProfileDao profileDao;
    private SettlementLogDao settlementLogDao;
    private ShopCashierDao shopCashierDao;
    private ShopPricerDao shopPricerDao;
    private ShopStockerDao shopStockerDao;

    public DatabaseFactoryImpl() {
        load();
    }

    public void load() {
        if (Config.database_sqlite_enabled) {
            dbType = DBType.SQLITE;
            loadSQLite();
        } else if (Config.database_mysql_enabled) {
            dbType = DBType.MYSQL;
            // TODO MySQL 实现
        }
    }

    @Override
    public void loadSQLite() {
        HikariConfig config = new HikariConfig();
        String path = DailyShop.INSTANCE.getDataFolder() + "/data/database.db";
        File dbFile = new File(path);
        if (!dbFile.exists()) {
            DailyShop.INSTANCE.saveResource("data/database.db", false);
        }
        config.setJdbcUrl("jdbc:sqlite:" + path);
        dataSource = new HikariDataSource(config);
    }

    @Override
    public Connection getConnection() throws SQLException {
        return dataSource.getConnection();
    }

    @Override
    public CartDao getCartDao() {
        if (cartDao == null) {
            switch (dbType) {
                case SQLITE -> cartDao = new SQLiteCartDao();
            }
        }
        return cartDao;
    }

    @Override
    public ProductStockDao getProductStockDao() {
        if (productStockDao == null) {
            switch (dbType) {
                case SQLITE -> productStockDao = new SQLiteProductStockDao();
            }
        }
        return productStockDao;
    }

    @Override
    public ProfileDao getProfileDao() {
        if (profileDao == null) {
            switch (dbType) {
                case SQLITE -> profileDao = new SQLiteProfileDao();
            }
        }
        return profileDao;
    }

    @Override
    public SettlementLogDao getSettlementLogDao() {
        if (settlementLogDao == null) {
            switch (dbType) {
                case SQLITE -> settlementLogDao = new SQLiteSettlementLogDao();
            }
        }
        return settlementLogDao;
    }

    @Override
    public ShopCashierDao getShopCashierDao() {
        if (shopCashierDao == null) {
            switch (dbType) {
                case SQLITE -> shopCashierDao = new SQLiteShopCashierDao();
            }
        }
        return shopCashierDao;
    }

    @Override
    public ShopPricerDao getShopPricerDao() {
        if (shopPricerDao == null) {
            switch (dbType) {
                case SQLITE -> shopPricerDao = new SQLiteShopPricerDao();
            }
        }
        return shopPricerDao;
    }

    @Override
    public ShopStockerDao getShopStockerDao() {
        if (shopStockerDao == null) {
            switch (dbType) {
                case SQLITE -> shopStockerDao = new SQLiteShopStockerDao();
            }
        }
        return shopStockerDao;
    }

    @Override
    public DBType getDbType() {
        return dbType;
    }
}
