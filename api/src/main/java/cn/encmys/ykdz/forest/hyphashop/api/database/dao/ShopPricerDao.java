package cn.encmys.ykdz.forest.hyphashop.api.database.dao;

import cn.encmys.ykdz.forest.hyphashop.api.database.schema.ShopPricerSchema;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public interface ShopPricerDao {
    void initDB();
    @Nullable ShopPricerSchema querySchema(@NotNull String shopId);
    void insertSchema(@NotNull ShopPricerSchema schema);
    void updateSchema(@NotNull ShopPricerSchema schema);
    void deleteSchema(@NotNull ShopPricerSchema schema);
}
