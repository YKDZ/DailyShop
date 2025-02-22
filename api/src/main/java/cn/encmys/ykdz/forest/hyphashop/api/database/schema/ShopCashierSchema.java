package cn.encmys.ykdz.forest.hyphashop.api.database.schema;

import cn.encmys.ykdz.forest.hyphashop.api.shop.cashier.ShopCashier;
import org.jetbrains.annotations.NotNull;

public record ShopCashierSchema(@NotNull String shopId,
                                double balance) {
    public static ShopCashierSchema of(@NotNull ShopCashier cashier) {
        return new ShopCashierSchema(cashier.getShop().getId(), cashier.getBalance());
    }
}
