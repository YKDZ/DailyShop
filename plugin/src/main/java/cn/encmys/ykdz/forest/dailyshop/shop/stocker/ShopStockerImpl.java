package cn.encmys.ykdz.forest.dailyshop.shop.stocker;

import cn.encmys.ykdz.forest.dailyshop.api.DailyShop;
import cn.encmys.ykdz.forest.dailyshop.api.event.ProductListEvent;
import cn.encmys.ykdz.forest.dailyshop.api.event.ShopRestockEvent;
import cn.encmys.ykdz.forest.dailyshop.api.product.Product;
import cn.encmys.ykdz.forest.dailyshop.api.product.enums.ProductType;
import cn.encmys.ykdz.forest.dailyshop.api.product.factory.ProductFactory;
import cn.encmys.ykdz.forest.dailyshop.api.shop.Shop;
import cn.encmys.ykdz.forest.dailyshop.api.shop.stocker.ShopStocker;
import cn.encmys.ykdz.forest.dailyshop.product.BundleProduct;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.stream.Collectors;

public class ShopStockerImpl implements ShopStocker {
    private static final Random random = new Random();
    private final Shop shop;
    private final List<String> allProductsId;
    private final List<String> listedProducts = new ArrayList<>();
    private final long restockPeriod;
    private long lastRestocking;

    public ShopStockerImpl(@NotNull Shop shop, long restockPeriod, List<String> allProductsId) {
        this.shop = shop;
        this.restockPeriod = restockPeriod;
        this.allProductsId = allProductsId;
    }

    @Override
    public void restock() {
        List<Product> productsPreparedToBeListed = new ArrayList<>();
        ProductFactory productFactory = DailyShop.PRODUCT_FACTORY;

        listedProducts.clear();
        // 映射为 productId : product
        Map<String, Product> allProducts = allProductsId.stream()
                .collect(Collectors.toMap(
                        productId -> productId,
                        productFactory::getProduct
                ));

        if (getShop().getSize() >= allProductsId.size()) {
            productsPreparedToBeListed.addAll(allProducts.values());
        } else {
            List<String> temp = new ArrayList<>(allProductsId);
            int totalWeight = allProducts.values().stream()
                    .mapToInt(p -> p.getRarity().getWeight()).sum();

            for (int i = 0; i < getShop().getSize(); i++) {
                int needed = random.nextInt(totalWeight) + 1; // 避免出现 0
                int runningWeight = 0;
                for (String productId : temp) {
                    Product product = allProducts.get(productId);
                    runningWeight += product.getRarity().getWeight();
                    if (needed <= runningWeight) {
                        productsPreparedToBeListed.add(product);
                        totalWeight -= product.getRarity().getWeight();
                        temp.remove(productId);
                        break;
                    }
                }
            }
        }

        // Event
        ShopRestockEvent event = new ShopRestockEvent(getShop(), productsPreparedToBeListed);
        if (event.isCancelled()) {
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
        // 如果 GUI 内不包含需要根据查看 GUI 的玩家而实时更新描述的商品（如具有 stock-player 限制），
        // 则缓存 GUI 对象。
        if (cacheGUIMarker) getShop().getShopGUI().setGui(getShop().getShopGUI().buildGUIBuilder(null).build());

        lastRestocking = System.currentTimeMillis();
    }

    @Override
    public void listProduct(Product product) {
        // Event
        ProductListEvent event = new ProductListEvent(getShop(), product);
        if (event.isCancelled()) {
            return;
        }
        // Event

        String productId = product.getId();

        getShop().getShopPricer().cachePrice(productId);
        getShop().cacheProductItem(product);

        // 确保每个捆绑包内容都有价格缓存
        if (product.getType() == ProductType.BUNDLE) {
            for (String contentId : ((BundleProduct) product).getBundleContents().keySet()) {
                Product content = DailyShop.PRODUCT_FACTORY.getProduct(contentId);
                getShop().cacheProductItem(content);
                getShop().getShopPricer().cachePrice(contentId);
            }
        }

        // 若商品上架则补充其库存
        product.getProductStock().restock();
        // 尝试重置商人模式余额
        shop.getShopCashier().restockMerchant();

        listedProducts.add(productId);
    }

    @Override
    public long getLastRestocking() {
        return lastRestocking;
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
    public void setLastRestocking(long lastRestocking) {
        this.lastRestocking = lastRestocking;
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
