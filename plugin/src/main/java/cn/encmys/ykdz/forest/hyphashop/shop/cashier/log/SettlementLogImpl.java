package cn.encmys.ykdz.forest.hyphashop.shop.cashier.log;

import cn.encmys.ykdz.forest.hyphashop.api.shop.cashier.log.SettlementLog;
import cn.encmys.ykdz.forest.hyphashop.api.shop.cashier.log.amount.AmountPair;
import cn.encmys.ykdz.forest.hyphashop.api.shop.order.enums.OrderType;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Unmodifiable;

import java.util.Collections;
import java.util.Date;
import java.util.Map;
import java.util.UUID;

public class SettlementLogImpl implements SettlementLog {
    protected UUID customer;
    protected OrderType type;
    protected Date transitionTime;
    protected double price;
    protected Map<String, AmountPair> orderedProducts;
    protected String settledShopId;

    private SettlementLogImpl() {
    }

    public static @NotNull SettlementLog of(@NotNull OrderType type, @NotNull UUID customer) {
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

    public static @NotNull SettlementLog buyFromLog(@NotNull UUID customer) {
        return new SettlementLogImpl()
                .setCustomerUUID(customer)
                .setType(OrderType.BUY_FROM)
                .setTransitionTime(new Date());
    }

    public static @NotNull SettlementLog buyAllFromLog(@NotNull UUID customer) {
        return new SettlementLogImpl()
                .setCustomerUUID(customer)
                .setType(OrderType.BUY_ALL_FROM)
                .setTransitionTime(new Date());
    }

    public static @NotNull SettlementLog sellToLog(@NotNull UUID customer) {
        return new SettlementLogImpl()
                .setCustomerUUID(customer)
                .setType(OrderType.SELL_TO)
                .setTransitionTime(new Date());
    }

    @Override
    public @NotNull SettlementLog setCustomerUUID(@NotNull UUID customerUUID) {
        this.customer = customerUUID;
        return this;
    }

    @Override
    public @NotNull SettlementLog setType(@NotNull OrderType type) {
        this.type = type;
        return this;
    }

    @Override
    public @NotNull SettlementLog setTransitionTime(@NotNull Date transitionTime) {
        this.transitionTime = transitionTime;
        return this;
    }

    @Override
    public @NotNull SettlementLog setTotalPrice(double price) {
        this.price = price;
        return this;
    }

    @Override
    public @NotNull @Unmodifiable Map<String, AmountPair> getOrderedProducts() {
        return Collections.unmodifiableMap(orderedProducts);
    }

    @Override
    public @NotNull SettlementLog setOrderedProducts(@NotNull Map<String, AmountPair> orderedProducts) {
        this.orderedProducts = orderedProducts;
        return this;
    }

    @Override
    public @NotNull String getSettledShopId() {
        return settledShopId;
    }

    @Override
    public @NotNull SettlementLog setSettledShopId(@NotNull String settledShopId) {
        this.settledShopId = settledShopId;
        return this;
    }

    @Override
    public @NotNull Date getTransitionTime() {
        return transitionTime;
    }

    @Override
    public @NotNull OrderType getType() {
        return type;
    }

    @Override
    public @NotNull UUID getCustomerUUID() {
        return customer;
    }

    @Override
    public double getTotalPrice() {
        return price;
    }
}
