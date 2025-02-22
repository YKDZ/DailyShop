package cn.encmys.ykdz.forest.hyphashop.api.database.dao;

import cn.encmys.ykdz.forest.hyphashop.api.database.schema.ProductStockSchema;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public interface ProductStockDao {
    void initDB();
    @Nullable ProductStockSchema querySchema(@NotNull String productId);
    void insertSchema(@NotNull ProductStockSchema schema);
    void updateSchema(@NotNull ProductStockSchema schema);
    void deleteSchema(@NotNull ProductStockSchema schema);
}
