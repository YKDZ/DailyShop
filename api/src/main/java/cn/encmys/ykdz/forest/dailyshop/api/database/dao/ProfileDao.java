package cn.encmys.ykdz.forest.dailyshop.api.database.dao;

import cn.encmys.ykdz.forest.dailyshop.api.database.schema.ProfileSchema;

import java.util.UUID;

public interface ProfileDao {
    void initDB();
    ProfileSchema querySchema(UUID playerUUID);
    void insertSchema(ProfileSchema schema);
    void updateSchema(ProfileSchema schema);
    void deleteSchema(ProfileSchema schema);
}
