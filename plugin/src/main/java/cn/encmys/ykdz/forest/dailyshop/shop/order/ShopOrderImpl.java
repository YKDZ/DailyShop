package cn.encmys.ykdz.forest.dailyshop.shop.order;

import cn.encmys.ykdz.forest.dailyshop.api.product.Product;
import cn.encmys.ykdz.forest.dailyshop.api.shop.order.ShopOrder;
import cn.encmys.ykdz.forest.dailyshop.api.shop.order.enums.OrderType;
import org.bukkit.entity.Player;

import java.util.HashMap;
import java.util.Map;

public class ShopOrderImpl implements ShopOrder {
    private OrderType orderType;
    private Player customer;
    private final Map<Product, Integer> orderedProducts = new HashMap<>();
    private Map<Product, Double> bill = new HashMap<>();
    private int totalStack = 1;
    private boolean isSettled = false;
    private boolean isBilled = false;

    public ShopOrderImpl() {}

    @Override
    public cn.encmys.ykdz.forest.dailyshop.api.shop.order.ShopOrder setOrderType(OrderType orderType) {
        this.orderType = orderType;
        return this;
    }

    @Override
    public cn.encmys.ykdz.forest.dailyshop.api.shop.order.ShopOrder setCustomer(Player customer) {
        this.customer = customer;
        return this;
    }

    @Override
    public cn.encmys.ykdz.forest.dailyshop.api.shop.order.ShopOrder addProduct(Product product, int amount) {
        orderedProducts.put(product, orderedProducts.getOrDefault(product, 0) + amount);
        return this;
    }

    @Override
    public cn.encmys.ykdz.forest.dailyshop.api.shop.order.ShopOrder removeProduct(Product product) {
        orderedProducts.remove(product);
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
    public Map<Product, Integer> getOrderedProducts() {
        return orderedProducts;
    }

    @Override
    public double getBilledPrice(Product product) {
        return bill.getOrDefault(product, -1d);
    }

    @Override
    public cn.encmys.ykdz.forest.dailyshop.api.shop.order.ShopOrder setBill(Map<Product, Double> bill) {
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
    public cn.encmys.ykdz.forest.dailyshop.api.shop.order.ShopOrder setBilled(boolean billed) {
        isBilled = billed;
        return this;
    }

    @Override
    public cn.encmys.ykdz.forest.dailyshop.api.shop.order.ShopOrder setTotalStack(int totalStack) {
        this.totalStack = totalStack;
        return this;
    }

    @Override
    public int getTotalStack() {
        return totalStack;
    }

    @Override
    public double getBill(Product product) {
        return bill.getOrDefault(product, -1d);
    }
}
