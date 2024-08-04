package cn.encmys.ykdz.forest.dailyshop.shop.order;

import cn.encmys.ykdz.forest.dailyshop.api.product.Product;
import cn.encmys.ykdz.forest.dailyshop.api.shop.order.ShopOrder;
import cn.encmys.ykdz.forest.dailyshop.api.shop.order.enums.OrderType;
import cn.encmys.ykdz.forest.dailyshop.api.utils.LogUtils;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

public class ShopOrderImpl implements ShopOrder {
    private final Player customer;
    private final Map<String, Integer> orderedProducts = new HashMap<>();
    private OrderType orderType;
    private Map<String, Double> bill = new HashMap<>();
    private boolean isSettled = false;
    private boolean isBilled = false;

    public ShopOrderImpl(Player customer) {
        this.customer = customer;
    }

    @Override
    public @NotNull ShopOrder combineOrder(ShopOrder order) {
        if (customer.getUniqueId() != order.getCustomer().getUniqueId()) {
            LogUtils.warn("Try to combine orders with different customer.");
            return this;
        }
        if (orderType != order.getOrderType()) {
            LogUtils.warn("Try to combine orders with different order types.");
            return this;
        }
        if (isSettled || order.isSettled()) {
            LogUtils.warn("Try to combine orders that has already been settled.");
            return this;
        }
        for (Map.Entry<String, Integer> entry : order.getOrderedProducts().entrySet()) {
            modifyStack(entry.getKey(), entry.getValue());
        }
        setBilled(false);
        return this;
    }

    @Override
    public @NotNull ShopOrder setOrderType(@NotNull OrderType orderType) {
        if (isSettled) {
            return this;
        }
        this.orderType = orderType;
        setBilled(false);
        return this;
    }

    @Override
    public @NotNull ShopOrder modifyStack(@NotNull Product product, int amount) {
        return modifyStack(product.getId(), amount);
    }

    @Override
    public @NotNull ShopOrder modifyStack(@NotNull String productId, int amount) {
        if (isSettled) {
            return this;
        }
        int newValue = orderedProducts.getOrDefault(productId, 0) + amount;
        return setStack(productId, newValue);
    }

    @Override
    public @NotNull ShopOrder setStack(@NotNull Product product, int amount) {
        return setStack(product.getId(), amount);
    }

    @Override
    public @NotNull ShopOrder setStack(@NotNull String productId, int amount) {
        if (isSettled) {
            return this;
        }
        if (amount <= 0) {
            orderedProducts.remove(productId);
        } else {
            orderedProducts.put(productId, amount);
        }
        setBilled(false);
        return this;
    }

    @Override
    public boolean isSettled() {
        return isSettled;
    }

    @Override
    public void setSettled(boolean settled) {
        isSettled = settled;
    }

    @Override
    public @NotNull OrderType getOrderType() {
        return orderType;
    }

    @Override
    public Player getCustomer() {
        return customer;
    }

    @Override
    public Map<String, Integer> getOrderedProducts() {
        return Collections.unmodifiableMap(orderedProducts);
    }

    @Override
    public double getBilledPrice(Product product) {
        return bill.getOrDefault(product.getId(), -1d);
    }

    @Override
    public @NotNull ShopOrder setBill(Map<String, Double> bill) {
        if (isSettled) {
            return this;
        }
        this.bill = bill;
        setBilled(false);
        return this;
    }

    @Override
    public double getTotalPrice() {
        return bill.values().stream().mapToDouble(Double::doubleValue).sum();
    }

    @Override
    public boolean isBilled() {
        return isBilled;
    }

    @Override
    public @NotNull ShopOrder setBilled(boolean billed) {
        if (isSettled) {
            return this;
        }
        isBilled = billed;
        return this;
    }

    @Override
    public void clear() {
        if (isSettled) {
            return;
        }
        isBilled = false;
        orderedProducts.clear();
    }
}
