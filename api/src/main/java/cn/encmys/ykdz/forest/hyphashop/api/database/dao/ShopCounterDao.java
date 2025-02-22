package cn.encmys.ykdz.forest.hyphashop.api.database.dao;

import cn.encmys.ykdz.forest.hyphashop.api.database.schema.ShopCounterSchema;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public interface ShopCounterDao {
    void initDB();
    @Nullable ShopCounterSchema querySchema(@NotNull String shopId);
    void insertSchema(@NotNull ShopCounterSchema schema);
    void updateSchema(@NotNull ShopCounterSchema schema);
    void deleteSchema(@NotNull ShopCounterSchema schema);
}
