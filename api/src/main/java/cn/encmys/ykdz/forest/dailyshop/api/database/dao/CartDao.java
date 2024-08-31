package cn.encmys.ykdz.forest.dailyshop.api.database.dao;

import cn.encmys.ykdz.forest.dailyshop.api.database.schema.CartSchema;

import java.util.UUID;

public interface CartDao {
    void initDB();
    CartSchema querySchema(UUID playerUUID);
    void insertSchema(CartSchema schema);
    void updateSchema(CartSchema schema);
    void deleteSchema(CartSchema schema);
}
