package cn.encmys.ykdz.forest.dailyshop.shop.cashier.log;

import cn.encmys.ykdz.forest.dailyshop.api.shop.cashier.log.SettlementLog;
import cn.encmys.ykdz.forest.dailyshop.api.shop.order.enums.OrderType;
import org.jetbrains.annotations.NotNull;

import java.util.Date;
import java.util.Map;
import java.util.UUID;

public class SettlementLogImpl extends SettlementLog {
    private SettlementLogImpl() {
    }

    public static SettlementLog of(OrderType type, UUID customer) {
        switch (type) {
            case SELL_TO -> {
                return SettlementLogImpl.sellToLog(customer);
            }
            case BUY_ALL_FROM -> {
                return SettlementLogImpl.buyAllFromLog(customer);
            }
            default -> {
                return SettlementLogImpl.buyFromLog(customer);
            }
        }
    }

    public static SettlementLog buyFromLog(UUID customer) {
        return new SettlementLogImpl()
                .setCustomer(customer)
                .setType(OrderType.BUY_FROM)
                .setTransitionTime(new Date());
    }

    public static SettlementLog buyAllFromLog(UUID customer) {
        return new SettlementLogImpl()
                .setCustomer(customer)
                .setType(OrderType.BUY_ALL_FROM)
                .setTransitionTime(new Date());
    }

    public static SettlementLog sellToLog(UUID customer) {
        return new SettlementLogImpl()
                .setCustomer(customer)
                .setType(OrderType.SELL_TO)
                .setTransitionTime(new Date());
    }

    @Override
    public SettlementLog setCustomer(UUID customer) {
        this.customer = customer;
        return this;
    }

    @Override
    public SettlementLog setType(OrderType type) {
        this.type = type;
        return this;
    }

    @Override
    public SettlementLog setTransitionTime(Date transitionTime) {
        this.transitionTime = transitionTime;
        return this;
    }

    @Override
    public SettlementLog setTotalPrice(double price) {
        this.price = price;
        return this;
    }

    @Override
    @NotNull
    public Map<String, Integer> getOrderedProducts() {
        return orderedProducts;
    }

    @Override
    public SettlementLog setOrderedProducts(Map<String, Integer> orderedProducts) {
        this.orderedProducts = orderedProducts;
        return this;
    }

    @Override
    public Date getTransitionTime() {
        return transitionTime;
    }

    @Override
    public OrderType getType() {
        return type;
    }

    @Override
    public UUID getCustomerUUID() {
        return customer;
    }

    @Override
    public double getTotalPrice() {
        return price;
    }
}
