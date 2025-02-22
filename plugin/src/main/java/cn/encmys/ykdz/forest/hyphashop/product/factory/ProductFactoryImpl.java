package cn.encmys.ykdz.forest.hyphashop.product.factory;

import cn.encmys.ykdz.forest.hyphascript.context.Context;
import cn.encmys.ykdz.forest.hyphashop.api.HyphaShop;
import cn.encmys.ykdz.forest.hyphashop.api.database.dao.ProductStockDao;
import cn.encmys.ykdz.forest.hyphashop.api.database.schema.ProductStockSchema;
import cn.encmys.ykdz.forest.hyphashop.api.item.BaseItem;
import cn.encmys.ykdz.forest.hyphashop.api.item.decorator.BaseItemDecorator;
import cn.encmys.ykdz.forest.hyphashop.api.price.Price;
import cn.encmys.ykdz.forest.hyphashop.api.product.Product;
import cn.encmys.ykdz.forest.hyphashop.api.product.factory.ProductFactory;
import cn.encmys.ykdz.forest.hyphashop.api.product.stock.ProductStock;
import cn.encmys.ykdz.forest.hyphashop.api.rarity.Rarity;
import cn.encmys.ykdz.forest.hyphashop.config.ProductConfig;
import cn.encmys.ykdz.forest.hyphashop.config.RarityConfig;
import cn.encmys.ykdz.forest.hyphashop.item.builder.BaseItemBuilder;
import cn.encmys.ykdz.forest.hyphashop.price.PriceImpl;
import cn.encmys.ykdz.forest.hyphashop.product.BundleProduct;
import cn.encmys.ykdz.forest.hyphashop.product.CommandProduct;
import cn.encmys.ykdz.forest.hyphashop.product.ItemProduct;
import cn.encmys.ykdz.forest.hyphashop.product.stock.ProductStockImpl;
import cn.encmys.ykdz.forest.hyphashop.utils.ConfigUtils;
import cn.encmys.ykdz.forest.hyphashop.utils.LogUtils;
import cn.encmys.ykdz.forest.hyphashop.utils.ScriptUtils;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.YamlConfiguration;
import org.jetbrains.annotations.NotNull;

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

            // 防止 null
            if (defaultSettings == null) defaultSettings = new YamlConfiguration();

            if (products == null) {
                continue;
            }

            List<String> bundleProducts = new ArrayList<>();
            for (String productId : products.getKeys(false)) {
                ConfigurationSection productSection = products.getConfigurationSection(productId);
                // 最后再构建捆绑包
                if (productSection != null && productSection.contains("bundle-contents")) {
                    bundleProducts.add(productId);
                } else if (productSection != null) {
                    buildProduct(productId, productSection, defaultSettings);
                } else {
                    throw new IllegalArgumentException("Product " + productId + " has no config section.");
                }
            }

            // 最后构建捆绑包商品以便进行内容可用性检查
            for (String bundleProductId : bundleProducts) {
                ConfigurationSection productSection = products.getConfigurationSection(bundleProductId);
                if (productSection == null) throw new IllegalArgumentException("Bundle product " + bundleProductId + " has no config section.");
                buildProduct(bundleProductId, productSection, defaultSettings);
            }
        }
    }

    @Override
    public void buildProduct(@NotNull String id, @NotNull ConfigurationSection productSection, @NotNull ConfigurationSection defaultSettings) {
        if (containsProduct(id)) {
            LogUtils.warn("Product ID is duplicated: " + id + ". Ignore this product.");
            return;
        }

        ConfigUtils.inheritConfigSection(productSection, defaultSettings);

        LogUtils.warn(productSection.getKeys(true).toString());

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
        Rarity rarity = HyphaShop.RARITY_FACTORY.getRarity(productSection.getString("rarity", RarityConfig.getAllId().getFirst()));

        // Cacheable (可以指定默认值)
        boolean isCacheable = productSection.getBoolean("cacheable", true);

        // Item (只有 ItemProduct 需要此配置)
        BaseItemDecorator itemDecorator = null;
        if (itemSection != null && productSection.contains("item")) {
            BaseItem base = BaseItemBuilder.get(itemSection.getString("base", "dirt"));

            if (base == null) {
                LogUtils.warn("Product " + id + " has invalid base setting. Please check it.");
                return;
            }

            itemDecorator = ConfigUtils.parseDecorator(itemSection);
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
            ConfigUtils.inheritConfigSection(iconSection, itemSection);
        }

        // 库存（可指定默认值）
        ProductStock stock = new ProductStockImpl(id, 0, 0, false, false, false, false, false, false);
        ConfigurationSection stockSection = productSection.getConfigurationSection("stock");

        if (stockSection != null) {
            stock = new ProductStockImpl(
                    id,
                    stockSection.getInt("global.size", -1),
                    stockSection.getInt("player.size", -1),
                    stockSection.getBoolean("global.replenish", false),
                    stockSection.getBoolean("player.replenish", false),
                    stockSection.getBoolean("global.overflow", false),
                    stockSection.getBoolean("player.overflow", false),
                    stockSection.getBoolean("global.inherit", false),
                    stockSection.getBoolean("player.inherit", false)
            );
        }

        if (stockSection != null) {
            ProductStockSchema stockSchema = HyphaShop.DATABASE_FACTORY.getProductStockDao().querySchema(id);

            // 仅持久化 currentAmount 数据（尊重最新的 overflow, supply, size 等配置）
            if (stockSchema != null) {
                stock.setCurrentGlobalAmount(stockSchema.currentGlobalAmount());
                stock.setCurrentPlayerAmount(stockSchema.currentPlayerAmount());
            }
        }

        // IconDecorator
        BaseItem base = BaseItemBuilder.get(iconSection.getString("base", "dirt"));

        if (base == null) {
            LogUtils.warn("Product " + id + " has invalid base setting. Please check it.");
            return;
        }

        // ListConditions
        List<String> listConditions = productSection.getStringList("list-conditions");

        BaseItemDecorator iconDecorator = ConfigUtils.parseDecorator(iconSection);

        if (iconDecorator == null) {
            LogUtils.warn("Product " + id + " has invalid icon setting. Please check it.");
            return;
        }

        // 脚本用上下文
        Context ctx = ScriptUtils.extractContext(productSection.getString("context", ""));

        // 构建商品 & 储存
        if (productSection.contains("buy-commands") || productSection.contains("sell-commands")) {
            // 命令
            products.put(id,
                    new CommandProduct(id, buyPrice, sellPrice, rarity, iconDecorator, stock, listConditions, ctx,
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
            // 捆绑包
            products.put(id,
                    new BundleProduct(id, buyPrice, sellPrice, rarity, iconDecorator, stock, listConditions, ctx, bundleContents));
        } else if (itemDecorator != null) {
            // 纯物品
            products.put(id,
                    new ItemProduct(id, buyPrice, sellPrice, rarity, iconDecorator, itemDecorator, stock, listConditions, ctx, isCacheable));
        } else {
            throw new IllegalArgumentException("Product is neither item, bundle or command.");
        }
    }

    @Override
    public @NotNull Map<String, Product> getProducts() {
        return Collections.unmodifiableMap(products);
    }

    @Override
    public Product getProduct(@NotNull String id) {
        return products.get(id);
    }

    @Override
    public boolean containsProduct(@NotNull String id) {
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
                ProductStockDao dao = HyphaShop.DATABASE_FACTORY.getProductStockDao();
                dao.insertSchema(ProductStockSchema.of(product.getProductStock()));
            }
        }
    }
}