package cn.encmys.ykdz.forest.dailyshop.shop.pricer;

import cn.encmys.ykdz.forest.dailyshop.api.DailyShop;
import cn.encmys.ykdz.forest.dailyshop.api.config.Config;
import cn.encmys.ykdz.forest.dailyshop.api.price.Price;
import cn.encmys.ykdz.forest.dailyshop.api.price.PricePair;
import cn.encmys.ykdz.forest.dailyshop.api.product.Product;
import cn.encmys.ykdz.forest.dailyshop.api.shop.Shop;
import cn.encmys.ykdz.forest.dailyshop.api.shop.cashier.log.SettlementLog;
import cn.encmys.ykdz.forest.dailyshop.api.shop.order.enums.OrderType;
import cn.encmys.ykdz.forest.dailyshop.api.shop.pricer.ShopPricer;
import cn.encmys.ykdz.forest.dailyshop.api.utils.LogUtils;
import cn.encmys.ykdz.forest.dailyshop.api.utils.TextUtils;
import cn.encmys.ykdz.forest.dailyshop.price.PricePairImpl;
import cn.encmys.ykdz.forest.dailyshop.product.BundleProduct;
import cn.encmys.ykdz.forest.dailyshop.shop.ShopImpl;
import org.jetbrains.annotations.NotNull;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutionException;

public class ShopPricerImpl implements ShopPricer {
    private final Shop shop;
    private Map<String, PricePair> cachedPrices = new HashMap<>();

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
        Product product = DailyShop.PRODUCT_FACTORY.getProduct(productId);
        if (product == null) {
            LogUtils.warn("Try to cache product " + productId + " which does not exist.");
            return;
        }
        Price buyPrice = product.getBuyPrice();
        Price sellPrice = product.getSellPrice();
        double buy = 0d;
        double sell = 0d;

        Map<String, String> additionalVars = new HashMap<>() {{
            put("amount", String.valueOf(product.getItemDecorator() != null ? product.getItemDecorator().getAmount() : product.getIconDecorator().getAmount()));
        }};

        switch (buyPrice.getPriceMode()) {
            case FORMULA -> {
                int historyBuy = getHistoryAmountFromLogs(shop.getId(), productId, Config.logUsageLimit_timeRange, Config.logUsageLimit_entryAmount, OrderType.SELL_TO);
                int historySell = getHistoryAmountFromLogs(shop.getId(), productId, Config.logUsageLimit_timeRange, Config.logUsageLimit_entryAmount, OrderType.BUY_FROM, OrderType.BUY_ALL_FROM);
                Map<String, String> vars = buyPrice.getFormulaVars();
                vars.put("history-buy", Integer.toString(historyBuy));
                vars.put("history-sell", Integer.toString(historySell));
                vars.putAll(additionalVars);
                double price = TextUtils.evaluateFormula(buyPrice.getFormula(), vars);
                buy = buyPrice.isRound() ? Math.round(price) : price;
            }
            case BUNDLE_AUTO_NEW -> {
                for (Map.Entry<String, Integer> entry : ((BundleProduct) product).getBundleContents().entrySet()) {
                    String contentId = entry.getKey();
                    int contentStack = entry.getValue();
                    Product content = DailyShop.PRODUCT_FACTORY.getProduct(contentId);
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
                int historyBuy = getHistoryAmountFromLogs(shop.getId(), productId, Config.logUsageLimit_timeRange, Config.logUsageLimit_entryAmount, OrderType.SELL_TO);
                int historySell = getHistoryAmountFromLogs(shop.getId(), productId, Config.logUsageLimit_timeRange, Config.logUsageLimit_entryAmount, OrderType.BUY_FROM, OrderType.BUY_ALL_FROM);
                Map<String, String> vars = sellPrice.getFormulaVars();
                vars.put("history-buy", Integer.toString(historyBuy));
                vars.put("history-sell", Integer.toString(historySell));
                vars.putAll(additionalVars);
                double price = TextUtils.evaluateFormula(sellPrice.getFormula(), vars);
                sell = sellPrice.isRound() ? Math.round(price) : price;
            }
            case BUNDLE_AUTO_NEW -> {
                for (Map.Entry<String, Integer> entry : ((BundleProduct) product).getBundleContents().entrySet()) {
                    String contentId = entry.getKey();
                    int contentStack = entry.getValue();
                    Product content = DailyShop.PRODUCT_FACTORY.getProduct(contentId);
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
    public PricePair getModifiedPricePair(@NotNull String productId, @NotNull PricePair pricePair) {
        return pricePair;
    }

    @Override
    public Map<String, PricePair> getCachedPrices() {
        return cachedPrices;
    }

    @Override
    public void setCachedPrices(@NotNull Map<String, PricePair> cachedPrices) {
        this.cachedPrices = cachedPrices;
    }

    private int getHistoryAmountFromLogs(@NotNull String shopId, @NotNull String productId, long timeLimitInDay, int numEntries, @NotNull OrderType... types) {
        int totalSales = 0;

        List<SettlementLog> logs;
        try {
            logs = DailyShop.DATABASE.queryLogs(shopId, null, null, timeLimitInDay, numEntries, types).get();
        } catch (InterruptedException | ExecutionException e) {
            throw new RuntimeException(e);
        }

        // 计算总销售量
        for (SettlementLog log : logs) {
            List<String> productIds = log.getOrderedProductIds();
            List<Integer> productStacks = log.getOrderedProductStacks();

            for (int i = 0; i < productIds.size(); i++) {
                if (productIds.get(i).equals(productId)) {
                    totalSales += productStacks.get(i);
                }
            }
        }

        return totalSales;
    }
}
