package cn.encmys.ykdz.forest.dailyshop.shop;

import cn.encmys.ykdz.forest.dailyshop.DailyShop;
import cn.encmys.ykdz.forest.dailyshop.api.product.Product;
import cn.encmys.ykdz.forest.dailyshop.config.ShopConfig;
import cn.encmys.ykdz.forest.dailyshop.enums.ProductType;
import cn.encmys.ykdz.forest.dailyshop.factory.ProductFactory;
import cn.encmys.ykdz.forest.dailyshop.util.GUIIconUtils;
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
    private final String id;
    private final String name;
    private final int restockTime;
    private final List<String> allProductsId;
    private final ConfigurationSection guiSection;
    private final int size;
    private final List<Product> listedProducts = new ArrayList<>();
    private Gui gui;
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
        this.guiSection = guiSection;
        loadData();
        buildGUI(guiSection);
    }

    public void buildGUI(@NotNull ConfigurationSection guiSection) {
        ScrollGui.Builder<@NotNull Item> builder = ScrollGui.items()
                .setStructure(guiSection.getStringList("layout").toArray(new String[0]))
                .addIngredient(productIdentifier, Markers.CONTENT_LIST_SLOT_HORIZONTAL);

        for (String iconKey : ShopConfig.getGUIIcons(id)) {
            char key = iconKey.charAt(0);
            if (key == productIdentifier) {
                continue;
            }
            builder.addIngredient(key, GUIIconUtils.getGUIIcon(ShopConfig.getGUIIconSection(id, key)));
        }

        for (Product product : listedProducts) {
            builder.addContent(product.getGUIItem(id));
        }

        gui = builder.build();
    }

    public void openGUI(Player player) {
        Window.single()
                .setGui(gui)
                .setViewer(player)
                .setTitle(PlaceholderAPI.setPlaceholders(player, ShopConfig.getGUITitle(id)))
                .build()
                .open();
    }

    public void restock() {
        Random random = new Random();
        ProductFactory productFactory = DailyShop.getProductFactory();

        listedProducts.clear();

        if (size >= allProductsId.size()) {
            for (String productId : allProductsId) {
                Product product = productFactory.getProduct(productId);
                product.updatePrice(id);
                listedProducts.add(product);
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
                    for (String contentId : product.getBundleContents()) {
                        Product content = productFactory.getProduct(contentId);
                        content.updatePrice(id);
                    }
                }

                product.updatePrice(id);
                listedProducts.add(product);

                // Update total weight and intervals
                totalWeight -= product.getRarity().getWeight();
                intervals.remove(index);
                temp.remove(index);
            }
        }

        buildGUI(guiSection);
        lastRestocking = System.currentTimeMillis();
    }

    public int getTotalWeight() {
        int sumWeight = 0;
        for (String productId : allProductsId) {
            Product product = DailyShop.getProductFactory().getProduct(productId);
            sumWeight += product.getRarity().getWeight();
        }
        return sumWeight;
    }

    public void loadData() {
        List<String> listedProductsId = DailyShop.getDatabase().loadShopData().get(id);
        if (listedProductsId == null) {
            restock();
        } else {
            for (String productId : listedProductsId) {
                listedProducts.add(DailyShop.getProductFactory().getProduct(productId));
            }
        }
    }

    public void saveData() {
        Map<String, List<String>> dataMap = new HashMap<>();
        List<String> listedProductsId = new ArrayList<>();
        for (Product product : listedProducts) {
            listedProductsId.add(product.getId());
        }
        dataMap.put(id, listedProductsId);
        DailyShop.getDatabase().saveShopData(dataMap);
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

    public List<Product> getListedProducts() {
        return listedProducts;
    }

    public List<String> getAllProductsId() {
        return allProductsId;
    }
}
