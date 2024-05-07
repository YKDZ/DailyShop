package cn.encmys.ykdz.forest.dailyshop.api.shop.cashier.log;

import cn.encmys.ykdz.forest.dailyshop.api.shop.cashier.log.enums.SettlementLogType;

import java.util.Date;
import java.util.List;
import java.util.UUID;

public abstract class SettlementLog {
    protected UUID customer;
    protected SettlementLogType type;
    protected Date transitionTime;
    protected double price;
    protected List<String> orderedProductIds;
    protected List<String> orderedProductNames;
    protected List<Integer> orderedProductStacks;
    protected int totalStack;

    public abstract SettlementLog setCustomer(UUID customer);

    public abstract SettlementLog setType(SettlementLogType type);

    public abstract SettlementLog setTransitionTime(Date transitionTime);

    public abstract SettlementLog setPrice(double price);

    public abstract int getTotalStack();

    public abstract SettlementLog setTotalStack(int totalStack);

    public abstract Date getTransitionTime();

    public abstract SettlementLogType getType();

    public abstract UUID getCustomerUUID();

    public abstract double getPrice();

    public abstract List<String> getOrderedProductIds();

    public abstract List<String> getOrderedProductNames();

    public abstract SettlementLog setOrderedProductNames(List<String> orderedProductNames);

    public abstract List<Integer> getOrderedProductStacks();

    public abstract SettlementLog setOrderedProductStacks(List<Integer> orderedProductStacks);

    public abstract SettlementLog setOrderedProductIds(List<String> orderedProductIds);
}
