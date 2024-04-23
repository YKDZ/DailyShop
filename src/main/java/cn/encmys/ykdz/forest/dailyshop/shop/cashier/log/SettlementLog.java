package cn.encmys.ykdz.forest.dailyshop.shop.cashier.log;

import cn.encmys.ykdz.forest.dailyshop.shop.cashier.log.enums.SettlementLogType;
import com.google.gson.annotations.Expose;

import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.UUID;

public class SettlementLog {
    @Expose
    private UUID customer;
    @Expose
    private SettlementLogType type;
    @Expose
    private Date transitionTime;
    @Expose
    private double price;
    @Expose
    private Map<String, Integer> orderedProductsNameAndStack;
    @Expose
    private List<String> orderedProductIds;
    @Expose
    private int totalStack;

    private SettlementLog() {}

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

    public SettlementLog setStack(int stack) {
        return this;
    }

    public Map<String, Integer> getOrderedProductsNameAndStack() {
        return orderedProductsNameAndStack;
    }

    public SettlementLog setOrderedProductsNameAndStack(Map<String, Integer> orderedProductsNameAndStack) {
        this.orderedProductsNameAndStack = orderedProductsNameAndStack;
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

    public SettlementLog setOrderedProductIds(List<String> orderedProductIds) {
        this.orderedProductIds = orderedProductIds;
        return this;
    }
}
