package cn.encmys.ykdz.forest.dailyshop.profile.factory;

import cn.encmys.ykdz.forest.dailyshop.api.DailyShop;
import cn.encmys.ykdz.forest.dailyshop.api.database.schema.CartSchema;
import cn.encmys.ykdz.forest.dailyshop.api.database.schema.ProfileSchema;
import cn.encmys.ykdz.forest.dailyshop.api.profile.Profile;
import cn.encmys.ykdz.forest.dailyshop.api.profile.enums.ShoppingMode;
import cn.encmys.ykdz.forest.dailyshop.api.profile.factory.ProfileFactory;
import cn.encmys.ykdz.forest.dailyshop.api.shop.order.enums.OrderType;
import cn.encmys.ykdz.forest.dailyshop.profile.ProfileImpl;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.Collections;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

public class ProfileFactoryImpl implements ProfileFactory {
    private final static Map<UUID, Profile> profiles = new ConcurrentHashMap<>();

    @Override
    public Profile buildProfile(Player player) {
        Profile profile = new ProfileImpl(player);
        profiles.put(player.getUniqueId(), profile);
        return profile;
    }

    @NotNull
    public Profile getProfile(Player player) {
        Profile profile = profiles.get(player.getUniqueId());
        if (profile == null) {
            return buildProfile(player);
        }
        return profile;
    }

    @Override
    public Map<UUID, Profile> getProfiles() {
        return Collections.unmodifiableMap(profiles);
    }

    @Override
    public void removeProfile(Player player) {
        if (player.isOnline()) {
            return;
        }
        profiles.remove(player.getUniqueId());
    }

    @Override
    public void save() {
        for (Profile profile : profiles.values()) {
            if (!profile.getCart().getOrders().isEmpty() ||
                    profile.getCart().getMode() != OrderType.SELL_TO ||
                    profile.getShoppingModes().containsValue(ShoppingMode.CART)
            ) {
                DailyShop.DATABASE_FACTORY.getProfileDao().insertSchema(ProfileSchema.of(profile));
                DailyShop.DATABASE_FACTORY.getCartDao().insertSchema(CartSchema.of(profile.getCart()));
            }
        }
    }

    @Override
    public void save(UUID playerUUID) {
        Profile profile = profiles.get(playerUUID);
        DailyShop.DATABASE_FACTORY.getProfileDao().insertSchema(ProfileSchema.of(profile));
        DailyShop.DATABASE_FACTORY.getCartDao().insertSchema(CartSchema.of(profile.getCart()));
    }

    @Override
    public void unload() {
        save();
        profiles.clear();
    }
}
