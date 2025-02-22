package cn.encmys.ykdz.forest.hyphashop.api.product.stock;

import cn.encmys.ykdz.forest.hyphashop.api.shop.order.ShopOrder;
import org.jetbrains.annotations.NotNull;

import java.util.Map;
import java.util.UUID;

public interface ProductStock {
    String getProductId();

    int getCurrentGlobalAmount();

    void setCurrentGlobalAmount(int currentGlobalAmount);

    int getInitialPlayerAmount();

    int getCurrentPlayerAmount(UUID uuid);

    Map<UUID, Integer> getCurrentPlayerAmount();

    void setCurrentPlayerAmount(UUID uuid, int amount);

    void setCurrentPlayerAmount(Map<UUID, Integer> amount);

    int getInitialGlobalAmount();

    boolean isPlayerReplenish();

    boolean isGlobalReplenish();

    boolean isStock();

    void stock();

    void modifyPlayer(UUID uuid, int amount);

    void modifyGlobal(int amount);

    boolean isPlayerStock();

    boolean isGlobalInherit();

    boolean isPlayerInherit();

    boolean isGlobalStock();

    boolean isPlayerOverflow();

    boolean isGlobalOverflow();

    void modifyPlayer(@NotNull ShopOrder shopOrder);

    void modifyGlobal(@NotNull ShopOrder order);

    boolean ifReachPlayerLimit(@NotNull UUID playerUUID);

    boolean ifReachGlobalLimit();
}
