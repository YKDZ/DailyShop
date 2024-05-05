package cn.encmys.ykdz.forest.dailyshop.shop.factory;

import cn.encmys.ykdz.forest.dailyshop.DailyShop;
import cn.encmys.ykdz.forest.dailyshop.config.ProductConfig;
import cn.encmys.ykdz.forest.dailyshop.config.ShopConfig;
import cn.encmys.ykdz.forest.dailyshop.price.PricePair;
import cn.encmys.ykdz.forest.dailyshop.shop.ShopImpl;
import cn.encmys.ykdz.forest.dailyshop.util.LogUtils;

import javax.management.openmbean.InvalidKeyException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ShopFactoryImpl implements cn.encmys.ykdz.forest.dailyshop.api.shop.factory.ShopFactory {
    private static final HashMap<String, ShopImpl> shops = new HashMap<>();

    public ShopFactoryImpl() {
        load();
    }

    @Override
    public void load() {
        // Build shop
        for (String id : ShopConfig.getAllId()) {
            buildShop(id);
        }

        // Build shop gui
        for (ShopImpl shop : getAllShops().values()) {
            shop.getShopGUI().buildGUIBuilder();
            shop.getHistoryGUI().buildGUIBuilder();
        }
    }

    @Override
    public ShopImpl buildShop(String id) {
        if (shops.containsKey(id)) {
            throw new InvalidKeyException("Shop ID is duplicated: " + id);
        }

        List<String> products = new ArrayList<>();

        for (String productId : ShopConfig.getAllProductsId(id)) {
            // Handle PACK:XXX format
            if (productId.startsWith("PACK:")) {
                products.addAll(ProductConfig.getAllProductId(productId.substring(5)));
                continue;
            }

            products.add(productId);
        }

        // Check whether the product actually exist.
        products = products.stream()
                .filter(productId -> {
                    if (!DailyShop.PRODUCT_FACTORY.containsProduct(productId)) {
                        LogUtils.warn("Product " + productId + " in shop " + id + " not exist.");
                        return false;
                    }
                    return true;
                })
                .toList();

        ShopImpl shop = new ShopImpl(
                id,
                ShopConfig.getName(id),
                ShopConfig.getRestockTimerSection(id),
                products,
                ShopConfig.getSize(id)
        );

        // Load data from database
        shop.setLastRestocking(DailyShop.DATABASE.queryShopLastRestocking(id));

        List<String> dataListedProducts = DailyShop.DATABASE.queryShopListedProducts(id);
        if (!dataListedProducts.isEmpty()) {
            shop.addListedProducts(dataListedProducts);
        } else {
            shop.restock();
        }

        Map<String, PricePair> dataCachedPrices = DailyShop.DATABASE.queryShopCachedPrices(id);
        if (!dataListedProducts.isEmpty()) {
            shop.getShopPricer().setCachedPrices(dataCachedPrices);
        }
        // Finish

        shops.put(id, shop);
        LogUtils.info("Successfully load shop " + id + " with " + products.size() + " products.");
        return shop;
    }

    @Override
    public ShopImpl getShop(String id) {
        return shops.get(id);
    }

    @Override
    public HashMap<String, ShopImpl> getAllShops() {
        return shops;
    }

    @Override
    public void unload() {
        save();
        shops.clear();
    }

    @Override
    public void save() {
        HashMap<String, ShopImpl> dataMap = new HashMap<>();
        for (ShopImpl shop : getAllShops().values()) {
            dataMap.put(shop.getId(), shop);
        }
        DailyShop.DATABASE.saveShopData(dataMap);
    }
}
