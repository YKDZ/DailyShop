package cn.encmys.ykdz.forest.dailyshop.shop.order;

import cn.encmys.ykdz.forest.dailyshop.api.product.Product;
import cn.encmys.ykdz.forest.dailyshop.api.shop.order.ShopOrder;
import cn.encmys.ykdz.forest.dailyshop.api.shop.order.enums.OrderType;
import org.bukkit.entity.Player;

import java.util.HashMap;
import java.util.Map;

public class ShopOrderImpl implements ShopOrder {
    private OrderType orderType;
    private final Player customer;
    private final Map<String, Integer> orderedProducts = new HashMap<>();
    private Map<String, Double> bill = new HashMap<>();
    private int totalStack = 1;
    private boolean isSettled = false;
    private boolean isBilled = false;

    public ShopOrderImpl(Player customer) {
        this.customer = customer;
    }

    @Override
    public ShopOrder setOrderType(OrderType orderType) {
        this.orderType = orderType;
        return this;
    }

    @Override
    public ShopOrder addProduct(Product product, int amount) {
        orderedProducts.put(product.getId(), orderedProducts.getOrDefault(product.getId(), 0) + amount);
        return this;
    }

    @Override
    public ShopOrder removeProduct(Product product) {
        orderedProducts.remove(product.getId());
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
        this.bill = bill;
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
        isBilled = billed;
        return this;
    }

    @Override
    public ShopOrder setTotalStack(int totalStack) {
        this.totalStack = totalStack;
        return this;
    }

    @Override
    public int getTotalStack() {
        return totalStack;
    }

    @Override
    public double getBill(Product product) {
        return bill.getOrDefault(product.getId(), -1d);
    }
}
