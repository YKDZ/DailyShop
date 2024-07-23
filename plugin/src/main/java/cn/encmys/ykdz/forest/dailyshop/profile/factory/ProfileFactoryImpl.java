package cn.encmys.ykdz.forest.dailyshop.profile.factory;

import cn.encmys.ykdz.forest.dailyshop.api.profile.Profile;
import cn.encmys.ykdz.forest.dailyshop.api.profile.factory.ProfileFactory;
import cn.encmys.ykdz.forest.dailyshop.profile.ProfileImpl;
import org.bukkit.entity.Player;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class ProfileFactoryImpl implements ProfileFactory {
    private final static Map<UUID, Profile> profiles = new HashMap<>();

    @Override
    public Profile buildProfile(Player player) {
        Profile profile = new ProfileImpl(player);
        profiles.put(player.getUniqueId(), profile);
        return profile;
    }

    @Override
    public Profile getProfile(Player player) {
        return profiles.getOrDefault(player.getUniqueId(), buildProfile(player));
    }

    @Override
    public Map<UUID, Profile> getProfiles() {
        return profiles;
    }

    @Override
    public void save() {

    }

    @Override
    public void unload() {

    }
}
