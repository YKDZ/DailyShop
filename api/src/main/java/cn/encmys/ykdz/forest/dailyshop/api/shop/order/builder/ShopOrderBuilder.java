package cn.encmys.ykdz.forest.dailyshop.api.shop.order.builder;

import cn.encmys.ykdz.forest.dailyshop.api.shop.order.ShopOrder;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

public interface ShopOrderBuilder {
    ShopOrder buyFromOrder(@NotNull Player customer);

    ShopOrder buyAllFromOrder(@NotNull Player customer);

    ShopOrder sellToOrder(@NotNull Player customer);
}
