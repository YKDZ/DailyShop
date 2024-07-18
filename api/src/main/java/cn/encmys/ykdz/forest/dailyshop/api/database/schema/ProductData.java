package cn.encmys.ykdz.forest.dailyshop.api.database.schema;

import cn.encmys.ykdz.forest.dailyshop.api.product.stock.ProductStock;
import org.jetbrains.annotations.NotNull;

public record ProductData(@NotNull String id, @NotNull ProductStock stock) {
}
