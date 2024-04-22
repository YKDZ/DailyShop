package cn.encmys.ykdz.forest.dailyshop.shop;

import cn.encmys.ykdz.forest.dailyshop.DailyShop;
import cn.encmys.ykdz.forest.dailyshop.api.product.Product;
import cn.encmys.ykdz.forest.dailyshop.product.BundleProduct;
import cn.encmys.ykdz.forest.dailyshop.product.enums.ProductType;
import cn.encmys.ykdz.forest.dailyshop.product.factory.ProductFactory;
import cn.encmys.ykdz.forest.dailyshop.shop.gui.ShopGUI;
import cn.encmys.ykdz.forest.dailyshop.shop.logger.ShopLogger;
import cn.encmys.ykdz.forest.dailyshop.shop.pricer.ShopPricer;
import com.google.gson.annotations.Expose;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.*;
import java.util.stream.Collectors;

public class Shop {
    private static final Random random = new Random();
    private static final ProductFactory productFactory = DailyShop.getProductFactory();
    private final String id;
    private final String name;
    private final int restockTime;
    private final List<String> allProductsId;
    private final int size;
    private final ShopGUI shopGUI;
    @Expose
    private final ShopPricer shopPricer;
    @Expose
    private final ShopLogger shopLogger;
    @Expose
    private List<String> listedProducts = new ArrayList<>();
    private Map<String, ItemStack> cachedProduct = new HashMap<>();
    @Expose
    private long lastRestocking;

    /**
     * @param id            Shop id
     * @param restockTime   Shop restock time in minutes
     * @param allProductsId ID of all possible products
     * @param size          Maximum number of items in the shop at the same time
     * @param guiSection    Shop gui configuration section
     */
    public Shop(String id, String name, int restockTime, List<String> allProductsId, int size, ConfigurationSection guiSection) {
        this.id = id;
        this.name = name;
        this.restockTime = restockTime;
        this.allProductsId = allProductsId;
        this.size = size;
        shopGUI = new ShopGUI(getId(), guiSection);
        shopPricer = new ShopPricer(this);
        shopLogger = new ShopLogger(this);
    }

    public void open(Player player) {
        shopGUI.open(player);
    }

    public void restock() {
        ProductFactory productFactory = DailyShop.getProductFactory();

        listedProducts.clear();
        // Make map of product id and product
        Map<String, Product> allProducts = allProductsId.stream()
                .collect(Collectors.toMap(
                        productId -> productId,
                        productFactory::getProduct
                ));

        if (size >= allProductsId.size()) {
            for (String productId : allProductsId) {
                Product product = allProducts.get(productId);
                listProduct(product);
            }
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
                        listProduct(product);
                        totalWeight -= product.getRarity().getWeight();
                        temp.remove(productId);
                        break;
                    }
                }
            }
        }

        getShopGUI().closeAll();
        getShopGUI().build(getListedProducts());

        lastRestocking = System.currentTimeMillis();
    }

    private void listProduct(Product product) {
        String productId = product.getId();

        shopPricer.cachePrice(productId);
        cacheProductItem(product);

        // Make sure that every bundle contents have its price.
        if (product.getType() == ProductType.BUNDLE) {
            for (String contentId : ((BundleProduct) product).getBundleContents().keySet()) {
                Product content = productFactory.getProduct(contentId);
                cacheProductItem(content);
                shopPricer.cachePrice(contentId);
            }
        }

        listedProducts.add(productId);
    }

    public long getLastRestocking() {
        return lastRestocking;
    }

    public String getName() {
        return name;
    }

    public int getRestockTime() {
        return restockTime;
    }

    public String getId() {
        return id;
    }

    public List<String> getListedProducts() {
        return listedProducts;
    }

    public List<String> getAllProductsId() {
        return allProductsId;
    }

    public boolean isListedProduct(String id) {
        return listedProducts.contains(id);
    }

    public void setLastRestocking(long lastRestocking) {
        this.lastRestocking = lastRestocking;
    }

    public void addListedProducts(List<String> listedProducts) {
        this.listedProducts.addAll(listedProducts);
    }

    public ShopGUI getShopGUI() {
        return shopGUI;
    }

    public Map<String, ItemStack> getCachedProductItems() {
        return cachedProduct;
    }

    public boolean hasCachedProductItem(String productId) {
        return getCachedProductItems().containsKey(productId);
    }

    public void cacheProductItem(Product product) {
        if (product.isCacheable()) {
            getCachedProductItems().put(product.getId(), product.getProductItemBuilder().buildProductItem(null));
        }
    }

    @Nullable
    public ItemStack getCachedProductItem(@NotNull Product product) {
        String id = product.getId();
        if (product.isCacheable() && !hasCachedProductItem(id)) {
            cacheProductItem(product);
        }
        return getCachedProductItems().get(id);
    }

    @NotNull
    public ItemStack getCachedProductItemOrCreateOne(@NotNull Product product, @Nullable Player player) {
        return Optional.ofNullable(getCachedProductItem(product))
                .orElse(product.getProductItemBuilder().buildProductItem(player));
    }

    public ShopPricer getShopPricer() {
        return shopPricer;
    }
}
