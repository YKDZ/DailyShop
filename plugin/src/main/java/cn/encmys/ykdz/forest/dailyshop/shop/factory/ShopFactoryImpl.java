package cn.encmys.ykdz.forest.dailyshop.shop.factory;

import cn.encmys.ykdz.forest.dailyshop.api.DailyShop;
import cn.encmys.ykdz.forest.dailyshop.api.config.ProductConfig;
import cn.encmys.ykdz.forest.dailyshop.api.config.ShopConfig;
import cn.encmys.ykdz.forest.dailyshop.api.database.schema.ShopCashierSchema;
import cn.encmys.ykdz.forest.dailyshop.api.database.schema.ShopPricerSchema;
import cn.encmys.ykdz.forest.dailyshop.api.database.schema.ShopStockerSchema;
import cn.encmys.ykdz.forest.dailyshop.api.shop.Shop;
import cn.encmys.ykdz.forest.dailyshop.api.shop.factory.ShopFactory;
import cn.encmys.ykdz.forest.dailyshop.api.utils.LogUtils;
import cn.encmys.ykdz.forest.dailyshop.shop.ShopImpl;

import javax.management.openmbean.InvalidKeyException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

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
                String packId = productId.substring(5);
                List<String> packProducts = ProductConfig.getAllProductId(packId);
                if (packProducts == null) {
                    LogUtils.warn("Product pack " + packId + ".yml in shop " + id + " not found.");
                    continue;
                }
                products.addAll(packProducts);
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
                ShopConfig.getShopSettingsRecord(id),
                products
        );

        // 从数据库加载一系列商店数据
        ShopCashierSchema cashierSchema = DailyShop.DATABASE_FACTORY.getShopCashierDao().querySchema(shop.getId());
        ShopPricerSchema pricerSchema = DailyShop.DATABASE_FACTORY.getShopPricerDao().querySchema(shop.getId());
        ShopStockerSchema stockerSchema = DailyShop.DATABASE_FACTORY.getShopStockerDao().querySchema(shop.getId());
        if (pricerSchema != null && !pricerSchema.cachedPrices().isEmpty()) {
            shop.getShopPricer().setCachedPrices(pricerSchema.cachedPrices());
        }
        if (cashierSchema != null && shop.getShopCashier().isInherit()) {
            shop.getShopCashier().setBalance(cashierSchema.balance());
        }
        if (stockerSchema != null) {
            shop.getShopStocker().setLastRestocking(stockerSchema.lastRestocking());
            shop.getShopStocker().addListedProducts(stockerSchema.listedProducts());
        } else {
            // 为新增商店填充初始商品
            shop.getShopStocker().stock();
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
    public HashMap<String, Shop> getShops() {
        return shops;
    }

    @Override
    public void unload() {
        save();
        shops.clear();
    }

    @Override
    public void save() {
        for (Shop shop : shops.values()) {
            DailyShop.DATABASE_FACTORY.getShopCashierDao().insertSchema(ShopCashierSchema.of(shop.getShopCashier()));
            DailyShop.DATABASE_FACTORY.getShopPricerDao().insertSchema(ShopPricerSchema.of(shop.getShopPricer()));
            DailyShop.DATABASE_FACTORY.getShopStockerDao().insertSchema(ShopStockerSchema.of(shop.getShopStocker()));
        }
    }
}
