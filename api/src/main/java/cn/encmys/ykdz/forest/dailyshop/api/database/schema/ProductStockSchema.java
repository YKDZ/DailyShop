package cn.encmys.ykdz.forest.dailyshop.api.database.schema;

import cn.encmys.ykdz.forest.dailyshop.api.product.stock.ProductStock;
import org.jetbrains.annotations.NotNull;

import java.util.Map;
import java.util.UUID;

public record ProductStockSchema(@NotNull String productId, @NotNull Map<UUID, Integer> currentPlayerAmount,
                                 int currentGlobalAmount) {
    public static ProductStockSchema of(ProductStock stock) {
        return new ProductStockSchema(stock.getProductId(), stock.getCurrentPlayerAmount(), stock.getCurrentGlobalAmount());
    }
}
