package cn.encmys.ykdz.forest.dailyshop.shop.factory;

import cn.encmys.ykdz.forest.dailyshop.api.DailyShop;
import cn.encmys.ykdz.forest.dailyshop.api.config.ProductConfig;
import cn.encmys.ykdz.forest.dailyshop.api.config.ShopConfig;
import cn.encmys.ykdz.forest.dailyshop.api.database.schema.ShopData;
import cn.encmys.ykdz.forest.dailyshop.api.shop.Shop;
import cn.encmys.ykdz.forest.dailyshop.api.shop.factory.ShopFactory;
import cn.encmys.ykdz.forest.dailyshop.shop.ShopImpl;
import cn.encmys.ykdz.forest.dailyshop.api.utils.LogUtils;

import javax.management.openmbean.InvalidKeyException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.ExecutionException;

public class ShopFactoryImpl implements ShopFactory {
    private static final HashMap<String, Shop> shops = new HashMap<>();

    public ShopFactoryImpl() {
        load();
    }

    @Override
    public void load() {
        for (String id : ShopConfig.getAllId()) {
            buildShop(id);
        }
    }

    @Override
    public Shop buildShop(String id) {
        if (shops.containsKey(id)) {
            throw new InvalidKeyException("Shop ID is duplicated: " + id);
        }

        List<String> products = new ArrayList<>();

        for (String productId : ShopConfig.getAllProductsId(id)) {
            // 处理 PACK:XXX 的包导入格式
            if (productId.startsWith("PACK:")) {
                products.addAll(ProductConfig.getAllProductId(productId.substring(5)));
                continue;
            }

            products.add(productId);
        }

        // 检查商店导入的商品是否存在
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
                ShopConfig.getRestockPeriod(id),
                products,
                ShopConfig.getSize(id)
        );

        // 从数据库加载一系列商店数据
        try {
            ShopData data = DailyShop.DATABASE.queryShopData(id).get();
            if (data != null) {
                if (!data.listedProducts().isEmpty()) {
                    shop.getShopStocker().addListedProducts(data.listedProducts());
                }
                if (!data.cachedPrices().isEmpty()) {
                    shop.getShopPricer().setCachedPrices(data.cachedPrices());
                }
                shop.getShopStocker().setLastRestocking(data.lastRestocking());
            } else {
                shop.getShopStocker().restock();
            }
        } catch (InterruptedException | ExecutionException e) {
            throw new RuntimeException(e);
        }
        // 加载完成

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
        DailyShop.DATABASE.saveShopData(getAllShops().values().stream().toList());
    }
}
