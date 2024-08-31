package cn.encmys.ykdz.forest.dailyshop.api.database.dao;

import cn.encmys.ykdz.forest.dailyshop.api.database.schema.ProductStockSchema;

public interface ProductStockDao {
    void initDB();
    ProductStockSchema querySchema(String productId);
    void insertSchema(ProductStockSchema schema);
    void updateSchema(ProductStockSchema schema);
    void deleteSchema(ProductStockSchema schema);
}
