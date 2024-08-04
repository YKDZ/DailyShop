package cn.encmys.ykdz.forest.dailyshop.shop.stocker;

import cn.encmys.ykdz.forest.dailyshop.api.DailyShop;
import cn.encmys.ykdz.forest.dailyshop.api.event.product.ProductListEvent;
import cn.encmys.ykdz.forest.dailyshop.api.event.product.ProductPreListEvent;
import cn.encmys.ykdz.forest.dailyshop.api.event.shop.ShopPreRestockEvent;
import cn.encmys.ykdz.forest.dailyshop.api.event.shop.ShopRestockEvent;
import cn.encmys.ykdz.forest.dailyshop.api.product.Product;
import cn.encmys.ykdz.forest.dailyshop.api.product.enums.ProductType;
import cn.encmys.ykdz.forest.dailyshop.api.product.factory.ProductFactory;
import cn.encmys.ykdz.forest.dailyshop.api.shop.Shop;
import cn.encmys.ykdz.forest.dailyshop.api.shop.stocker.ShopStocker;
import cn.encmys.ykdz.forest.dailyshop.product.BundleProduct;
import org.bukkit.Bukkit;
import org.jetbrains.annotations.NotNull;

import java.util.*;

public class ShopStockerImpl implements ShopStocker {
    private static final Random random = new Random();
    private final Shop shop;
    private final List<String> allProductsId;
    private final List<String> listedProducts = new ArrayList<>();
    private final boolean restockEnabled;
    private final long restockPeriod;
    private long lastRestocking;

    public ShopStockerImpl(@NotNull Shop shop, boolean restockEnabled, long restockPeriod, List<String> allProductsId) {
        this.shop = shop;
        this.restockEnabled = restockEnabled;
        this.restockPeriod = restockPeriod;
        this.allProductsId = allProductsId;
    }

    @Override
    public boolean needRestock() {
        return restockEnabled;
    }

    @Override
    public void stock() {
        List<Product> productsPreparedToBeListed = new ArrayList<>();
        ProductFactory productFactory = DailyShop.PRODUCT_FACTORY;

        listedProducts.clear();
        // 映射为 productId : product
        Map<String, Product> allProducts = new HashMap<>();
        for (String productId : allProductsId) {
            Product product = productFactory.getProduct(productId);
            if (product != null) {
                allProducts.put(productId, product);
            }
        }

        if (getShop().getSize() >= allProductsId.size()) {
            productsPreparedToBeListed.addAll(allProducts.values());
        } else {
            List<String> temp = new ArrayList<>(allProductsId);
            int totalWeight = allProducts.values().stream()
                    .mapToInt(p -> p.getRarity().weight()).sum();

            for (int i = 0; i < getShop().getSize(); i++) {
                int needed = random.nextInt(totalWeight) + 1; // 避免出现 0
                int runningWeight = 0;
                for (String productId : temp) {
                    Product product = allProducts.get(productId);
                    runningWeight += product.getRarity().weight();
                    if (needed <= runningWeight) {
                        productsPreparedToBeListed.add(product);
                        totalWeight -= product.getRarity().weight();
                        temp.remove(productId);
                        break;
                    }
                }
            }
        }

        // Event
        ShopPreRestockEvent shopPreRestockEvent = new ShopPreRestockEvent(getShop(), productsPreparedToBeListed);
        Bukkit.getPluginManager().callEvent(shopPreRestockEvent);
        if (shopPreRestockEvent.isCancelled()) {
            return;
        }
        // Event

        boolean cacheGUIMarker = true;
        for (Product product : productsPreparedToBeListed) {
            if (cacheGUIMarker && (product.getProductStock().isPlayerStock() || product.getProductStock().isPlayerStock())) {
                cacheGUIMarker = false;
            }
            listProduct(product);
        }

        getShop().getShopGUI().closeAll();

        lastRestocking = System.currentTimeMillis();

        // Event
        ShopRestockEvent shopRestockEvent = new ShopRestockEvent(getShop(), productsPreparedToBeListed);
        Bukkit.getPluginManager().callEvent(shopRestockEvent);
        // Event
    }

    @Override
    public void listProduct(@NotNull Product product) {
        // Event
        ProductPreListEvent productPreListEvent = new ProductPreListEvent(getShop(), product);
        Bukkit.getPluginManager().callEvent(productPreListEvent);
        if (productPreListEvent.isCancelled()) {
            return;
        }
        // Event

        String productId = product.getId();

        shop.getShopPricer().cachePrice(productId);
        if (product.isProductItemCacheable() && !shop.isCached(productId)) {
            shop.cacheProductItem(product);
        }

        // 确保每个捆绑包内容都有价格缓存
        // 同时尝试缓存商品物品
        if (product.getType() == ProductType.BUNDLE) {
            for (String contentId : ((BundleProduct) product).getBundleContents().keySet()) {
                Product content = DailyShop.PRODUCT_FACTORY.getProduct(contentId);
                if (content != null) {
                    shop.getShopPricer().cachePrice(contentId);
                    if (product.isProductItemCacheable()) {
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
        ProductListEvent productListEvent = new ProductListEvent(getShop(), product);
        Bukkit.getPluginManager().callEvent(productListEvent);
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
    public long getRestockPeriod() {
        return restockPeriod;
    }

    @Override
    public List<String> getListedProducts() {
        return listedProducts;
    }

    @Override
    public List<String> getAllProductsId() {
        return allProductsId;
    }

    @Override
    public boolean isListedProduct(String id) {
        return listedProducts.contains(id);
    }

    @Override
    public void addListedProducts(List<String> listedProducts) {
        this.listedProducts.addAll(listedProducts);
    }

    @NotNull
    public Shop getShop() {
        return shop;
    }
}
