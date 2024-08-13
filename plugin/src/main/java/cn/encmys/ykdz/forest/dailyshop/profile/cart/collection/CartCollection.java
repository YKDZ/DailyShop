package cn.encmys.ykdz.forest.dailyshop.profile.cart.collection;

import cn.encmys.ykdz.forest.dailyshop.api.shop.order.ShopOrder;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Date;
import java.util.List;
import java.util.Map;

public record CartCollection(@Nullable String name, @NotNull Date createAt,
                             @NotNull List<Map<String, ShopOrder>> orders) {
}
