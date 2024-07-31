package cn.encmys.ykdz.forest.dailyshop.api.shop.order;

import cn.encmys.ykdz.forest.dailyshop.api.product.Product;
import cn.encmys.ykdz.forest.dailyshop.api.shop.order.enums.OrderType;
import org.bukkit.entity.Player;

import java.util.Map;

/**
 * ShopOrder stores the product and customer information involved in each transaction. The total price for each ShopOrder cannot be calculated individually (because the price information is stored independently in each Shop's ShopPricer instance) and needs to be calculated using the ShopCashier#billOrder method.
 * <p>
 * As a stipulation, ShopOrder of type OrderType.BUY_ALL_FROM needs to ensure that each key of map orderedProducts has a value of 1 in order to work properly.
 */
public interface ShopOrder {
    ShopOrder combineOrder(ShopOrder order);

    ShopOrder setOrderType(OrderType orderType);

    ShopOrder modifyStack(Product product, int amount);

    ShopOrder modifyStack(String productId, int amount);

    ShopOrder setStack(Product product, int amount);

    ShopOrder setStack(String productId, int amount);

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

    void clear();
}
