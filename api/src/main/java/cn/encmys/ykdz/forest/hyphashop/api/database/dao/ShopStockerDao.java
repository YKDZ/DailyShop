package cn.encmys.ykdz.forest.hyphashop.api.database.dao;

import cn.encmys.ykdz.forest.hyphashop.api.database.schema.ShopStockerSchema;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public interface ShopStockerDao {
    void initDB();
    @Nullable ShopStockerSchema querySchema(@NotNull String shopId);
    void insertSchema(@NotNull ShopStockerSchema schema);
    void updateSchema(@NotNull ShopStockerSchema schema);
    void deleteSchema(@NotNull ShopStockerSchema schema);
}
