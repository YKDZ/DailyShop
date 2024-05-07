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

    public static ShopOrder buyFromOrder(Player customer) {
        return new ShopOrderImpl()
                .setOrderType(OrderType.BUY_FROM)
                .setCustomer(customer);
    }

    public static ShopOrder buyAllFromOrder(Player customer) {
        return new ShopOrderImpl()
                .setOrderType(OrderType.BUY_ALL_FROM)
                .setCustomer(customer);
    }

    public static ShopOrder sellToOrder(Player customer) {
        return new ShopOrderImpl()
                .setOrderType(OrderType.SELL_TO)
                .setCustomer(customer);
    }

    @Override
    public ShopOrder setOrderType(OrderType orderType) {
        this.orderType = orderType;
        return this;
    }

    @Override
    public ShopOrder setCustomer(Player customer) {
        this.customer = customer;
        return this;
    }

    @Override
    public ShopOrder addProduct(Product product, int amount) {
        orderedProducts.put(product, orderedProducts.getOrDefault(product, 0) + amount);
        return this;
    }

    @Override
    public ShopOrder removeProduct(Product product) {
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
    public ShopOrder setBill(Map<Product, Double> bill) {
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
        return bill.getOrDefault(product, -1d);
    }
}
