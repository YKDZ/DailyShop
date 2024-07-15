package cn.encmys.ykdz.forest.dailyshop.shop;

import cn.encmys.ykdz.forest.dailyshop.api.DailyShop;
import cn.encmys.ykdz.forest.dailyshop.api.event.ShopRestockEvent;
import cn.encmys.ykdz.forest.dailyshop.api.product.Product;
import cn.encmys.ykdz.forest.dailyshop.api.product.enums.ProductType;
import cn.encmys.ykdz.forest.dailyshop.api.product.factory.ProductFactory;
import cn.encmys.ykdz.forest.dailyshop.api.shop.Shop;
import cn.encmys.ykdz.forest.dailyshop.api.shop.cashier.ShopCashier;
import cn.encmys.ykdz.forest.dailyshop.api.shop.pricer.ShopPricer;
import cn.encmys.ykdz.forest.dailyshop.gui.HistoryGUI;
import cn.encmys.ykdz.forest.dailyshop.gui.ShopGUI;
import cn.encmys.ykdz.forest.dailyshop.product.BundleProduct;
import cn.encmys.ykdz.forest.dailyshop.shop.cashier.ShopCashierImpl;
import cn.encmys.ykdz.forest.dailyshop.shop.pricer.ShopPricerImpl;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.*;
import java.util.stream.Collectors;

public class ShopImpl implements Shop {
    private static final Random random = new Random();
    private final String id;
    private final String name;
    private final int restockTime;
    private final List<String> allProductsId;
    private final int size;
    private final ShopGUI shopGUI;
    private final HistoryGUI historyGUI;
    private final ShopPricer shopPricer;
    private final ShopCashier shopCashier;
    private final List<String> listedProducts = new ArrayList<>();
    private final Map<String, ItemStack> cachedProduct = new HashMap<>();
    private long lastRestocking;

    /**
     * @param id            Shop id
     * @param restockTime   Shop restock time in minutes
     * @param allProductsId ID of all possible products
     * @param size          Maximum number of items in the shop at the same time
     */
    public ShopImpl(String id, String name, int restockTime, List<String> allProductsId, int size) {
        this.id = id;
        this.name = name;
        this.restockTime = restockTime;
        this.allProductsId = allProductsId;
        this.size = size;
        shopGUI = new ShopGUI(this);
        historyGUI = new HistoryGUI(this);
        shopPricer = new ShopPricerImpl(this);
        shopCashier = new ShopCashierImpl(this);
    }

    @Override
    public void restock() {
        List<Product> productsPreparedToBeListed = new ArrayList<>();
        ProductFactory productFactory = DailyShop.PRODUCT_FACTORY;

        listedProducts.clear();
        // Make map of product id and product
        Map<String, Product> allProducts = allProductsId.stream()
                .collect(Collectors.toMap(
                        productId -> productId,
                        productFactory::getProduct
                ));

        if (size >= allProductsId.size()) {
            productsPreparedToBeListed.addAll(allProducts.values());
        } else {
            List<String> temp = new ArrayList<>(allProductsId);
            int totalWeight = allProducts.values().stream()
                    .mapToInt(p -> p.getRarity().getWeight()).sum();

            for (int i = 0; i < size; i++) {
                int needed = random.nextInt(totalWeight) + 1; // Add 1 to avoid 0
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
        ShopRestockEvent event = new ShopRestockEvent(this, productsPreparedToBeListed);
        if (event.isCancelled()) {
            return;
        }
        // Event

        for (Product product : productsPreparedToBeListed) {
            listProduct(product);
        }

        getShopGUI().closeAll();
        getShopGUI().buildGUIBuilder();

        lastRestocking = System.currentTimeMillis();
    }

    @Override
    public void listProduct(Product product) {
        String productId = product.getId();

        shopPricer.cachePrice(productId);
        cacheProductItem(product);

        // Make sure that every bundle contents have its price.
        if (product.getType() == ProductType.BUNDLE) {
            for (String contentId : ((BundleProduct) product).getBundleContents().keySet()) {
                Product content = DailyShop.PRODUCT_FACTORY.getProduct(contentId);
                cacheProductItem(content);
                shopPricer.cachePrice(contentId);
            }
        }

        listedProducts.add(productId);
    }

    @Override
    public long getLastRestocking() {
        return lastRestocking;
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public int getRestockTime() {
        return restockTime;
    }

    @Override
    public String getId() {
        return id;
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

    @Override
    public ShopGUI getShopGUI() {
        return shopGUI;
    }

    @Override
    public Map<String, ItemStack> getCachedProductItems() {
        return cachedProduct;
    }

    @Override
    public boolean hasCachedProductItem(String productId) {
        return getCachedProductItems().containsKey(productId);
    }

    @Override
    public void cacheProductItem(Product product) {
        if (product.isCacheable()) {
            getCachedProductItems().put(product.getId(), product.getProductItemBuilder().buildProductItem(null));
        }
    }

    @Override
    @Nullable
    public ItemStack getCachedProductItem(@NotNull Product product) {
        String id = product.getId();
        if (product.isCacheable() && !hasCachedProductItem(id)) {
            cacheProductItem(product);
        }
        return getCachedProductItems().get(id);
    }

    @Override
    @NotNull
    public ItemStack getCachedProductItemOrCreateOne(@NotNull Product product, @Nullable Player player) {
        return Optional.ofNullable(getCachedProductItem(product))
                .orElse(product.getProductItemBuilder().buildProductItem(player));
    }

    @Override
    public ShopPricer getShopPricer() {
        return shopPricer;
    }

    @Override
    public ShopCashier getShopCashier() {
        return shopCashier;
    }

    @Override
    public HistoryGUI getHistoryGUI() {
        return historyGUI;
    }
}
