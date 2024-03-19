package cn.encmys.ykdz.forest.dailyshop.shop;

import cn.encmys.ykdz.forest.dailyshop.DailyShop;
import cn.encmys.ykdz.forest.dailyshop.api.product.Product;
import cn.encmys.ykdz.forest.dailyshop.factory.ProductFactory;
import cn.encmys.ykdz.forest.dailyshop.gui.ShopGUI;
import cn.encmys.ykdz.forest.dailyshop.price.PricePair;
import cn.encmys.ykdz.forest.dailyshop.product.BundleProduct;
import cn.encmys.ykdz.forest.dailyshop.product.enums.ProductType;
import com.google.gson.annotations.Expose;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;

import java.util.*;

public class Shop {
    /**
     * Product slot marker icon in the layout
     */
    private static final ProductFactory productFactory = DailyShop.getProductFactory();
    private final String id;
    private final String name;
    private final int restockTime;
    private final List<String> allProductsId;
    private final int size;
    private ShopGUI shopGUI;
    @Expose
    private List<String> listedProducts = new ArrayList<>();
    @Expose
    private Map<String, PricePair> cachedPrice = new HashMap<>();
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
        this.shopGUI = new ShopGUI(getId(), guiSection);
    }

    public void open(Player player) {
        shopGUI.open(player);
    }

    public void restock() {
        Random random = new Random();
        ProductFactory productFactory = DailyShop.getProductFactory();

        listedProducts.clear();
        Map<String, Product> allProducts = new HashMap<>();
        for (String productId : allProductsId) {
            allProducts.put(productId, productFactory.getProduct(productId));
        }

        if (size >= allProductsId.size()) {
            for (String productId : allProductsId) {
                Product product = allProducts.get(productId);
                cachePrice(productId, product.getNewPricePair(getId()));
                listedProducts.add(productId);
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
                        handleProduct(product);
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

    private void handleProduct(Product product) {
        String productId = product.getId();

        cachePrice(productId, product.getNewPricePair(getId()));

        if (product.isCacheable()) {
            product.cacheProductItem(getId(), null);
        }

        // Make sure that every bundle contents have its price.
        if (product.getType() == ProductType.BUNDLE) {
            for (String contentId : ((BundleProduct) product).getBundleContents()) {
                Product content = productFactory.getProduct(contentId);
                if (content.isCacheable()) {
                    content.cacheProductItem(getId(), null);
                }
                cachePrice(contentId, content.getNewPricePair(getId()));
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

    public void setListedProducts(List<String> listedProducts) {
        this.listedProducts = listedProducts;
    }

    public Map<String, PricePair> getCachedPrice() {
        return cachedPrice;
    }

    public void setCachedPrice(Map<String, PricePair> cachedPrice) {
        this.cachedPrice = cachedPrice;
    }

    public void cachePrice(String id, PricePair pricePair) {
        getCachedPrice().put(id, pricePair);
    }

    public double getBuyPrice(String productId) {
        return getCachedPrice().get(productId).getBuy();
    }

    public double getSellPrice(String productId) {
        return getCachedPrice().get(productId).getSell();
    }

    public ShopGUI getShopGUI() {
        return shopGUI;
    }
}
