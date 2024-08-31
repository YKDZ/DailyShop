package cn.encmys.ykdz.forest.dailyshop.api.database.dao;

import cn.encmys.ykdz.forest.dailyshop.api.database.schema.ShopCashierSchema;

public interface ShopCashierDao {
    void initDB();
    ShopCashierSchema querySchema(String shopId);
    void insertSchema(ShopCashierSchema schema);
    void updateSchema(ShopCashierSchema schema);
    void deleteSchema(ShopCashierSchema schema);
}
