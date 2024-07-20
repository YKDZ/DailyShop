package cn.encmys.ykdz.forest.dailyshop.api.database.schema;

import cn.encmys.ykdz.forest.dailyshop.api.price.PricePair;

import java.util.List;
import java.util.Map;

public record ShopData(String id, List<String> listedProducts, Map<String, PricePair> cachedPrices,
                       long lastRestocking, double balance) {
}
