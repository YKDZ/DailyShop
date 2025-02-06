package cn.encmys.ykdz.forest.dailyshop.product.factory;

import cn.encmys.ykdz.forest.dailyshop.api.DailyShop;
import cn.encmys.ykdz.forest.dailyshop.api.config.ProductConfig;
import cn.encmys.ykdz.forest.dailyshop.api.config.RarityConfig;
import cn.encmys.ykdz.forest.dailyshop.api.database.dao.ProductStockDao;
import cn.encmys.ykdz.forest.dailyshop.api.database.schema.ProductStockSchema;
import cn.encmys.ykdz.forest.dailyshop.api.item.BaseItem;
import cn.encmys.ykdz.forest.dailyshop.api.item.decorator.BaseItemDecorator;
import cn.encmys.ykdz.forest.dailyshop.api.item.decorator.enums.PropertyType;
import cn.encmys.ykdz.forest.dailyshop.api.price.Price;
import cn.encmys.ykdz.forest.dailyshop.api.product.Product;
import cn.encmys.ykdz.forest.dailyshop.api.product.factory.ProductFactory;
import cn.encmys.ykdz.forest.dailyshop.api.product.stock.ProductStock;
import cn.encmys.ykdz.forest.dailyshop.api.rarity.Rarity;
import cn.encmys.ykdz.forest.dailyshop.api.utils.ConfigUtils;
import cn.encmys.ykdz.forest.dailyshop.api.utils.LogUtils;
import cn.encmys.ykdz.forest.dailyshop.item.builder.BaseItemBuilder;
import cn.encmys.ykdz.forest.dailyshop.price.PriceImpl;
import cn.encmys.ykdz.forest.dailyshop.product.BundleProduct;
import cn.encmys.ykdz.forest.dailyshop.product.CommandProduct;
import cn.encmys.ykdz.forest.dailyshop.product.ItemProduct;
import cn.encmys.ykdz.forest.dailyshop.product.stock.ProductStockImpl;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.YamlConfiguration;

import java.util.*;

public class ProductFactoryImpl implements ProductFactory {
    private static final Map<String, Product> products = new HashMap<>();

    public ProductFactoryImpl() {
        load();
    }

    public void load() {
        for (String configId : ProductConfig.getAllPacksId()) {
            YamlConfiguration config = ProductConfig.getConfig(configId);

            if (config == null) {
                continue;
            }

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
        Rarity rarity = DailyShop.RARITY_FACTORY.getRarity(productSection.getString("rarity", defaultSettings.getString("rarity", RarityConfig.getAllId().getFirst())));

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

            // 首先尊重 baseItem 可能自带的名称（一般特指原版物品的自动本地化物品名机制）
            // 其次再应用插件内指定的名称
            itemDecorator = new BaseItemDecorator(item)
                    .setProperty(PropertyType.NAME, itemSection.getString("name"))
                    .setProperty(PropertyType.LORE, itemSection.getStringList("lore"))
                    .setProperty(PropertyType.AMOUNT, itemSection.getString("amount", defaultSettings.getString("item.amount", "1")))
                    .setProperty(PropertyType.ITEM_FLAGS, itemSection.getStringList("item-flags"))
                    .setProperty(PropertyType.CUSTOM_MODEL_DATA, itemSection.get("custom-model-data"))
                    .setProperty(PropertyType.BANNER_PATTERNS, itemSection.getStringList("banner-patterns"))
                    .setProperty(PropertyType.FIREWORK_EFFECTS, itemSection.getStringList("firework-effects"))
                    .setProperty(PropertyType.ENCHANTMENTS, itemSection.getStringList("enchantments"));
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
            if (!iconSection.contains("base")) {
                if (itemSection.isString("base")) {
                    iconSection.set("base", itemSection.getString("base"));
                } else {
                    LogUtils.warn("Can not find base item for product " + id + ".");
                    return;
                }
            }
            if (!iconSection.contains("name")) {
                if (itemSection.isString("name")) {
                    iconSection.set("name", itemSection.getString("name"));
                }
            }
            if (!iconSection.contains("amount")) {
                if (itemSection.isInt("amount")) {
                    iconSection.set("amount", itemSection.getInt("amount"));
                } else if (defaultSettings.isInt("item.amount")) {
                    iconSection.set("amount", defaultSettings.getInt("item.amount"));
                }
            }
            if (!iconSection.contains("custom-model-data")) {
                if (itemSection.isInt("custom-model-data")) {
                    iconSection.set("custom-model-data", itemSection.getInt("custom-model-data"));
                }
            }
            if (!iconSection.contains("banner-patterns")) {
                if (itemSection.isList("banner-patterns")) {
                    iconSection.set("banner-patterns", itemSection.getStringList("banner-patterns"));
                }
            }
            if (!iconSection.contains("firework-effects")) {
                if (itemSection.isList("firework-effects")) {
                    iconSection.set("firework-effects", itemSection.getStringList("firework-effects"));
                }
            }
            if (!iconSection.contains("enchantments")) {
                if (itemSection.isList("enchantments")) {
                    iconSection.set("enchantments", itemSection.getStringList("enchantments"));
                }
            }
            if (!iconSection.contains("item-flags")) {
                if (itemSection.isList("item-flags")) {
                    iconSection.set("item-flags", itemSection.getStringList("item-flags"));
                }
            }
        }

        // 库存（可指定默认值）
        ProductStock stock;

        ProductStockSchema stockSchema = DailyShop.DATABASE_FACTORY.getProductStockDao().querySchema(id);

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

        // 仅持久化 currentAmount 数据（尊重最新的 overflow, supply, size 等配置）
        if (stockSchema != null) {
            stock.setCurrentGlobalAmount(stockSchema.currentGlobalAmount());
            stock.setCurrentPlayerAmount(stockSchema.currentPlayerAmount());
        }

        // IconDecorator
        BaseItem icon = BaseItemBuilder.get(iconSection.getString("base", "DIRT"));

        if (icon == null) {
            LogUtils.warn("Product " + id + " has invalid base setting. Please check it.");
            return;
        }

        // ListConditions
        List<String> listConditions = productSection.getStringList("list-conditions");
        if (listConditions.isEmpty()) {
            listConditions = defaultSettings.getStringList("list-conditions");
        }

        BaseItemDecorator iconDecorator = new BaseItemDecorator(icon)
                .setProperty(PropertyType.AMOUNT, iconSection.getString("amount", "1"))
                .setProperty(PropertyType.LORE, iconSection.getStringList("lore").isEmpty() ? null : iconSection.getStringList("lore"))
                .setProperty(PropertyType.NAME, iconSection.getString("name"))
                .setProperty(PropertyType.ITEM_FLAGS, iconSection.getStringList("item-flags"))
                .setProperty(PropertyType.CUSTOM_MODEL_DATA, iconSection.getInt("custom-model-data"))
                .setProperty(PropertyType.BANNER_PATTERNS, iconSection.getStringList("banner-patterns"))
                .setProperty(PropertyType.FIREWORK_EFFECTS, iconSection.getStringList("firework-effects"))
                .setProperty(PropertyType.ENCHANTMENTS, iconSection.getStringList("enchantments"));

        // 构建商品 & 储存
        if (productSection.contains("buy-commands") || productSection.contains("sell-commands")) {
            products.put(id,
                    new CommandProduct(id, buyPrice, sellPrice, rarity, iconDecorator, stock, listConditions,
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
                Product content = products.get(parsedContentData[0]);
                if (content == null) {
                    LogUtils.warn("Bundle product " + id + " has invalid content " + contentData + ". Please check and fix it in your product config.");
                    bundleContents.remove(parsedContentData[0]);
                }
            }
            products.put(id,
                    new BundleProduct(id, buyPrice, sellPrice, rarity, iconDecorator, stock, listConditions, bundleContents));
        } else {
            products.put(id,
                    new ItemProduct(id, buyPrice, sellPrice, rarity, iconDecorator, itemDecorator, stock, listConditions, isCacheable));
        }
    }

    @Override
    public Map<String, Product> getProducts() {
        return Collections.unmodifiableMap(products);
    }

    @Override
    public Product getProduct(String id) {
        return products.get(id);
    }

    @Override
    public boolean containsProduct(String id) {
        return products.containsKey(id);
    }

    @Override
    public void unload() {
        save();
        products.clear();
    }

    @Override
    public void save() {
        for (Product product : getProducts().values()) {
            // 仅需要储存有库存设置的商品
            if (product.getProductStock().isGlobalStock() || product.getProductStock().isPlayerStock()) {
                ProductStockDao dao = DailyShop.DATABASE_FACTORY.getProductStockDao();
                dao.insertSchema(ProductStockSchema.of(product.getProductStock()));
            }
        }
    }
}