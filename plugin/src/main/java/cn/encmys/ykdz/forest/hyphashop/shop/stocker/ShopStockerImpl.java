package cn.encmys.ykdz.forest.hyphashop.shop.stocker;

import cn.encmys.ykdz.forest.hyphascript.context.Context;
import cn.encmys.ykdz.forest.hyphashop.api.HyphaShop;
import cn.encmys.ykdz.forest.hyphashop.api.product.Product;
import cn.encmys.ykdz.forest.hyphashop.api.product.enums.ProductType;
import cn.encmys.ykdz.forest.hyphashop.api.product.factory.ProductFactory;
import cn.encmys.ykdz.forest.hyphashop.api.shop.Shop;
import cn.encmys.ykdz.forest.hyphashop.api.shop.stocker.ShopStocker;
import cn.encmys.ykdz.forest.hyphashop.product.BundleProduct;
import cn.encmys.ykdz.forest.hyphashop.scheduler.Scheduler;
import cn.encmys.ykdz.forest.hyphashop.utils.ScriptUtils;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Unmodifiable;

import java.util.*;

public class ShopStockerImpl implements ShopStocker {
    private static final Random random = new Random();

    private final @NotNull Shop shop;
    private final int size;
    private final @NotNull List<String> allProductsId;
    private final @NotNull List<String> listedProducts = new ArrayList<>();
    private final boolean autoRestockEnabled;
    private final long autoRestockPeriod;
    private long lastRestocking;

    public ShopStockerImpl(@NotNull Shop shop, int size, boolean autoRestockEnabled, long autoRestockPeriod, @NotNull List<String> allProductsId) {
        this.shop = shop;
        this.size = size;
        this.autoRestockEnabled = autoRestockEnabled;
        this.autoRestockPeriod = autoRestockPeriod;
        this.allProductsId = allProductsId;
    }

    @Override
    public boolean needAutoRestock() {
        return autoRestockEnabled;
    }

    @Override
    public void stock() {
        List<Product> productsPreparedToBeListed = new ArrayList<>();
        ProductFactory productFactory = HyphaShop.PRODUCT_FACTORY;

        listedProducts.clear();
        // 映射为 productId : product
        Map<String, Product> allProducts = new HashMap<>();
        for (String productId : allProductsId) {
            Product product = productFactory.getProduct(productId);
            if (product != null) {
                allProducts.put(productId, product);
            }
        }

        // size == -1 代表该商店尺寸无限
        if (size == -1 || size >= allProductsId.size()) {
            productsPreparedToBeListed.addAll(allProducts.values());
        } else {
            // 按照权重算法上架商品
            List<String> temp = new ArrayList<>(allProductsId);
            int totalWeight = allProducts.values().stream()
                    .mapToInt(p -> p.getRarity().weight()).sum();

            int productsAdded = 0;

            while (productsAdded < size && !temp.isEmpty()) {
                int randomValue = random.nextInt(totalWeight) + 1; // 避免出现 0
                int cumulativeWeight = 0;

                for (String productId : temp) {
                    Product product = allProducts.get(productId);
                    cumulativeWeight += product.getRarity().weight();
                    if (randomValue <= cumulativeWeight) {
                        // 根据 list-conditions 判断是否可以被上架
                        List<String> listConditions = product.getListConditions();
                        boolean conditionFlag = true;
                        if (!listConditions.isEmpty()) {
                            Map<String, Object> vars = new HashMap<>() {{
                                put("product-id", productId);
                                put("shop-id", shop.getId());
                            }};
                            // shop -> global
                            Context ctx = ScriptUtils.buildContext(
                                    shop.getScriptContext(),
                                    vars
                            );
                            for (String condition : listConditions) {
                                if (!ScriptUtils.evaluateBoolean(ctx, condition)) {
                                    conditionFlag = false;
                                    break;
                                }
                            }
                        }
                        if (conditionFlag) {
                            productsPreparedToBeListed.add(product);
                            totalWeight -= product.getRarity().weight();
                            temp.remove(productId);
                            productsAdded++;
                            break;
                        }
                    }
                }
            }
        }

        // Event
//        ShopPreRestockEvent shopPreRestockEvent = new ShopPreRestockEvent(getShop(), productsPreparedToBeListed);
//        Bukkit.getPluginManager().callEvent(shopPreRestockEvent);
//        if (shopPreRestockEvent.isCancelled()) {
//            return;
//        }
        // Event

        // 逐个上架
        productsPreparedToBeListed.forEach(this::listProduct);

        lastRestocking = System.currentTimeMillis();

        // 防止玩家看到未刷新的内容
        Scheduler.runTask((task) -> shop.getShopGUI().closeAll());

        // Event
//        ShopRestockEvent shopRestockEvent = new ShopRestockEvent(getShop(), productsPreparedToBeListed);
//        Bukkit.getPluginManager().callEvent(shopRestockEvent);
        // Event
    }

    @Override
    public void listProduct(@NotNull Product product) {
        // Event
//        ProductPreListEvent productPreListEvent = new ProductPreListEvent(getShop(), product);
//        Bukkit.getPluginManager().callEvent(productPreListEvent);
//        if (productPreListEvent.isCancelled()) {
//            return;
//        }
        // Event

        String productId = product.getId();

        shop.getShopCounter().cacheAmount(productId);
        shop.getShopPricer().cachePrice(productId);
        if (product.isProductItemCacheable() && !shop.isProductItemCached(productId)) {
            shop.cacheProductItem(product);
        }

        // 确保每个捆绑包内容都有价格和数量缓存
        // 同时尝试缓存内容的商品物品
        if (product.getType() == ProductType.BUNDLE) {
            for (String contentId : ((BundleProduct) product).getBundleContents().keySet()) {
                Product content = HyphaShop.PRODUCT_FACTORY.getProduct(contentId);
                if (content != null) {
                    shop.getShopCounter().cacheAmount(contentId);
                    shop.getShopPricer().cachePrice(contentId);
                    if (content.isProductItemCacheable() && !shop.isProductItemCached(contentId)) {
                        shop.cacheProductItem(content);
                    }
                }
            }
        }

        // 若商品上架则补充其库存
        product.getProductStock().stock();
        // 尝试重置商人模式余额
        shop.getShopCashier().restockMerchant();

        listedProducts.add(productId);

        // Event
//        ProductListEvent productListEvent = new ProductListEvent(getShop(), product);
//        Bukkit.getPluginManager().callEvent(productListEvent);
        // Event
    }

    @Override
    public long getLastRestocking() {
        return lastRestocking;
    }

    @Override
    public void setLastRestocking(long lastRestocking) {
        this.lastRestocking = lastRestocking;
    }

    @Override
    public long getAutoRestockPeriod() {
        return autoRestockPeriod;
    }

    @Override
    public @NotNull @Unmodifiable List<String> getListedProducts() {
        return Collections.unmodifiableList(listedProducts);
    }

    @Override
    public @NotNull @Unmodifiable List<String> getAllProductsId() {
        return Collections.unmodifiableList(allProductsId);
    }

    @Override
    public boolean isListedProduct(@NotNull String id) {
        return listedProducts.contains(id);
    }

    @Override
    public void addListedProducts(@NotNull List<String> listedProducts) {
        this.listedProducts.addAll(listedProducts);
    }

    @Override
    @NotNull
    public Shop getShop() {
        return shop;
    }

    @Override
    public int getSize() {
        return size;
    }
}
