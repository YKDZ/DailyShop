package cn.encmys.ykdz.forest.dailyshop.shop.factory;

import cn.encmys.ykdz.forest.dailyshop.api.DailyShop;
import cn.encmys.ykdz.forest.dailyshop.api.config.ProductConfig;
import cn.encmys.ykdz.forest.dailyshop.api.config.ShopConfig;
import cn.encmys.ykdz.forest.dailyshop.api.price.PricePair;
import cn.encmys.ykdz.forest.dailyshop.api.shop.Shop;
import cn.encmys.ykdz.forest.dailyshop.api.shop.factory.ShopFactory;
import cn.encmys.ykdz.forest.dailyshop.shop.ShopImpl;
import cn.encmys.ykdz.forest.dailyshop.util.LogUtils;

import javax.management.openmbean.InvalidKeyException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ShopFactoryImpl implements ShopFactory {
    private static final HashMap<String, Shop> shops = new HashMap<>();

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
        for (Shop shop : getAllShops().values()) {
            shop.getShopGUI().buildGUIBuilder();
            shop.getHistoryGUI().buildGUIBuilder();
        }
    }

    @Override
    public Shop buildShop(String id) {
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

        Shop shop = new ShopImpl(
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
    public Shop getShop(String id) {
        return shops.get(id);
    }

    @Override
    public HashMap<String, Shop> getAllShops() {
        return shops;
    }

    @Override
    public void unload() {
        save();
        shops.clear();
    }

    @Override
    public void save() {
        HashMap<String, Shop> dataMap = new HashMap<>();
        for (Shop shop : getAllShops().values()) {
            dataMap.put(shop.getId(), shop);
        }
        DailyShop.DATABASE.saveShopData(dataMap);
    }
}
