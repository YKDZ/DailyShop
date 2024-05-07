package cn.encmys.ykdz.forest.dailyshop.shop.cashier.log.stream;

import cn.encmys.ykdz.forest.dailyshop.api.shop.cashier.log.enums.SettlementLogType;
import cn.encmys.ykdz.forest.dailyshop.shop.cashier.log.SettlementLog;

import java.util.Date;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;
import java.util.function.Consumer;
import java.util.stream.Stream;

public class LogStream {
    private Stream<SettlementLog> stream;

    private LogStream(Stream<SettlementLog> stream) {
        this.stream = stream;
    }

    public static LogStream of(Stream<SettlementLog> logStream) {
        return new LogStream(logStream);
    }

    public LogStream after(Date date) {
        return new LogStream(stream.filter(log -> log.getTransitionTime().after(date)));
    }

    public LogStream withCustomer(UUID customerUUID) {
        return new LogStream(stream.filter(log -> log.getCustomerUUID().equals(customerUUID)));
    }

    public LogStream withType(SettlementLogType... types) {
        Set<SettlementLogType> typeSet = Set.of(types);
        return new LogStream(stream.filter(log -> typeSet.contains(log.getType())));
    }

    public LogStream withProduct(String... productIds) {
        Set<String> idSet = Set.of(productIds);
        return new LogStream(stream.filter(log -> new HashSet<>(log.getOrderedProductIds()).containsAll(idSet)));
    }

    public double totalPrice() {
        return stream.mapToDouble(SettlementLog::getPrice).sum();
    }

    public void forEach(Consumer<SettlementLog> action) {
        stream.forEach(action);
    }

    public Stream<SettlementLog> getStream() {
        return stream;
    }
}