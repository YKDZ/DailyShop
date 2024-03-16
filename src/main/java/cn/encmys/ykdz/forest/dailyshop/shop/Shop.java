package cn.encmys.ykdz.forest.dailyshop.shop;

import cn.encmys.ykdz.forest.dailyshop.DailyShop;
import cn.encmys.ykdz.forest.dailyshop.api.product.Product;
import cn.encmys.ykdz.forest.dailyshop.config.ShopConfig;
import cn.encmys.ykdz.forest.dailyshop.enums.ProductType;
import cn.encmys.ykdz.forest.dailyshop.factory.ProductFactory;
import cn.encmys.ykdz.forest.dailyshop.price.PricePair;
import cn.encmys.ykdz.forest.dailyshop.product.BundleProduct;
import cn.encmys.ykdz.forest.dailyshop.util.GUIIconUtils;
import com.google.gson.annotations.Expose;
import me.clip.placeholderapi.PlaceholderAPI;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import xyz.xenondevs.invui.gui.Gui;
import xyz.xenondevs.invui.gui.ScrollGui;
import xyz.xenondevs.invui.gui.structure.Markers;
import xyz.xenondevs.invui.item.Item;
import xyz.xenondevs.invui.window.Window;

import java.util.*;

public class Shop {
    /**
     * Product slot marker icon in the layout
     */
    private static final char productIdentifier = 'x';
    private static final ProductFactory productFactory = DailyShop.getProductFactory();
    private static final List<Window> windows = new ArrayList<>();
    private final String id;
    private final String name;
    private final int restockTime;
    private final List<String> allProductsId;
    private final ConfigurationSection guiSection;
    private final int size;
    private Gui gui;
    @Expose
    private List<String> listedProducts = new ArrayList<>();
    @Expose
    private long lastRestocking;
    @Expose
    private Map<String, PricePair> cachedPrice = new HashMap<>();

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
        this.guiSection = guiSection;
    }

    public static List<Window> getWindows() {
        return windows;
    }

    public void buildGUI() {
        ScrollGui.Builder<@NotNull Item> builder = ScrollGui.items()
                .setStructure(guiSection.getStringList("layout").toArray(new String[0]))
                .addIngredient(productIdentifier, Markers.CONTENT_LIST_SLOT_HORIZONTAL);

        // Normal Icon
        for (String iconKey : ShopConfig.getGUIIcons(id)) {
            char key = iconKey.charAt(0);
            if (key == productIdentifier) {
                continue;
            }
            builder.addIngredient(key, GUIIconUtils.getGUIIcon(ShopConfig.getGUIIconSection(id, key)));
        }

        // Product Icon
        for (String productId : listedProducts) {
            Product product = productFactory.getProduct(productId);
            builder.addContent(product.getIconBuilder().build(id, product));
        }

        gui = builder.build();
    }

    public void openGUI(Player player) {
        if (gui == null) {
            buildGUI();
        }

        Window window = Window.single()
                        .setGui(gui)
                        .setViewer(player)
                        .setTitle(PlaceholderAPI.setPlaceholders(player, ShopConfig.getGUITitle(id)))
                        .build();

        window.setCloseHandlers(new ArrayList<>() {{
            add(() -> getWindows().remove(window));
        }});

        getWindows().add(window);
        window.open();
    }

    public void restock() {
        Random random = new Random();
        ProductFactory productFactory = DailyShop.getProductFactory();

        listedProducts.clear();

        if (size >= allProductsId.size()) {
            for (String productId : allProductsId) {
                Product product = productFactory.getProduct(productId);
                cachePrice(productId, product.getNewPricePair(getId()));
                listedProducts.add(productId);
            }
        } else {
            List<String> temp = new ArrayList<>(allProductsId);
            List<Integer> intervals = new ArrayList<>();
            int totalWeight = 0;

            // Calculate cumulative weights and total weight
            for (String productId : temp) {
                Product product = productFactory.getProduct(productId);
                totalWeight += product.getRarity().getWeight();
                intervals.add(totalWeight);
            }

            for (int i = 0; i < size; i++) {
                int needed = random.nextInt(totalWeight) + 1; // Add 1 to avoid 0
                int index = Collections.binarySearch(intervals, needed);
                if (index < 0) {
                    index = -index - 1;
                }

                // Handle bundle contents
                String productId = temp.get(index);
                Product product = productFactory.getProduct(productId);
                if (product.getType() == ProductType.BUNDLE) {
                    System.out.println("Bundle: " + productId);
                    for (String contentId : ((BundleProduct) product).getBundleContents()) {
                        Product content = productFactory.getProduct(contentId);
                        // Cache Item
                        if (content.isCacheable()) {
                            content.cacheProductItem(id, null);
                        }
                        cachePrice(contentId, content.getNewPricePair(getId()));
                    }
                }

                cachePrice(productId, product.getNewPricePair(getId()));
                // Cache Item
                if (product.isCacheable()) {
                    product.cacheProductItem(id, null);
                }
                listedProducts.add(productId);

                // Update total weight and intervals
                totalWeight -= product.getRarity().getWeight();
                intervals.remove(index);
                temp.remove(index);
            }
        }

        // Close all windows
        for (Window window : getWindows()) {
            window.close();
        }

        buildGUI();
        lastRestocking = System.currentTimeMillis();
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
}
