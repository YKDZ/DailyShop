package cn.encmys.ykdz.forest.dailyshop.api.shop.cashier.log;

import cn.encmys.ykdz.forest.dailyshop.api.shop.order.enums.OrderType;
import org.jetbrains.annotations.NotNull;

import java.util.Date;
import java.util.Map;
import java.util.UUID;

public abstract class SettlementLog {
    protected UUID customer;
    protected OrderType type;
    protected Date transitionTime;
    protected double price;
    protected Map<String, Integer> orderedProducts;
    protected String settledShop;

    public abstract SettlementLog setCustomerUUID(UUID customerUUID);

    public abstract OrderType getType();

    public abstract SettlementLog setTransitionTime(Date transitionTime);

    public abstract SettlementLog setType(OrderType type);

    public abstract Date getTransitionTime();

    public abstract double getTotalPrice();

    public abstract UUID getCustomerUUID();

    public abstract SettlementLog setTotalPrice(double price);

    @NotNull
    public abstract Map<String, Integer> getOrderedProducts();

    public abstract SettlementLog setOrderedProducts(Map<String, Integer> orderedProducts);

    public abstract String getSettledShop();

    public abstract SettlementLog setSettledShop(String settledShop);
}
