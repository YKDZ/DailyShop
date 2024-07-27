package cn.encmys.ykdz.forest.dailyshop.api.profile.factory;

import cn.encmys.ykdz.forest.dailyshop.api.profile.Profile;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.Nullable;

import java.util.Map;
import java.util.UUID;

public interface ProfileFactory {
    Profile buildProfile(Player player);

    @Nullable
    Profile getProfile(Player player);

    Map<UUID, Profile> getProfiles();

    void save();

    void unload();
}
