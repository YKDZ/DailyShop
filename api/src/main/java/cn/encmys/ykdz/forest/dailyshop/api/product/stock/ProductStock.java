package cn.encmys.ykdz.forest.dailyshop.api.product.stock;

import cn.encmys.ykdz.forest.dailyshop.api.shop.order.ShopOrder;
import org.jetbrains.annotations.NotNull;

import java.util.UUID;

public interface ProductStock {
    String getProductId();

    int getCurrentGlobalAmount();

    void setCurrentGlobalAmount(int currentGlobalAmount);

    int getInitialPlayerAmount();

    int getCurrentPlayerAmount(UUID uuid);

    void setCurrentPlayerAmount(UUID uuid, int amount);

    int getInitialGlobalAmount();

    boolean isPlayerSupply();

    boolean isGlobalSupply();

    void restock();

    void modifyPlayer(UUID uuid, int amount);

    void modifyGlobal(int amount);

    boolean isPlayerStock();

    boolean isGlobalStock();

    boolean isPlayerOverflow();

    boolean isGlobalOverflow();

    void modifyPlayer(@NotNull ShopOrder shopOrder);

    void modifyGlobal(@NotNull ShopOrder order);

    boolean ifReachPlayerLimit(@NotNull UUID playerUUID);

    boolean ifReachGlobalLimit();
}
