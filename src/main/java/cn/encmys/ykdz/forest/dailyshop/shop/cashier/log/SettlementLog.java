package cn.encmys.ykdz.forest.dailyshop.shop.cashier.log;

import cn.encmys.ykdz.forest.dailyshop.shop.cashier.log.enums.SettlementLogType;

import java.util.Date;
import java.util.List;
import java.util.UUID;

public class SettlementLog {
    private UUID customer;
    private SettlementLogType type;
    private Date transitionTime;
    private double price;
    private List<String> orderedProductIds;
    private List<String> orderedProductNames;
    private List<Integer> orderedProductStacks;
    private int totalStack;

    private SettlementLog() {}

    public static SettlementLog of(SettlementLogType type, UUID customer) {
        switch (type) {
            case SELL_TO -> {
                return sellToLog(customer);
            }
            case BUY_ALL_FROM -> {
                return buyAllFromLog(customer);
            }
            default -> {
                return buyFromLog(customer);
            }
        }
    }

    public static SettlementLog buyFromLog(UUID customer) {
        return new SettlementLog()
                .setCustomer(customer)
                .setType(SettlementLogType.BUY_FROM)
                .setTransitionTime(new Date());
    }

    public static SettlementLog buyAllFromLog(UUID customer) {
        return new SettlementLog()
                .setCustomer(customer)
                .setType(SettlementLogType.BUY_ALL_FROM)
                .setTransitionTime(new Date());
    }

    public static SettlementLog sellToLog(UUID customer) {
        return new SettlementLog()
                .setCustomer(customer)
                .setType(SettlementLogType.SELL_TO)
                .setTransitionTime(new Date());
    }

    public SettlementLog setCustomer(UUID customer) {
        this.customer = customer;
        return this;
    }

    public SettlementLog setType(SettlementLogType type) {
        this.type = type;
        return this;
    }

    public SettlementLog setTransitionTime(Date transitionTime) {
        this.transitionTime = transitionTime;
        return this;
    }

    public SettlementLog setPrice(double price) {
        this.price = price;
        return this;
    }

    public int getTotalStack() {
        return totalStack;
    }

    public SettlementLog setTotalStack(int totalStack) {
        this.totalStack = totalStack;
        return this;
    }

    public Date getTransitionTime() {
        return transitionTime;
    }

    public SettlementLogType getType() {
        return type;
    }

    public UUID getCustomerUUID() {
        return customer;
    }

    public double getPrice() {
        return price;
    }

    public List<String> getOrderedProductIds() {
        return orderedProductIds;
    }

    public List<String> getOrderedProductNames() {
        return orderedProductNames;
    }

    public SettlementLog setOrderedProductNames(List<String> orderedProductNames) {
        this.orderedProductNames = orderedProductNames;
        return this;
    }

    public List<Integer> getOrderedProductStacks() {
        return orderedProductStacks;
    }

    public SettlementLog setOrderedProductStacks(List<Integer> orderedProductStacks) {
        this.orderedProductStacks = orderedProductStacks;
        return this;
    }

    public SettlementLog setOrderedProductIds(List<String> orderedProductIds) {
        this.orderedProductIds = orderedProductIds;
        return this;
    }
}
