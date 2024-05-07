package cn.encmys.ykdz.forest.dailyshop.shop.cashier.log.stream;

import cn.encmys.ykdz.forest.dailyshop.api.shop.cashier.log.SettlementLog;
import cn.encmys.ykdz.forest.dailyshop.api.shop.cashier.log.enums.SettlementLogType;
import cn.encmys.ykdz.forest.dailyshop.api.shop.cashier.log.stream.LogStream;

import java.util.Date;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;
import java.util.function.Consumer;
import java.util.stream.Stream;

public class LogStreamImpl extends LogStream {

    private LogStreamImpl(Stream<SettlementLog> stream) {
        super(stream);
    }

    public static LogStream of(Stream<SettlementLog> logStream) {
        return new LogStreamImpl(logStream);
    }

    @Override
    public LogStream after(Date date) {
        return new LogStreamImpl(stream.filter(log -> log.getTransitionTime().after(date)));
    }

    @Override
    public LogStream withCustomer(UUID customerUUID) {
        return new LogStreamImpl(stream.filter(log -> log.getCustomerUUID().equals(customerUUID)));
    }

    @Override
    public LogStream withType(SettlementLogType... types) {
        Set<SettlementLogType> typeSet = Set.of(types);
        return new LogStreamImpl(stream.filter(log -> typeSet.contains(log.getType())));
    }

    @Override
    public LogStream withProduct(String... productIds) {
        Set<String> idSet = Set.of(productIds);
        return new LogStreamImpl(stream.filter(log -> new HashSet<>(log.getOrderedProductIds()).containsAll(idSet)));
    }

    @Override
    public double totalPrice() {
        return stream.mapToDouble(SettlementLog::getPrice).sum();
    }

    @Override
    public void forEach(Consumer<SettlementLog> action) {
        stream.forEach(action);
    }

    @Override
    public Stream<SettlementLog> getStream() {
        return stream;
    }
}