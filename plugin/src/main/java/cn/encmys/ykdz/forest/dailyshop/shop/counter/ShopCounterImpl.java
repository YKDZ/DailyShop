package cn.encmys.ykdz.forest.dailyshop.shop.counter;

import cn.encmys.ykdz.forest.dailyshop.api.DailyShop;
import cn.encmys.ykdz.forest.dailyshop.api.product.Product;
import cn.encmys.ykdz.forest.dailyshop.api.shop.Shop;
import cn.encmys.ykdz.forest.dailyshop.api.shop.counter.ShopCounter;
import cn.encmys.ykdz.forest.dailyshop.api.utils.ConfigUtils;
import cn.encmys.ykdz.forest.dailyshop.api.utils.LogUtils;
import org.jetbrains.annotations.NotNull;

import java.util.HashMap;
import java.util.Map;

public class ShopCounterImpl implements ShopCounter {
    private final Shop shop;
    private final Map<String, Integer> cachedAmounts = new HashMap<>();

    public ShopCounterImpl(@NotNull Shop shop) {
        this.shop = shop;
    }

    @Override
    public void cacheAmount(@NotNull String productId) {
        Product product = DailyShop.PRODUCT_FACTORY.getProduct(productId);
        if (product == null) {
            LogUtils.warn("Try to cache amount for product " + productId + " which does not exist.");
            return;
        }
        if (product.getItemDecorator() == null) {
            LogUtils.warn("Try to cache amount for product " + productId + " which is not an item product (have not amount).");
            return;
        }

        String amountConfig = product.getItemDecorator().getAmount();
        int amount;

        try {
            amount = ConfigUtils.amountFromConfig(amountConfig);
        } catch (Exception e) {
            LogUtils.warn("Error when evaluate amount formula of product " + productId + ": " + e.getMessage() + ". The amount will fallback to 1. Please check your amount config.");
            amount = 1;
        }

        cachedAmounts.put(productId, amount);
    }

    @Override
    public int getAmount(@NotNull String productId) {
        if (!cachedAmounts.containsKey(productId)) {
            LogUtils.warn("Try to get amount for product " + productId + " which does not be cached. The amount will fallback to 1. This could be a plugin issue.");
            return 1;
        }
        return cachedAmounts.get(productId);
    }

    @Override
    public Shop getShop() {
        return shop;
    }
}
