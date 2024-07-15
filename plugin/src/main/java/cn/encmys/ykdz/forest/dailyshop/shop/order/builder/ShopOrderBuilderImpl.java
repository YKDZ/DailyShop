package cn.encmys.ykdz.forest.dailyshop.shop.order.builder;

import cn.encmys.ykdz.forest.dailyshop.api.shop.order.ShopOrder;
import cn.encmys.ykdz.forest.dailyshop.api.shop.order.builder.ShopOrderBuilder;
import cn.encmys.ykdz.forest.dailyshop.api.shop.order.enums.OrderType;
import cn.encmys.ykdz.forest.dailyshop.shop.order.ShopOrderImpl;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

public class ShopOrderBuilderImpl implements ShopOrderBuilder {
    @Override
    public ShopOrder buyFromOrder(@NotNull Player customer) {
        return new ShopOrderImpl(customer)
                .setOrderType(OrderType.BUY_FROM);
    }

    @Override
    public ShopOrder buyAllFromOrder(@NotNull Player customer) {
        return new ShopOrderImpl(customer)
                .setOrderType(OrderType.BUY_ALL_FROM);
    }

    @Override
    public ShopOrder sellToOrder(@NotNull Player customer) {
        return new ShopOrderImpl(customer)
                .setOrderType(OrderType.SELL_TO);
    }
}
