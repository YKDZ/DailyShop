package cn.encmys.ykdz.forest.hyphashop.shop.counter;

import cn.encmys.ykdz.forest.hyphascript.context.Context;
import cn.encmys.ykdz.forest.hyphashop.api.HyphaShop;
import cn.encmys.ykdz.forest.hyphashop.api.item.decorator.BaseItemDecorator;
import cn.encmys.ykdz.forest.hyphashop.api.item.decorator.enums.PropertyType;
import cn.encmys.ykdz.forest.hyphashop.api.product.Product;
import cn.encmys.ykdz.forest.hyphashop.api.shop.Shop;
import cn.encmys.ykdz.forest.hyphashop.api.shop.counter.ShopCounter;
import cn.encmys.ykdz.forest.hyphashop.utils.LogUtils;
import cn.encmys.ykdz.forest.hyphashop.utils.ScriptUtils;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Unmodifiable;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

public class ShopCounterImpl implements ShopCounter {
    @NotNull
    private final Shop shop;
    @NotNull
    private Map<String, Integer> cachedAmounts = new HashMap<>();

    public ShopCounterImpl(@NotNull Shop shop) {
        this.shop = shop;
    }

    @Override
    public void cacheAmount(@NotNull String productId) {
        Product product = HyphaShop.PRODUCT_FACTORY.getProduct(productId);
        if (product == null) {
            LogUtils.warn("Try to cache amount for product " + productId + " which does not exist.");
            return;
        }

        // 优先缓存 ItemDecorator 的数量，否则缓存 IconDecorator
        BaseItemDecorator targetDecorator = product.getIconDecorator();
        if (product.getProductItemDecorator() != null) targetDecorator = product.getProductItemDecorator();
        String amountConfig = targetDecorator.getProperty(PropertyType.AMOUNT);

        if (amountConfig == null) cachedAmounts.put(productId, 1);
        else {
            // product -> shop -> global
            Context ctx = ScriptUtils.linkContext(
                    product.getScriptContext().clone(),
                    shop.getScriptContext().clone()
            );
            cachedAmounts.put(productId, ScriptUtils.evaluateInt(ctx, amountConfig));
        }
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
    public @NotNull Shop getShop() {
        return shop;
    }

    @Override
    public @NotNull @Unmodifiable Map<String, Integer> getCachedAmounts() {
        return Collections.unmodifiableMap(cachedAmounts);
    }

    @Override
    public void setCachedAmounts(@NotNull Map<String, Integer> cachedAmounts) {
        this.cachedAmounts = cachedAmounts;
    }
}
