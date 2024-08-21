package cn.encmys.ykdz.forest.dailyshop.api.config.record.shop;

import cn.encmys.ykdz.forest.dailyshop.api.shop.cashier.record.MerchantRecord;
import org.jetbrains.annotations.NotNull;

public record ShopSettingsRecord(int size, @NotNull String name,
                                 boolean autoRestockEnabled, long autoRestockPeriod,
                                 @NotNull MerchantRecord merchant) {
}
