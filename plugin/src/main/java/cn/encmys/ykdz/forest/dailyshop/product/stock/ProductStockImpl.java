package cn.encmys.ykdz.forest.dailyshop.product.stock;

import cn.encmys.ykdz.forest.dailyshop.api.product.stock.ProductStock;
import cn.encmys.ykdz.forest.dailyshop.api.shop.order.ShopOrder;
import cn.encmys.ykdz.forest.dailyshop.api.shop.order.enums.OrderType;
import cn.encmys.ykdz.forest.dailyshop.api.utils.LogUtils;
import com.google.gson.annotations.Expose;
import org.jetbrains.annotations.NotNull;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class ProductStockImpl implements ProductStock {
    private final String productId;
    private final int initialGlobalAmount;
    @Expose
    private final Map<UUID, Integer> currentPlayerAmount = new HashMap<>();
    private final int initialPlayerAmount;
    private final boolean globalReplenish;
    private final boolean playerReplenish;
    private final boolean globalOverflow;
    private final boolean playerOverflow;
    private final boolean globalInherit;
    private final boolean playerInherit;
    @Expose
    private int currentGlobalAmount;

    public ProductStockImpl(@NotNull String productId, int initialGlobalAmount, int initialPlayerAmount, boolean globalReplenish, boolean playerReplenish, boolean globalOverflow, boolean playerOverflow, boolean globalInherit, boolean playerInherit) {
        this.productId = productId;
        this.initialGlobalAmount = initialGlobalAmount;
        this.currentGlobalAmount = initialGlobalAmount;
        this.initialPlayerAmount = initialPlayerAmount;
        this.globalReplenish = globalReplenish;
        this.playerReplenish = playerReplenish;
        this.globalOverflow = globalOverflow;
        this.playerOverflow = playerOverflow;
        this.globalInherit = globalInherit;
        this.playerInherit = playerInherit;
    }

    @Override
    public String getProductId() {
        return productId;
    }

    @Override
    public int getCurrentGlobalAmount() {
        return currentGlobalAmount;
    }

    @Override
    public void setCurrentGlobalAmount(int currentGlobalAmount) {
        this.currentGlobalAmount = currentGlobalAmount;
    }

    @Override
    public int getInitialPlayerAmount() {
        return initialPlayerAmount;
    }

    @Override
    public int getCurrentPlayerAmount(@NotNull UUID playerUUID) {
        return currentPlayerAmount.getOrDefault(playerUUID, initialPlayerAmount);
    }

    @Override
    public Map<UUID, Integer> getCurrentPlayerAmount() {
        return currentPlayerAmount;
    }

    @Override
    public void setCurrentPlayerAmount(Map<UUID, Integer> amount) {
        this.currentPlayerAmount.clear();
        this.currentPlayerAmount.putAll(amount);
    }

    @Override
    public void setCurrentPlayerAmount(@NotNull UUID playerUUID, int amount) {
        this.currentPlayerAmount.put(playerUUID, amount);
    }

    @Override
    public int getInitialGlobalAmount() {
        return initialGlobalAmount;
    }

    @Override
    public boolean isPlayerReplenish() {
        return playerReplenish;
    }

    @Override
    public boolean isGlobalReplenish() {
        return globalReplenish;
    }

    @Override
    public boolean isPlayerOverflow() {
        return playerOverflow;
    }

    @Override
    public boolean isGlobalOverflow() {
        return globalOverflow;
    }

    @Override
    public boolean isPlayerStock() {
        return initialPlayerAmount != -1;
    }

    @Override
    public boolean isGlobalInherit() {
        return globalInherit;
    }

    @Override
    public boolean isPlayerInherit() {
        return playerInherit;
    }

    @Override
    public boolean isGlobalStock() {
        return initialGlobalAmount != -1;
    }

    @Override
    public boolean isStock() {
        return isGlobalStock() || isPlayerStock();
    }

    @Override
    public void stock() {
        if (!isStock()) {
            return;
        }
        if (isPlayerStock() && !isPlayerInherit()) {
            currentPlayerAmount.clear();
        }
        if (isGlobalStock() && !isGlobalInherit()) {
            currentGlobalAmount = initialGlobalAmount;
        }
    }

    @Override
    public void modifyPlayer(@NotNull UUID playerUUID, int amount) {
        if ((!playerReplenish && amount > 0) || !isPlayerStock()) {
            return;
        }
        boolean isOverflow = getCurrentPlayerAmount(playerUUID) + amount >= initialPlayerAmount;
        setCurrentPlayerAmount(playerUUID, isOverflow ? initialPlayerAmount : getCurrentPlayerAmount(playerUUID) + amount);
    }

    @Override
    public void modifyGlobal(int amount) {
        if ((!globalReplenish && amount > 0) || !isGlobalStock()) {
            return;
        }
        boolean isOverflow = getCurrentGlobalAmount() + amount >= getInitialGlobalAmount();
        setCurrentGlobalAmount(isOverflow ? initialGlobalAmount : currentGlobalAmount + amount);
    }

    @Override
    public void modifyPlayer(@NotNull ShopOrder order) {
        if (!order.isSettled()) {
            LogUtils.warn("Try to handle stock for an unsettled order.");
            return;
        }
        UUID uuid = order.getCustomer().getUniqueId();
        int amount = order.getOrderedProducts().get(productId) * (order.getOrderType() == OrderType.SELL_TO ? -1 : 1);
        modifyPlayer(uuid, amount);
    }

    @Override
    public void modifyGlobal(@NotNull ShopOrder order) {
        if (!order.isSettled()) {
            LogUtils.warn("Try to handle stock for an unsettled order.");
            return;
        }
        int amount = order.getOrderedProducts().get(productId) * (order.getOrderType() == OrderType.SELL_TO ? -1 : 1);
        modifyGlobal(amount);
    }

    @Override
    public boolean ifReachPlayerLimit(@NotNull UUID playerUUID) {
        return getCurrentPlayerAmount(playerUUID) <= 0;
    }

    @Override
    public boolean ifReachGlobalLimit() {
        return getCurrentGlobalAmount() <= 0;
    }
}
