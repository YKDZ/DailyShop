package cn.encmys.ykdz.forest.dailyshop.shop.pricer;

import cn.encmys.ykdz.forest.dailyshop.api.DailyShop;
import cn.encmys.ykdz.forest.dailyshop.api.config.Config;
import cn.encmys.ykdz.forest.dailyshop.api.price.Price;
import cn.encmys.ykdz.forest.dailyshop.api.price.PricePair;
import cn.encmys.ykdz.forest.dailyshop.api.product.Product;
import cn.encmys.ykdz.forest.dailyshop.api.shop.Shop;
import cn.encmys.ykdz.forest.dailyshop.api.shop.cashier.log.SettlementLog;
import cn.encmys.ykdz.forest.dailyshop.api.shop.cashier.log.enums.SettlementLogType;
import cn.encmys.ykdz.forest.dailyshop.api.shop.pricer.ShopPricer;
import cn.encmys.ykdz.forest.dailyshop.price.PricePairImpl;
import cn.encmys.ykdz.forest.dailyshop.product.BundleProduct;
import cn.encmys.ykdz.forest.dailyshop.shop.ShopImpl;
import cn.encmys.ykdz.forest.dailyshop.util.LogUtils;
import cn.encmys.ykdz.forest.dailyshop.util.TextUtils;
import org.jetbrains.annotations.NotNull;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ShopPricerImpl implements ShopPricer {
    private final Shop shop;
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

        int historyBuy = getHistoryAmountFromLogs(shop.getId(), productId, Config.logDataLimit_timeRange, Config.logDataLimit_entryAmount, SettlementLogType.SELL_TO);
        int historySell = getHistoryAmountFromLogs(shop.getId(), productId, Config.logDataLimit_timeRange, Config.logDataLimit_entryAmount, SettlementLogType.BUY_FROM, SettlementLogType.BUY_ALL_FROM);

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

        // 避免 buy-price <= sell-price 的刷钱漏洞
        // 因为难以保证动态价格落入指定范围
        if (buy <= sell) {
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

    private int getHistoryAmountFromLogs(@NotNull String shopId, @NotNull String productId, double timeLimitInDay, int numEntries, @NotNull SettlementLogType... types) {
        int totalSales = 0;

        List<SettlementLog> logs = DailyShop.DATABASE.queryLogs(shopId, null, null, timeLimitInDay, numEntries, types);

        // 计算总销售量
        for (SettlementLog log : logs) {
            List<String> productIds = log.getOrderedProductIds();
            List<Integer> productStacks = log.getOrderedProductStacks();
            int totalStack = log.getTotalStack();

            for (int i = 0; i < productIds.size(); i++) {
                if (productIds.get(i).equals(productId)) {
                    totalSales += productStacks.get(i) * totalStack;
                }
            }
        }

        return totalSales;
    }
}
