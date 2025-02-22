package cn.encmys.ykdz.forest.hyphashop.profile.factory;

import cn.encmys.ykdz.forest.hyphashop.api.HyphaShop;
import cn.encmys.ykdz.forest.hyphashop.api.database.schema.CartSchema;
import cn.encmys.ykdz.forest.hyphashop.api.database.schema.ProfileSchema;
import cn.encmys.ykdz.forest.hyphashop.api.profile.Profile;
import cn.encmys.ykdz.forest.hyphashop.api.profile.factory.ProfileFactory;
import cn.encmys.ykdz.forest.hyphashop.profile.ProfileImpl;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Unmodifiable;

import java.util.Collections;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

public class ProfileFactoryImpl implements ProfileFactory {
    private final static Map<UUID, Profile> profiles = new ConcurrentHashMap<>();

    @Override
    public @NotNull Profile buildProfile(@NotNull Player player) {
        Profile profile = new ProfileImpl(player);

        ProfileSchema profileSchema = HyphaShop.DATABASE_FACTORY.getProfileDao().querySchema(player.getUniqueId());
        CartSchema cartSchema = HyphaShop.DATABASE_FACTORY.getCartDao().querySchema(player.getUniqueId());

        if (profileSchema != null) {
            profile.setShoppingModes(profileSchema.shoppingModes());
        }
        if (cartSchema != null) {
            profile.getCart().setMode(cartSchema.mode());
            profile.getCart().setOrders(cartSchema.orders());
        }

        profiles.put(player.getUniqueId(), profile);
        return profile;
    }

    public @NotNull Profile getProfile(@NotNull Player player) {
        Profile profile = profiles.get(player.getUniqueId());
        if (profile == null) {
            return buildProfile(player);
        }
        return profile;
    }

    @Override
    public @NotNull @Unmodifiable Map<UUID, Profile> getProfiles() {
        return Collections.unmodifiableMap(profiles);
    }

    @Override
    public void removeProfile(@NotNull Player player) {
        if (player.isOnline()) {
            return;
        }
        profiles.remove(player.getUniqueId());
    }

    @Override
    public void save() {
        for (Profile profile : profiles.values()) {
            HyphaShop.DATABASE_FACTORY.getProfileDao().insertSchema(ProfileSchema.of(profile));
            HyphaShop.DATABASE_FACTORY.getCartDao().insertSchema(CartSchema.of(profile.getCart()));
        }
    }

    @Override
    public void save(@NotNull UUID playerUUID) {
        Profile profile = profiles.get(playerUUID);
        HyphaShop.DATABASE_FACTORY.getProfileDao().insertSchema(ProfileSchema.of(profile));
        HyphaShop.DATABASE_FACTORY.getCartDao().insertSchema(CartSchema.of(profile.getCart()));
    }

    @Override
    public void unload() {
        save();
        profiles.clear();
    }
}
