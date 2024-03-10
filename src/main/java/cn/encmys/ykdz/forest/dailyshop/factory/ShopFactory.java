package cn.encmys.ykdz.forest.dailyshop.factory;

import cn.encmys.ykdz.forest.dailyshop.config.ProductConfig;
import cn.encmys.ykdz.forest.dailyshop.config.ShopConfig;
import cn.encmys.ykdz.forest.dailyshop.shop.Shop;

import javax.management.openmbean.InvalidKeyException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class ShopFactory {
    private static final HashMap<String, Shop> shops = new HashMap<>();

    public ShopFactory() {
        for (String id : ShopConfig.getAllId()) {
            buildShop(id);
        }
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

    public void unload() {
        for (Shop shop : shops.values()) {
            shop.saveData();
        }
        shops.clear();
    }
}
