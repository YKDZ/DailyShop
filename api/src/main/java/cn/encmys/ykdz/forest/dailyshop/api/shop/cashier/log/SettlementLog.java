package cn.encmys.ykdz.forest.dailyshop.api.shop.cashier.log;

import cn.encmys.ykdz.forest.dailyshop.api.shop.order.enums.OrderType;

import java.util.Date;
import java.util.List;
import java.util.UUID;

public abstract class SettlementLog {
    protected UUID customer;
    protected OrderType type;
    protected Date transitionTime;
    protected double price;
    protected List<String> orderedProductIds;
    protected List<String> orderedProductNames;
    protected List<Integer> orderedProductStacks;

    public abstract SettlementLog setCustomer(UUID customer);

    public abstract OrderType getType();

    public abstract SettlementLog setTransitionTime(Date transitionTime);

    public abstract SettlementLog setType(OrderType type);

    public abstract Date getTransitionTime();

    public abstract double getTotalPrice();

    public abstract UUID getCustomerUUID();

    public abstract SettlementLog setTotalPrice(double price);

    public abstract List<String> getOrderedProductIds();

    public abstract List<String> getOrderedProductNames();

    public abstract SettlementLog setOrderedProductNames(List<String> orderedProductNames);

    public abstract List<Integer> getOrderedProductStacks();

    public abstract SettlementLog setOrderedProductStacks(List<Integer> orderedProductStacks);

    public abstract SettlementLog setOrderedProductIds(List<String> orderedProductIds);
}
