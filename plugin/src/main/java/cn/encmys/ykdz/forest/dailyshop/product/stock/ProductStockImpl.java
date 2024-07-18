package cn.encmys.ykdz.forest.dailyshop.product.stock;

import cn.encmys.ykdz.forest.dailyshop.api.product.stock.ProductStock;
import cn.encmys.ykdz.forest.dailyshop.api.shop.order.ShopOrder;
import cn.encmys.ykdz.forest.dailyshop.api.shop.order.enums.OrderType;
import cn.encmys.ykdz.forest.dailyshop.util.LogUtils;
import org.jetbrains.annotations.NotNull;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class ProductStockImpl implements ProductStock {
    private final String productId;
    private int currentGlobalAmount;
    private final int initialGlobalAmount;
    private final Map<UUID, Integer> currentPlayerAmount = new HashMap<>();
    private final int initialPlayerAmount;
    private final boolean globalSupply;
    private final boolean playerSupply;
    private final boolean globalOverflow;
    private final boolean playerOverflow;

    public ProductStockImpl(@NotNull String productId, int initialGlobalAmount, int initialPlayerAmount, boolean globalSupply, boolean playerSupply, boolean globalOverflow, boolean playerOverflow) {
        this.productId = productId;
        this.initialGlobalAmount = initialGlobalAmount;
        this.currentGlobalAmount = initialGlobalAmount;
        this.initialPlayerAmount = initialPlayerAmount;
        this.globalSupply = globalSupply;
        this.playerSupply = playerSupply;
        this.globalOverflow = globalOverflow;
        this.playerOverflow = playerOverflow;
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
    public void setCurrentPlayerAmount(@NotNull UUID playerUUID, int amount) {
        this.currentPlayerAmount.put(playerUUID, amount);
    }

    @Override
    public int getInitialGlobalAmount() {
        return initialGlobalAmount;
    }

    @Override
    public boolean isPlayerSupply() {
        return playerSupply;
    }

    @Override
    public boolean isGlobalSupply() {
        return globalSupply;
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
    public boolean isGlobalStock() {
        return initialGlobalAmount != -1;
    }

    @Override
    public void restock() {
        currentGlobalAmount = initialGlobalAmount;
        currentPlayerAmount.clear();
    }

    @Override
    public void modifyPlayer(@NotNull UUID playerUUID, int amount) {
        if ((!playerSupply && amount > 0) || !isPlayerStock()) {
            return;
        }
        boolean isOverflow = getCurrentPlayerAmount(playerUUID) + amount >= initialPlayerAmount;
        setCurrentPlayerAmount(playerUUID, isOverflow ? initialPlayerAmount : getCurrentPlayerAmount(playerUUID) + amount);
    }

    @Override
    public void modifyGlobal(int amount) {
        if ((!globalSupply && amount > 0) || !isGlobalStock()) {
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
        int amount = order.getTotalStack() * order.getOrderedProducts().get(productId) * (order.getOrderType() == OrderType.SELL_TO ? -1 : 1);
        modifyPlayer(uuid, amount);
    }

    @Override
    public void modifyGlobal(@NotNull ShopOrder order) {
        if (!order.isSettled()) {
            LogUtils.warn("Try to handle stock for an unsettled order.");
            return;
        }
        int amount = order.getTotalStack() * order.getOrderedProducts().get(productId) * (order.getOrderType() == OrderType.SELL_TO ? -1 : 1);
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
