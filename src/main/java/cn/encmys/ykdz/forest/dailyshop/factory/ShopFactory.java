package cn.encmys.ykdz.forest.dailyshop.factory;

import cn.encmys.ykdz.forest.dailyshop.DailyShop;
import cn.encmys.ykdz.forest.dailyshop.config.ProductConfig;
import cn.encmys.ykdz.forest.dailyshop.config.ShopConfig;
import cn.encmys.ykdz.forest.dailyshop.shop.Shop;
import cn.encmys.ykdz.forest.dailyshop.util.LogUtils;

import javax.management.openmbean.InvalidKeyException;
import java.util.*;

public class ShopFactory {
    private static final HashMap<String, Shop> shops = new HashMap<>();

    public ShopFactory() {
        load();
    }

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
        Iterator<String> iterator = products.iterator();
        while (iterator.hasNext()) {
            String productId = iterator.next();
            if (!DailyShop.getProductFactory().containsProduct(productId)) {
                iterator.remove();
                LogUtils.warn("Product " + productId + " in shop " + id + " not exist.");
            }
        }

        Shop shop = new Shop(
                id,
                ShopConfig.getName(id),
                ShopConfig.getRestockTimer(id),
                products,
                ShopConfig.getSize(id),
                ShopConfig.getGUISection(id)
        );

        shops.put(id, shop);
        return shop;
    }

    public Shop getShop(String id) {
        return shops.get(id);
    }

    public HashMap<String, Shop> getAllShops() {
        return shops;
    }

    public void load() {
        // Build shop
        for (String id : ShopConfig.getAllId()) {
            buildShop(id);
        }

        // Load data to built shop
        Map<String, Shop> data = DailyShop.getDatabase().loadShopData();
        for (Map.Entry<String, Shop> entry : data.entrySet()) {
            String id = entry.getKey();
            Shop dataShop = entry.getValue();
            Shop shop = getShop(id);
            if (shop == null) {
                continue;
            }
            shop.setLastRestocking(dataShop.getLastRestocking());
            shop.setListedProducts(dataShop.getListedProducts());
            shop.setCachedPrice(dataShop.getCachedPrice());
        }

        // Build shop gui
        for (Shop shop : getAllShops().values()) {
            shop.buildGUI();
        }
    }

    public void unload() {
        HashMap<String, Shop> dataMap = new HashMap();
        for (Shop shop : getAllShops().values()) {
            dataMap.put(shop.getId(), shop);
        }
        DailyShop.getDatabase().saveShopData(dataMap);
        shops.clear();
    }

    public void clean() {

    }
}
