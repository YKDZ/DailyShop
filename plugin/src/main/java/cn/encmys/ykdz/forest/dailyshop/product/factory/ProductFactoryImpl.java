package cn.encmys.ykdz.forest.dailyshop.product.factory;

import cn.encmys.ykdz.forest.dailyshop.api.DailyShop;
import cn.encmys.ykdz.forest.dailyshop.api.config.ProductConfig;
import cn.encmys.ykdz.forest.dailyshop.api.config.RarityConfig;
import cn.encmys.ykdz.forest.dailyshop.api.database.schema.ProductData;
import cn.encmys.ykdz.forest.dailyshop.api.item.BaseItem;
import cn.encmys.ykdz.forest.dailyshop.api.item.decorator.BaseItemDecorator;
import cn.encmys.ykdz.forest.dailyshop.api.price.Price;
import cn.encmys.ykdz.forest.dailyshop.api.product.Product;
import cn.encmys.ykdz.forest.dailyshop.api.product.factory.ProductFactory;
import cn.encmys.ykdz.forest.dailyshop.api.product.stock.ProductStock;
import cn.encmys.ykdz.forest.dailyshop.api.rarity.Rarity;
import cn.encmys.ykdz.forest.dailyshop.api.utils.ConfigUtils;
import cn.encmys.ykdz.forest.dailyshop.api.utils.LogUtils;
import cn.encmys.ykdz.forest.dailyshop.item.builder.BaseItemBuilder;
import cn.encmys.ykdz.forest.dailyshop.item.decorator.BaseItemDecoratorImpl;
import cn.encmys.ykdz.forest.dailyshop.price.PriceImpl;
import cn.encmys.ykdz.forest.dailyshop.product.BundleProduct;
import cn.encmys.ykdz.forest.dailyshop.product.CommandProduct;
import cn.encmys.ykdz.forest.dailyshop.product.ItemProduct;
import cn.encmys.ykdz.forest.dailyshop.product.stock.ProductStockImpl;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.YamlConfiguration;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutionException;

public class ProductFactoryImpl implements ProductFactory {
    private static final HashMap<String, Product> allProducts = new HashMap<>();

    public ProductFactoryImpl() {
        load();
    }

    public void load() {
        for (String configId : ProductConfig.getAllPacksId()) {
            YamlConfiguration config = ProductConfig.getConfig(configId);
            ConfigurationSection products = config.getConfigurationSection("products");
            ConfigurationSection defaultSettings = config.getConfigurationSection("default-settings");

            if (products == null) {
                continue;
            }

            List<String> bundleProducts = new ArrayList<>();
            for (String productId : products.getKeys(false)) {
                ConfigurationSection productSection = products.getConfigurationSection(productId);
                if (productSection != null && productSection.contains("bundle-contents")) {
                    bundleProducts.add(productId);
                } else {
                    buildProduct(productId, productSection, defaultSettings);
                }
            }

            // 最后构建捆绑包商品以便进行内容可用性检查
            for (String bundleProductId : bundleProducts) {
                ConfigurationSection productSection = products.getConfigurationSection(bundleProductId);
                buildProduct(bundleProductId, productSection, defaultSettings);
            }
        }
    }

    @Override
    public void buildProduct(String id, ConfigurationSection productSection, ConfigurationSection defaultSettings) {
        if (containsProduct(id)) {
            LogUtils.warn("Product ID is duplicated: " + id + ". Ignore this product.");
            return;
        }

        ConfigurationSection itemSection = productSection.getConfigurationSection("item");
        ConfigurationSection iconSection = productSection.getConfigurationSection("icon");

        // Price (可以指定默认值)
        Price buyPrice = new PriceImpl(
                ConfigUtils.inheritPriceSection(productSection.getConfigurationSection("buy-price"), defaultSettings.getConfigurationSection("buy-price"))
        );
        Price sellPrice = new PriceImpl(
                ConfigUtils.inheritPriceSection(productSection.getConfigurationSection("sell-price"), defaultSettings.getConfigurationSection("sell-price"))
        );

        // Rarity (可以指定默认值)
        Rarity rarity = DailyShop.RARITY_FACTORY.getRarity(productSection.getString("rarity", defaultSettings.getString("rarity", RarityConfig.getAllId().get(0))));

        // Cacheable (可以指定默认值)
        boolean isCacheable = productSection.getBoolean("cacheable", defaultSettings.getBoolean("cacheable", true));

        // Item (只有 ItemProduct 需要此配置)
        BaseItemDecorator itemDecorator = null;
        if (itemSection != null && productSection.contains("item")) {
            BaseItem item = BaseItemBuilder.get(itemSection.getString("base", "DIRT"));

            if (item == null) {
                LogUtils.warn("Product " + id + " has invalid base setting. Please check it.");
                return;
            }

            itemDecorator = new BaseItemDecoratorImpl(item, false)
                    .setName(itemSection.getString("name"))
                    .setLore(itemSection.getStringList("lore"))
                    .setAmount(itemSection.getInt("amount", defaultSettings.getInt("item.amount", 1)))
                    .setItemFlags(itemSection.getStringList("item-flags"))
                    .setCustomModelData((Integer) itemSection.get("custom-model-data"))
                    .setBannerPatterns(itemSection.getStringList("banner-patterns"))
                    .setFireworkEffectData(itemSection.getStringList("firework-effects"));
        }

        // Icon (若不指定则与 Item 相同)
        if (iconSection == null) {
            if (itemSection != null) {
                iconSection = itemSection;
            } else {
                LogUtils.warn("Product " + id + " has invalid config.");
                return;
            }
        }

        // Icon 继承 Item
        if (itemSection != null) {
            // base
            if (!iconSection.contains("base")) {
                if (itemSection.isString("base")) {
                    iconSection.set("base", itemSection.getString("base"));
                } else {
                    LogUtils.warn("Can not find base item for product " + id + ".");
                    return;
                }
            }
            // name
            if (!iconSection.contains("name")) {
                if (itemSection.isString("name")) {
                    iconSection.set("name", itemSection.getString("name"));
                }
            }
            // amount
            if (!iconSection.contains("amount")) {
                if (itemSection.isInt("amount")) {
                    iconSection.set("amount", itemSection.getInt("amount"));
                } else if (defaultSettings.isInt("item.amount")) {
                    iconSection.set("amount", defaultSettings.getInt("item.amount"));
                }
            }
            // custom-model-data
            if (!iconSection.contains("custom-model-data")) {
                if (itemSection.isInt("custom-model-data")) {
                    iconSection.set("custom-model-data", itemSection.getInt("custom-model-data"));
                }
            }
            // banner-patterns
            if (!iconSection.contains("banner-patterns")) {
                if (itemSection.isList("banner-patterns")) {
                    iconSection.set("banner-patterns", itemSection.getStringList("banner-patterns"));
                }
            }
            // banner-patterns
            if (!iconSection.contains("firework-effects")) {
                if (itemSection.isList("firework-effects")) {
                    iconSection.set("firework-effects", itemSection.getStringList("firework-effects"));
                }
            }
        }

        // 库存（可指定默认值）
        ProductStock stock;

        try {
            ProductData data = DailyShop.DATABASE.queryProductData(id).get();

            ConfigurationSection stockSection = productSection.getConfigurationSection("stock");
            ConfigurationSection defaultStockSection = defaultSettings.getConfigurationSection("stock");

            stock = new ProductStockImpl(
                    id,
                    ConfigUtils.getInt(stockSection, defaultStockSection, "global.size", -1),
                    ConfigUtils.getInt(stockSection, defaultStockSection, "player.size", -1),
                    ConfigUtils.getBoolean(stockSection, defaultStockSection, "global.replenish", false),
                    ConfigUtils.getBoolean(stockSection, defaultStockSection, "player.replenish", false),
                    ConfigUtils.getBoolean(stockSection, defaultStockSection, "global.overflow", false),
                    ConfigUtils.getBoolean(stockSection, defaultStockSection, "player.overflow", false),
                    ConfigUtils.getBoolean(stockSection, defaultStockSection, "global.inherit", false),
                    ConfigUtils.getBoolean(stockSection, defaultStockSection, "player.inherit", false)
            );

            // 仅持久化 currentAmount 数据（尊重最新的溢出、补充、尺寸等配置）
            if (data != null) {
                stock.setCurrentGlobalAmount(data.stock().getCurrentGlobalAmount());
                stock.setCurrentPlayerAmount(data.stock().getCurrentPlayerAmount());
            }
        } catch (InterruptedException | ExecutionException e) {
            throw new RuntimeException(e);
        }

        // IconDecorator
        BaseItem icon = BaseItemBuilder.get(iconSection.getString("base", "DIRT"));

        if (icon == null) {
            LogUtils.warn("Product " + id + " has invalid base setting. Please check it.");
            return;
        }

        BaseItemDecorator iconDecorator = new BaseItemDecoratorImpl(icon, true)
                .setAmount(iconSection.getInt("amount", 1))
                .setLore(iconSection.getStringList("lore").isEmpty() ? null : iconSection.getStringList("lore"))
                .setName(iconSection.getString("name"))
                .setItemFlags(iconSection.getStringList("item-flags"))
                .setCustomModelData((Integer) iconSection.get("custom-model-data"))
                .setBannerPatterns(iconSection.getStringList("banner-patterns"))
                .setFireworkEffectData(iconSection.getStringList("firework-effects"));

        // 构建商品 & 储存
        if (productSection.contains("buy-commands") || productSection.contains("sell-commands")) {
            allProducts.put(id,
                    new CommandProduct(id, buyPrice, sellPrice, rarity, iconDecorator, stock,
                            productSection.getStringList("buy-commands"),
                            productSection.getStringList("sell-commands")));
        } else if (productSection.contains("bundle-contents")) {
            Map<String, Integer> bundleContents = new HashMap<>();
            for (String contentData : productSection.getStringList("bundle-contents")) {
                String[] parsedContentData = contentData.split(":");
                if (parsedContentData.length == 1) {
                    bundleContents.put(parsedContentData[0], 1);
                } else if (parsedContentData.length == 2) {
                    bundleContents.put(parsedContentData[0], Integer.parseInt(parsedContentData[1]));
                } else {
                    LogUtils.warn("Product " + id + " has invalid bundle-contents. The invalid line is: " + contentData + ".");
                    continue;
                }
                // 检查捆绑包内容商品是否存在
                // 需确保捆绑包商品在所有商品之后加载
                Product content = allProducts.get(parsedContentData[0]);
                if (content == null) {
                    LogUtils.warn("Bundle product " + id + " has invalid content " + contentData + ". Please check and fix it in your product config.");
                    bundleContents.remove(parsedContentData[0]);
                }
            }
            allProducts.put(id,
                    new BundleProduct(id, buyPrice, sellPrice, rarity, iconDecorator, stock, bundleContents));
        } else {
            allProducts.put(id,
                    new ItemProduct(id, buyPrice, sellPrice, rarity, iconDecorator, itemDecorator, stock, isCacheable));
        }
    }

    @Override
    public HashMap<String, Product> getProducts() {
        return allProducts;
    }

    @Override
    public Product getProduct(String id) {
        return allProducts.get(id);
    }

    @Override
    public boolean containsProduct(String id) {
        return allProducts.containsKey(id);
    }

    @Override
    public void unload() {
        save();
        allProducts.clear();
    }

    @Override
    public void save() {
        List<Product> data = new ArrayList<>();
        for (Product product : getProducts().values()) {
            // 仅需要储存有库存设置的商品
            if (product.getProductStock().isGlobalStock() || product.getProductStock().isPlayerStock()) {
                data.add(product);
            }
        }
        DailyShop.DATABASE.saveProductData(data);
    }
}