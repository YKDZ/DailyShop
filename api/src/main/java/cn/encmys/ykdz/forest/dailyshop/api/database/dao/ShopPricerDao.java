package cn.encmys.ykdz.forest.dailyshop.api.database.dao;

import cn.encmys.ykdz.forest.dailyshop.api.database.schema.ShopPricerSchema;

public interface ShopPricerDao {
    void initDB();
    ShopPricerSchema querySchema(String shopId);
    void insertSchema(ShopPricerSchema schema);
    void updateSchema(ShopPricerSchema schema);
    void deleteSchema(ShopPricerSchema schema);
}
