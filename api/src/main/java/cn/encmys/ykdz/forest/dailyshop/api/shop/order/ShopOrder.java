package cn.encmys.ykdz.forest.dailyshop.api.shop.order;

import cn.encmys.ykdz.forest.dailyshop.api.product.Product;
import cn.encmys.ykdz.forest.dailyshop.api.shop.order.enums.OrderType;
import org.bukkit.entity.Player;

import java.util.Map;

public interface ShopOrder {
    ShopOrder combineOrder(ShopOrder order);

    ShopOrder setOrderType(OrderType orderType);

    ShopOrder addProduct(Product product, int amount);

    ShopOrder setProduct(Product product, int amount);

    ShopOrder removeProduct(Product product);

    boolean isSettled();

    void setSettled(boolean settled);

    OrderType getOrderType();

    Player getCustomer();

    Map<String, Integer> getOrderedProducts();

    double getBilledPrice(Product product);

    ShopOrder setBill(Map<String, Double> bill);

    double getTotalPrice();

    boolean isBilled();

    ShopOrder setBilled(boolean billed);

    double getBill(Product product);
}
