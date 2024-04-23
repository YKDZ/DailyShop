package cn.encmys.ykdz.forest.dailyshop.shop.order;

import cn.encmys.ykdz.forest.dailyshop.api.product.Product;
import cn.encmys.ykdz.forest.dailyshop.shop.order.enums.OrderType;
import org.bukkit.entity.Player;

import java.util.HashMap;
import java.util.Map;

public class ShopOrder {
    private OrderType orderType;
    private Player customer;
    private Map<Product, Integer> orderedProducts = new HashMap<>();
    private Map<Product, Double> bill = new HashMap<>();
    private int totalStack = 1;
    private boolean isSettled = false;
    private boolean isBilled = false;

    private ShopOrder() {}

    public static ShopOrder buyFromOrder(Player customer) {
        return new ShopOrder()
                .setOrderType(OrderType.BUY_FROM)
                .setCustomer(customer);
    }

    public static ShopOrder buyAllFromOrder(Player customer) {
        return new ShopOrder()
                .setOrderType(OrderType.BUY_ALL_FROM)
                .setCustomer(customer);
    }

    public static ShopOrder sellToOrder(Player customer) {
        return new ShopOrder()
                .setOrderType(OrderType.SELL_TO)
                .setCustomer(customer);
    }

    public ShopOrder setOrderType(OrderType orderType) {
        this.orderType = orderType;
        return this;
    }

    public ShopOrder setCustomer(Player customer) {
        this.customer = customer;
        return this;
    }

    public ShopOrder addProduct(Product product, int amount) {
        orderedProducts.put(product, orderedProducts.getOrDefault(product, 0) + amount);
        return this;
    }

    public ShopOrder removeProduct(Product product) {
        orderedProducts.remove(product);
        return this;
    }

    public boolean isSettled() {
        return isSettled;
    }

    public void setSettled(boolean settled) {
        isSettled = settled;
    }

    public OrderType getOrderType() {
        return orderType;
    }

    public Player getCustomer() {
        return customer;
    }

    public Map<Product, Integer> getOrderedProducts() {
        return orderedProducts;
    }

    public double getBilledPrice(Product product) {
        return bill.getOrDefault(product, -1d);
    }

    public ShopOrder setBill(Map<Product, Double> bill) {
        this.bill = bill;
        return this;
    }

    public double getTotalPrice() {
        return bill.values().stream().mapToDouble(Double::doubleValue).sum();
    }

    public boolean isBilled() {
        return isBilled;
    }

    public ShopOrder setBilled(boolean billed) {
        isBilled = billed;
        return this;
    }

    public ShopOrder setTotalStack(int totalStack) {
        this.totalStack = totalStack;
        return this;
    }

    public int getTotalStack() {
        return totalStack;
    }
}
