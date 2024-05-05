package cn.encmys.ykdz.forest.dailyshop.shop.pricer;

import cn.encmys.ykdz.forest.dailyshop.DailyShop;
import cn.encmys.ykdz.forest.dailyshop.api.product.Product;
import cn.encmys.ykdz.forest.dailyshop.api.shop.pricer.ShopPricer;
import cn.encmys.ykdz.forest.dailyshop.config.Config;
import cn.encmys.ykdz.forest.dailyshop.price.Price;
import cn.encmys.ykdz.forest.dailyshop.price.PricePair;
import cn.encmys.ykdz.forest.dailyshop.product.BundleProduct;
import cn.encmys.ykdz.forest.dailyshop.shop.ShopImpl;
import cn.encmys.ykdz.forest.dailyshop.shop.cashier.log.enums.SettlementLogType;
import cn.encmys.ykdz.forest.dailyshop.util.TextUtils;
import org.jetbrains.annotations.NotNull;

import java.util.HashMap;
import java.util.Map;

public class ShopPricerImpl implements ShopPricer {
    private final ShopImpl shop;
    private Map<String, PricePair> cachedPrices = new HashMap<>();

    public ShopPricerImpl(@NotNull ShopImpl shop) {
        this.shop = shop;
    }

    @Override
    public double getBuyPrice(@NotNull String productId) {
        return cachedPrices.get(productId).getBuy();
    }

    @Override
    public double getSellPrice(@NotNull String productId) {
        return cachedPrices.get(productId).getSell();
    }

    @Override
    public void cachePrice(@NotNull String productId) {
        Product product = DailyShop.PRODUCT_FACTORY.getProduct(productId);
        Price buyPrice = product.getBuyPrice();
        Price sellPrice = product.getSellPrice();
        double buy = 0d;
        double sell = 0d;

        int historyBuy = DailyShop.DATABASE.queryHistoryAmountFromLogs(shop.getId(), productId, Config.logDataLimit_timeRange, Config.logDataLimit_entryAmount, SettlementLogType.SELL_TO);
        int historySell = DailyShop.DATABASE.queryHistoryAmountFromLogs(shop.getId(), productId, Config.logDataLimit_timeRange, Config.logDataLimit_entryAmount, SettlementLogType.BUY_FROM, SettlementLogType.BUY_ALL_FROM);

        // Handle special price mode
        switch (buyPrice.getPriceMode()) {
            case FORMULA -> {
                Map<String, String> vars = buyPrice.getFormulaVars();
                vars.put("history-buy", Integer.toString(historyBuy));
                vars.put("history-sell", Integer.toString(historySell));
                double price = TextUtils.evaluateFormula(buyPrice.getFormula(), vars);
                buy = buyPrice.isRound() ? Math.round(price) : price;
            } case BUNDLE_AUTO_NEW -> {
                for (Map.Entry<String, Integer> entry : ((BundleProduct) product).getBundleContents().entrySet()) {
                    String contentId = entry.getKey();
                    int contentStack = entry.getValue();
                    Product content = DailyShop.PRODUCT_FACTORY.getProduct(contentId);
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
            case FORMULA -> {
                Map<String, String> vars = sellPrice.getFormulaVars();
                vars.put("history-buy", Integer.toString(historyBuy));
                vars.put("history-sell", Integer.toString(historySell));
                double price = TextUtils.evaluateFormula(sellPrice.getFormula(), vars);
                sell = sellPrice.isRound() ? Math.round(price) : price;
            } case BUNDLE_AUTO_NEW -> {
                for (Map.Entry<String, Integer> entry : ((BundleProduct) product).getBundleContents().entrySet()) {
                    String contentId = entry.getKey();
                    int contentStack = entry.getValue();
                    Product content = DailyShop.PRODUCT_FACTORY.getProduct(contentId);
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

        // Avoid buy-price <= sell-price
        if (buy <= sell) {
            sell = -1d;
        }

        cachedPrices.put(productId, getModifiedPricePair(productId, new PricePair(buy, sell)));
    }

    // Todo Discount or something
    @Override
    public PricePair getModifiedPricePair(@NotNull String productId, @NotNull PricePair pricePair) {
        return pricePair;
    }

    @Override
    public void setCachedPrices(@NotNull Map<String, PricePair> cachedPrices) {
        this.cachedPrices = cachedPrices;
    }

    @Override
    public Map<String, PricePair> getCachedPrices() {
        return cachedPrices;
    }
}
