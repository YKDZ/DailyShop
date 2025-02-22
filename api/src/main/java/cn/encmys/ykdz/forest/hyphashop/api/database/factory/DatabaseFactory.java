package cn.encmys.ykdz.forest.hyphashop.api.database.factory;

import cn.encmys.ykdz.forest.hyphashop.api.database.dao.*;
import cn.encmys.ykdz.forest.hyphashop.api.database.factory.enums.DBType;

import java.sql.Connection;
import java.sql.SQLException;

public interface DatabaseFactory {
    void loadSQLite();

    Connection getConnection() throws SQLException;

    CartDao getCartDao();

    ProductStockDao getProductStockDao();

    ProfileDao getProfileDao();

    SettlementLogDao getSettlementLogDao();

    ShopCashierDao getShopCashierDao();

    ShopPricerDao getShopPricerDao();

    ShopStockerDao getShopStockerDao();

    ShopCounterDao getShopCounterDao();

    DBType getDbType();
}
