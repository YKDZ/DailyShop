package cn.encmys.ykdz.forest.dailyshop.shop.cashier.log.stream;

import cn.encmys.ykdz.forest.dailyshop.shop.cashier.log.SettlementLog;
import cn.encmys.ykdz.forest.dailyshop.shop.cashier.log.enums.SettlementLogType;

import java.util.Date;
import java.util.List;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class LogStream {
    private Stream<SettlementLog> stream;

    private LogStream(Stream<SettlementLog> stream) {
        this.stream = stream;
    }

    public static LogStream of(List<SettlementLog> logs) {
        return new LogStream(logs.stream());
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

    public LogStream withProduct(String... products) {
        Set<String> idSet = Set.of(products);
        return new LogStream(stream.filter(log -> idSet.containsAll(log.getOrderedProductIds())));
    }

    public double totalPrice() {
        return stream.mapToDouble(SettlementLog::getPrice).sum();
    }

    public List<SettlementLog> toList() {
        return stream.collect(Collectors.toList());
    }
}