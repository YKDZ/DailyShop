package cn.encmys.ykdz.forest.hyphashop.api.profile.factory;

import cn.encmys.ykdz.forest.hyphashop.api.profile.Profile;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Unmodifiable;

import java.util.Map;
import java.util.UUID;

public interface ProfileFactory {
    @NotNull Profile buildProfile(@NotNull Player player);

    @NotNull Profile getProfile(@NotNull Player player);

    @NotNull @Unmodifiable Map<UUID, Profile> getProfiles();

    void removeProfile(@NotNull Player player);

    void save();

    void save(@NotNull UUID playerUUID);

    void unload();
}
