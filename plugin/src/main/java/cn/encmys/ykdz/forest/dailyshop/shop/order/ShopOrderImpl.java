package cn.encmys.ykdz.forest.dailyshop.shop.order;

import cn.encmys.ykdz.forest.dailyshop.api.product.Product;
import cn.encmys.ykdz.forest.dailyshop.api.shop.order.ShopOrder;
import cn.encmys.ykdz.forest.dailyshop.api.shop.order.enums.OrderType;
import cn.encmys.ykdz.forest.dailyshop.api.utils.LogUtils;
import org.bukkit.entity.Player;

import java.util.HashMap;
import java.util.Map;

public class ShopOrderImpl implements ShopOrder {
    private OrderType orderType;
    private final Player customer;
    private final Map<String, Integer> orderedProducts = new HashMap<>();
    private Map<String, Double> bill = new HashMap<>();
    private boolean isSettled = false;
    private boolean isBilled = false;

    public ShopOrderImpl(Player customer) {
        this.customer = customer;
    }

    @Override
    public ShopOrder combineOrder(ShopOrder order) {
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
            orderedProducts.put(entry.getKey(), entry.getValue() + orderedProducts.getOrDefault(entry.getKey(), 0));
        }
        setBilled(false);
        return this;
    }

    @Override
    public ShopOrder setOrderType(OrderType orderType) {
        if (isSettled) {
            return this;
        }
        this.orderType = orderType;
        setBilled(false);
        return this;
    }

    @Override
    public ShopOrder addProduct(Product product, int amount) {
        if (isSettled) {
            return this;
        }
        orderedProducts.put(product.getId(), orderedProducts.getOrDefault(product.getId(), 0) + amount);
        setBilled(false);
        return this;
    }

    @Override
    public ShopOrder setProduct(Product product, int amount) {
        if (isSettled) {
            return this;
        }
        orderedProducts.put(product.getId(), amount);
        setBilled(false);
        return this;
    }

    @Override
    public ShopOrder removeProduct(Product product) {
        if (isSettled) {
            return this;
        }
        orderedProducts.remove(product.getId());
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
    public OrderType getOrderType() {
        return orderType;
    }

    @Override
    public Player getCustomer() {
        return customer;
    }

    @Override
    public Map<String, Integer> getOrderedProducts() {
        return orderedProducts;
    }

    @Override
    public double getBilledPrice(Product product) {
        return bill.getOrDefault(product.getId(), -1d);
    }

    @Override
    public ShopOrder setBill(Map<String, Double> bill) {
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
    public ShopOrder setBilled(boolean billed) {
        if (isSettled) {
            return this;
        }
        isBilled = billed;
        return this;
    }

    @Override
    public double getBill(Product product) {
        return bill.getOrDefault(product.getId(), -1d);
    }
}
