package cn.encmys.ykdz.forest.hyphashop.api.database.dao;

import cn.encmys.ykdz.forest.hyphashop.api.database.schema.ShopCashierSchema;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public interface ShopCashierDao {
    void initDB();
    @Nullable ShopCashierSchema querySchema(@NotNull String shopId);
    void insertSchema(@NotNull ShopCashierSchema schema);
    void updateSchema(@NotNull ShopCashierSchema schema);
    void deleteSchema(@NotNull ShopCashierSchema schema);
}
