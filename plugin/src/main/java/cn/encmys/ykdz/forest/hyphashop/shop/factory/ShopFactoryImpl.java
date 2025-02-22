package cn.encmys.ykdz.forest.hyphashop.shop.factory;

import cn.encmys.ykdz.forest.hyphashop.api.HyphaShop;
import cn.encmys.ykdz.forest.hyphashop.api.database.schema.ShopCashierSchema;
import cn.encmys.ykdz.forest.hyphashop.api.database.schema.ShopCounterSchema;
import cn.encmys.ykdz.forest.hyphashop.api.database.schema.ShopPricerSchema;
import cn.encmys.ykdz.forest.hyphashop.api.database.schema.ShopStockerSchema;
import cn.encmys.ykdz.forest.hyphashop.api.shop.Shop;
import cn.encmys.ykdz.forest.hyphashop.api.shop.factory.ShopFactory;
import cn.encmys.ykdz.forest.hyphashop.config.ProductConfig;
import cn.encmys.ykdz.forest.hyphashop.config.ShopConfig;
import cn.encmys.ykdz.forest.hyphashop.shop.ShopImpl;
import cn.encmys.ykdz.forest.hyphashop.utils.LogUtils;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.jetbrains.annotations.Unmodifiable;

import java.util.*;

public class ShopFactoryImpl implements ShopFactory {
    @NotNull
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
    public @NotNull Shop buildShop(@NotNull String id) {
        if (shops.containsKey(id)) {
            throw new IllegalArgumentException("Shop ID is duplicated: " + id);
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
                    if (!HyphaShop.PRODUCT_FACTORY.containsProduct(productId)) {
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
        ShopCashierSchema cashierSchema = HyphaShop.DATABASE_FACTORY.getShopCashierDao().querySchema(shop.getId());
        ShopPricerSchema pricerSchema = HyphaShop.DATABASE_FACTORY.getShopPricerDao().querySchema(shop.getId());
        ShopStockerSchema stockerSchema = HyphaShop.DATABASE_FACTORY.getShopStockerDao().querySchema(shop.getId());
        ShopCounterSchema counterSchema = HyphaShop.DATABASE_FACTORY.getShopCounterDao().querySchema(shop.getId());
        if (pricerSchema != null && !pricerSchema.cachedPrices().isEmpty()) {
            shop.getShopPricer().setCachedPrices(pricerSchema.cachedPrices());
        }
        if (cashierSchema != null && shop.getShopCashier().isInherit()) {
            shop.getShopCashier().setBalance(cashierSchema.balance());
        }
        if (counterSchema != null) {
            shop.getShopCounter().setCachedAmounts(counterSchema.cachedAmounts());
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
    public @Nullable Shop getShop(@NotNull String id) {
        return shops.get(id);
    }

    @Override
    public @NotNull @Unmodifiable Map<String, Shop> getShops() {
        return Collections.unmodifiableMap(shops);
    }

    @Override
    public void unload() {
        save();
        shops.clear();
    }

    @Override
    public void save() {
        for (Shop shop : shops.values()) {
            HyphaShop.DATABASE_FACTORY.getShopCashierDao().insertSchema(ShopCashierSchema.of(shop.getShopCashier()));
            HyphaShop.DATABASE_FACTORY.getShopPricerDao().insertSchema(ShopPricerSchema.of(shop.getShopPricer()));
            HyphaShop.DATABASE_FACTORY.getShopStockerDao().insertSchema(ShopStockerSchema.of(shop.getShopStocker()));
            HyphaShop.DATABASE_FACTORY.getShopCounterDao().insertSchema(ShopCounterSchema.of(shop.getShopCounter()));
        }
    }
}
