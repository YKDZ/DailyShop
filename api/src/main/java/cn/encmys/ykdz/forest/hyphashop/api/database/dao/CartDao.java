package cn.encmys.ykdz.forest.hyphashop.api.database.dao;

import cn.encmys.ykdz.forest.hyphashop.api.database.schema.CartSchema;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.UUID;

public interface CartDao {
    void initDB();
    @Nullable CartSchema querySchema(@NotNull UUID playerUUID);
    void insertSchema(@NotNull CartSchema schema);
    void updateSchema(@NotNull CartSchema schema);
    void deleteSchema(@NotNull CartSchema schema);
}
