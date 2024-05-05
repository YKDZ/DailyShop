package cn.encmys.ykdz.forest.dailyshop.api.shop.order;

import cn.encmys.ykdz.forest.dailyshop.api.product.Product;
import cn.encmys.ykdz.forest.dailyshop.shop.order.ShopOrderImpl;
import cn.encmys.ykdz.forest.dailyshop.shop.order.enums.OrderType;
import org.bukkit.entity.Player;

import java.util.Map;

public interface ShopOrder {
    static ShopOrder buyFromOrder(Player customer) {
        return new ShopOrderImpl()
                .setOrderType(OrderType.BUY_FROM)
                .setCustomer(customer);
    }

    static ShopOrder buyAllFromOrder(Player customer) {
        return new ShopOrderImpl()
                .setOrderType(OrderType.BUY_ALL_FROM)
                .setCustomer(customer);
    }

    static ShopOrder sellToOrder(Player customer) {
        return new ShopOrderImpl()
                .setOrderType(OrderType.SELL_TO)
                .setCustomer(customer);
    }

    ShopOrder setOrderType(OrderType orderType);

    ShopOrder setCustomer(Player customer);

    ShopOrder addProduct(Product product, int amount);

    ShopOrder removeProduct(Product product);

    boolean isSettled();

    void setSettled(boolean settled);

    OrderType getOrderType();

    Player getCustomer();

    Map<Product, Integer> getOrderedProducts();

    double getBilledPrice(Product product);

    ShopOrder setBill(Map<Product, Double> bill);

    double getTotalPrice();

    boolean isBilled();

    ShopOrder setBilled(boolean billed);

    ShopOrder setTotalStack(int totalStack);

    int getTotalStack();

    double getBill(Product product);
}
