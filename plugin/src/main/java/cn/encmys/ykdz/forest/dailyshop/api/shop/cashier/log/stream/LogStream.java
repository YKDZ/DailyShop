package cn.encmys.ykdz.forest.dailyshop.api.shop.cashier.log.stream;

import cn.encmys.ykdz.forest.dailyshop.api.shop.cashier.log.SettlementLog;
import cn.encmys.ykdz.forest.dailyshop.api.shop.cashier.log.enums.SettlementLogType;

import java.util.Date;
import java.util.UUID;
import java.util.function.Consumer;
import java.util.stream.Stream;

public abstract class LogStream {
    protected Stream<SettlementLog> stream;

    public LogStream(Stream<SettlementLog> stream) {
        this.stream = stream;
    }

    public abstract LogStream after(Date date);

    public abstract LogStream withCustomer(UUID customerUUID);

    public abstract LogStream withType(SettlementLogType... types);

    public abstract LogStream withProduct(String... productIds);

    public abstract double totalPrice();

    public abstract void forEach(Consumer<SettlementLog> action);

    public abstract Stream<SettlementLog> getStream();
}
