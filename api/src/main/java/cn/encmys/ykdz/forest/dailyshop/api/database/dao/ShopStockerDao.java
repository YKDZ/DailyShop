package cn.encmys.ykdz.forest.dailyshop.api.database.dao;

import cn.encmys.ykdz.forest.dailyshop.api.database.schema.ShopStockerSchema;

public interface ShopStockerDao {
    void initDB();
    ShopStockerSchema querySchema(String shopId);
    void insertSchema(ShopStockerSchema schema);
    void updateSchema(ShopStockerSchema schema);
    void deleteSchema(ShopStockerSchema schema);
}
