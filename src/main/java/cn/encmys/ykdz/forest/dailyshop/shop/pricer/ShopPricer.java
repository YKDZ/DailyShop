package cn.encmys.ykdz.forest.dailyshop.shop.pricer;

import cn.encmys.ykdz.forest.dailyshop.DailyShop;
import cn.encmys.ykdz.forest.dailyshop.api.product.Product;
import cn.encmys.ykdz.forest.dailyshop.price.Price;
import cn.encmys.ykdz.forest.dailyshop.price.PricePair;
import cn.encmys.ykdz.forest.dailyshop.product.BundleProduct;
import cn.encmys.ykdz.forest.dailyshop.shop.Shop;
import com.google.gson.annotations.Expose;
import org.jetbrains.annotations.NotNull;

import java.util.HashMap;
import java.util.Map;

public class ShopPricer {
    private final Shop shop;
    @Expose
    private Map<String, PricePair> cachedPrices = new HashMap<>();

    public ShopPricer(@NotNull Shop shop) {
        this.shop = shop;
    }

    public double getBuyPrice(@NotNull String productId) {
        return cachedPrices.get(productId).getBuy();
    }

    public double getSellPrice(@NotNull String productId) {
        return cachedPrices.get(productId).getSell();
    }

    public void cachePrice(@NotNull String productId) {
        Product product = DailyShop.getProductFactory().getProduct(productId);
        Price buyPrice = product.getBuyPrice();
        Price sellPrice = product.getSellPrice();
        double buy = 0d;
        double sell = 0d;

        // Handle bundle price mode
        switch (buyPrice.getPriceMode()) {
            case BUNDLE_AUTO_NEW -> {
                for (Map.Entry<String, Integer> entry : ((BundleProduct) product).getBundleContents().entrySet()) {
                    String contentId = entry.getKey();
                    int contentStack = entry.getValue();
                    Product content = DailyShop.getProductFactory().getProduct(contentId);
                    buy += content.getBuyPrice().getNewPrice() * contentStack;
                }
            } case BUNDLE_AUTO_REUSE -> {
                for (Map.Entry<String, Integer> entry : ((BundleProduct) product).getBundleContents().entrySet()) {
                    String contentId = entry.getKey();
                    int contentStack = entry.getValue();
                    buy += getBuyPrice(contentId) * contentStack;
                }
            } default -> buy = buyPrice.getNewPrice();
        }

        switch (sellPrice.getPriceMode()) {
            case BUNDLE_AUTO_NEW -> {
                for (Map.Entry<String, Integer> entry : ((BundleProduct) product).getBundleContents().entrySet()) {
                    String contentId = entry.getKey();
                    int contentStack = entry.getValue();
                    Product content = DailyShop.getProductFactory().getProduct(contentId);
                    sell += content.getSellPrice().getNewPrice() * contentStack;
                }
            } case BUNDLE_AUTO_REUSE -> {
                for (Map.Entry<String, Integer> entry : ((BundleProduct) product).getBundleContents().entrySet()) {
                    String contentId = entry.getKey();
                    int contentStack = entry.getValue();
                    sell += getSellPrice(contentId) * contentStack;
                }
            } default -> sell = sellPrice.getNewPrice();
        }

        cachedPrices.put(productId, getModifiedPricePair(new PricePair(buy, sell)));
    }

    public PricePair getModifiedPricePair(@NotNull PricePair pricePair) {
        return pricePair;
    }

    public void setCachedPrices(@NotNull Map<String, PricePair> cachedPrices) {
        this.cachedPrices = cachedPrices;
    }

    public Map<String, PricePair> getCachedPrices() {
        return cachedPrices;
    }
}
