package cn.encmys.ykdz.forest.hyphashop.shop.pricer;

import cn.encmys.ykdz.forest.hyphascript.context.Context;
import cn.encmys.ykdz.forest.hyphashop.api.HyphaShop;
import cn.encmys.ykdz.forest.hyphashop.api.price.Price;
import cn.encmys.ykdz.forest.hyphashop.api.price.PricePair;
import cn.encmys.ykdz.forest.hyphashop.api.product.Product;
import cn.encmys.ykdz.forest.hyphashop.api.shop.Shop;
import cn.encmys.ykdz.forest.hyphashop.api.shop.order.enums.OrderType;
import cn.encmys.ykdz.forest.hyphashop.api.shop.pricer.ShopPricer;
import cn.encmys.ykdz.forest.hyphashop.config.Config;
import cn.encmys.ykdz.forest.hyphashop.price.PricePairImpl;
import cn.encmys.ykdz.forest.hyphashop.product.BundleProduct;
import cn.encmys.ykdz.forest.hyphashop.shop.ShopImpl;
import cn.encmys.ykdz.forest.hyphashop.utils.LogUtils;
import cn.encmys.ykdz.forest.hyphashop.utils.ScriptUtils;
import cn.encmys.ykdz.forest.hyphashop.utils.SettlementLogUtils;
import cn.encmys.ykdz.forest.hyphashop.utils.VarUtils;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Unmodifiable;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

public class ShopPricerImpl implements ShopPricer {
    private final @NotNull Shop shop;
    private @NotNull Map<String, PricePair> cachedPrices = new HashMap<>();

    public ShopPricerImpl(@NotNull ShopImpl shop) {
        this.shop = shop;
    }

    @Override
    public double getBuyPrice(@NotNull String productId) {
        PricePair pricePair = cachedPrices.get(productId);
        if (pricePair == null) {
            LogUtils.warn("Product " + productId + " do not have buy-price cached. This is likely a bug.");
            return -1d;
        }
        return pricePair.getBuy();
    }

    @Override
    public double getSellPrice(@NotNull String productId) {
        PricePair pricePair = cachedPrices.get(productId);
        if (pricePair == null) {
            LogUtils.warn("Product " + productId + " do not have sell-price cached. This is likely a bug.");
            return -1d;
        }
        return pricePair.getSell();
    }

    @Override
    public void cachePrice(@NotNull String productId) {
        Product product = HyphaShop.PRODUCT_FACTORY.getProduct(productId);
        if (product == null) {
            LogUtils.warn("Try to cache price for product " + productId + " which does not exist.");
            return;
        }
        Price buyPrice = product.getBuyPrice();
        Price sellPrice = product.getSellPrice();
        double buy = 0d;
        double sell = 0d;

        Map<String, Object> vars = VarUtils.extractVars(shop, product);

        switch (buyPrice.getPriceMode()) {
            case FORMULA -> {
                Context ctx = ScriptUtils.buildContext(ScriptUtils.linkContext(
                        buyPrice.getScriptContext().clone(),
                        product.getScriptContext().clone(),
                        shop.getScriptContext().clone()
                ), vars);
                buy = ScriptUtils.evaluateDouble(ctx, buyPrice.getFormula());
            }
            case BUNDLE_AUTO_NEW -> {
                for (Map.Entry<String, Integer> entry : ((BundleProduct) product).getBundleContents().entrySet()) {
                    String contentId = entry.getKey();
                    int contentStack = entry.getValue();
                    Product content = HyphaShop.PRODUCT_FACTORY.getProduct(contentId);
                    if (content == null) {
                        return;
                    }
                    buy += content.getBuyPrice().getNewPrice() * contentStack;
                }
            }
            case BUNDLE_AUTO_REUSE -> {
                for (Map.Entry<String, Integer> entry : ((BundleProduct) product).getBundleContents().entrySet()) {
                    String contentId = entry.getKey();
                    int contentStack = entry.getValue();
                    buy += getBuyPrice(contentId) * contentStack;
                }
            }
            default -> buy = buyPrice.getNewPrice();
        }

        switch (sellPrice.getPriceMode()) {
            case FORMULA -> {
                // price -> product -> shop -> global
                Context ctx = ScriptUtils.buildContext(ScriptUtils.linkContext(
                        sellPrice.getScriptContext().clone(),
                        product.getScriptContext().clone(),
                        shop.getScriptContext().clone()
                ), vars);
                sell = ScriptUtils.evaluateDouble(ctx, sellPrice.getFormula());
            }
            case BUNDLE_AUTO_NEW -> {
                for (Map.Entry<String, Integer> entry : ((BundleProduct) product).getBundleContents().entrySet()) {
                    String contentId = entry.getKey();
                    int contentStack = entry.getValue();
                    Product content = HyphaShop.PRODUCT_FACTORY.getProduct(contentId);
                    if (content == null) {
                        LogUtils.warn("Bundle product " + productId + " may have an invalid content " + contentId + ".");
                        continue;
                    }
                    sell += content.getSellPrice().getNewPrice() * contentStack;
                }
            }
            case BUNDLE_AUTO_REUSE -> {
                for (Map.Entry<String, Integer> entry : ((BundleProduct) product).getBundleContents().entrySet()) {
                    String contentId = entry.getKey();
                    int contentStack = entry.getValue();
                    sell += getSellPrice(contentId) * contentStack;
                }
            }
            default -> sell = sellPrice.getNewPrice();
        }

        // 避免 buy-price <= sell-price 的刷钱漏洞
        // 因为难以保证动态价格落入指定范围
        if (buy != -1 && buy <= sell) {
            // 根据配置禁用出售或收购
            if (Config.priceCorrectByDisableSellOrBuy) {
                LogUtils.warn("The current buy-price of product " + productId + " is " + buy + ", which is less than the sell-price of " + sell + ". Sell has been disabled.");
                sell = -1d;
            } else {
                LogUtils.warn("The current buy-price of product " + productId + " is " + buy + ", which is less than the sell-price of " + sell + ". Buy has been disabled.");
                buy = -1d;
            }
        }

        cachedPrices.put(productId, getModifiedPricePair(productId, new PricePairImpl(buy, sell)));
    }

    @Override
    public @NotNull PricePair getModifiedPricePair(@NotNull String productId, @NotNull PricePair pricePair) {
        return pricePair;
    }

    @Override
    public @NotNull @Unmodifiable Map<String, PricePair> getCachedPrices() {
        return Collections.unmodifiableMap(cachedPrices);
    }

    @Override
    public @NotNull Shop getShop() {
        return shop;
    }

    @Override
    public void setCachedPrices(@NotNull Map<String, PricePair> cachedPrices) {
        this.cachedPrices = cachedPrices;
    }
}
