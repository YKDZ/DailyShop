package cn.encmys.ykdz.forest.dailyshop.factory;

import cn.encmys.ykdz.forest.dailyshop.DailyShop;
import cn.encmys.ykdz.forest.dailyshop.api.product.Product;
import cn.encmys.ykdz.forest.dailyshop.builder.ProductIconBuilder;
import cn.encmys.ykdz.forest.dailyshop.builder.ProductItemBuilder;
import cn.encmys.ykdz.forest.dailyshop.config.ProductConfig;
import cn.encmys.ykdz.forest.dailyshop.config.RarityConfig;
import cn.encmys.ykdz.forest.dailyshop.price.Price;
import cn.encmys.ykdz.forest.dailyshop.product.BundleProduct;
import cn.encmys.ykdz.forest.dailyshop.product.CommandProduct;
import cn.encmys.ykdz.forest.dailyshop.product.ItemProduct;
import cn.encmys.ykdz.forest.dailyshop.rarity.Rarity;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.YamlConfiguration;

import javax.management.openmbean.InvalidKeyException;
import java.util.HashMap;
import java.util.List;

public class ProductFactory {
    private static final RarityFactory rarityFactory = DailyShop.getRarityFactory();
    private static final HashMap<String, Product> products = new HashMap<>();

    public ProductFactory() {
        for (String configId : ProductConfig.getAllPacksId()) {
            YamlConfiguration config = ProductConfig.getConfig(configId);
            ConfigurationSection products = config.getConfigurationSection("products");
            ConfigurationSection defaultSettings = config.getConfigurationSection("default-settings");

            for (String productId : products.getKeys(false)) {
                ConfigurationSection productSection = products.getConfigurationSection(productId);

                if (!productSection.getStringList("bundle-contents").isEmpty()) {
                    buildBundleProduct(productId, productSection, defaultSettings);
                } else if (productSection.contains("buy-commands") || productSection.contains("sell-commands")) {
                    buildCommandProduct(productId, productSection, defaultSettings);
                } else {
                    buildItemProduct(productId, productSection, defaultSettings);
                }
            }
        }
    }

    public ProductIconBuilder buildProductIconBuilder(ConfigurationSection productSection, ConfigurationSection defaultSettings) {
        String item = productSection.getString("item", "DIRT");
        List<String> descLore = productSection.getStringList("desc-lore");
        int amount = productSection.getInt("amount", defaultSettings.getInt("amount", 1));
        String name = productSection.getString("name");
        Integer customModelData = productSection.getInt("custom-model-data");
        List<String> itemFlags = productSection.getStringList("item-flags");

        return ProductIconBuilder.get(item)
                .setName(name)
                .setDescLore(descLore)
                .setAmount(amount)
                .setCustomModelData(customModelData)
                .setItemFlags(itemFlags);
    }

    public ProductItemBuilder buildProductItemBuilder(ConfigurationSection productSection, ConfigurationSection defaultSettings) {
        String item = productSection.getString("item", "DIRT");
        List<String> lore = productSection.getStringList("lore");
        int amount = productSection.getInt("amount", defaultSettings.getInt("amount", 1));
        String name = productSection.getString("name");
        int customModelData = productSection.getInt("custom-model-data", 0);
        List<String> itemFlags = productSection.getStringList("item-flags");

        return ProductItemBuilder.get(item)
                .setName(name)
                .setLore(lore)
                .setAmount(amount)
                .setCustomModelData(customModelData)
                .setItemFlags(itemFlags);
    }

    public Product buildBundleProduct(String id, ConfigurationSection productSection, ConfigurationSection defaultSettings) {
        if (products.containsKey(id)) {
            throw new InvalidKeyException("Product ID is duplicated: " + id);
        }

        // Price
        Price buyPrice = new Price(
                productSection.getConfigurationSection( "buy-price") != null ? productSection.getConfigurationSection( "buy-price") : defaultSettings.getConfigurationSection("buy-price")
        );
        Price sellPrice = new Price(
                productSection.getConfigurationSection( "sell-price") != null ? productSection.getConfigurationSection( "sell-price") : defaultSettings.getConfigurationSection("sell-price")
        );

        // Icon Builder
        ProductIconBuilder productIconBuilder = buildProductIconBuilder(productSection, defaultSettings);

        // ProductItem Builder
        ProductItemBuilder productItemBuilder = null;

        // Rarity
        Rarity rarity = rarityFactory.getRarity(productSection.getString( "rarity", defaultSettings.getString("rarity", RarityConfig.getAllId().get(0))));

        // Bundle Contents
        List<String> bundleContents = productSection.getStringList("bundle-contents");

        Product product = new BundleProduct(id, buyPrice, sellPrice, rarity, productIconBuilder, productItemBuilder, bundleContents);
        products.put(id, product);
        return product;
    }

    public Product buildCommandProduct(String id, ConfigurationSection productSection, ConfigurationSection defaultSettings) {
        if (products.containsKey(id)) {
            throw new InvalidKeyException("Product ID is duplicated: " + id);
        }

        // Price
        Price buyPrice = new Price(
                productSection.getConfigurationSection( "buy-price") != null ? productSection.getConfigurationSection( "buy-price") : defaultSettings.getConfigurationSection("buy-price")
        );
        Price sellPrice = new Price(
                productSection.getConfigurationSection( "sell-price") != null ? productSection.getConfigurationSection( "sell-price") : defaultSettings.getConfigurationSection("sell-price")
        );

        // Icon Builder
        ProductIconBuilder productIconBuilder = buildProductIconBuilder(productSection, defaultSettings);

        // ProductItem Builder
        ProductItemBuilder productItemBuilder = null;

        // Rarity
        Rarity rarity = rarityFactory.getRarity(productSection.getString( "rarity", defaultSettings.getString("rarity", RarityConfig.getAllId().get(0))));

        // Commands
        List<String> buyCommands = productSection.getStringList("buy-commands");
        List<String> sellCommands = productSection.getStringList("sell-commands");

        Product product = new CommandProduct(id, buyPrice, sellPrice, rarity, productIconBuilder, productItemBuilder, buyCommands, sellCommands);
        products.put(id, product);
        return product;
    }

    public Product buildItemProduct(String id, ConfigurationSection productSection, ConfigurationSection defaultSettings) {
        if (products.containsKey(id)) {
            throw new InvalidKeyException("Product ID is duplicated: " + id);
        }

        // Price
        Price buyPrice = new Price(
                productSection.getConfigurationSection( "buy-price") != null ? productSection.getConfigurationSection( "buy-price") : defaultSettings.getConfigurationSection("buy-price")
        );
        Price sellPrice = new Price(
                productSection.getConfigurationSection( "sell-price") != null ? productSection.getConfigurationSection( "sell-price") : defaultSettings.getConfigurationSection("sell-price")
        );

        // Icon Builder
        ProductIconBuilder productIconBuilder = buildProductIconBuilder(productSection, defaultSettings);

        // ProductItem Builder
        ProductItemBuilder productItemBuilder = buildProductItemBuilder(productSection, defaultSettings);

        // Rarity
        Rarity rarity = rarityFactory.getRarity(productSection.getString( "rarity", defaultSettings.getString("rarity", RarityConfig.getAllId().get(0))));

        // Cache ProductItem
        boolean isCacheable = productSection.getBoolean("cacheable", defaultSettings.getBoolean("cacheable", true));

        Product product = new ItemProduct(id, buyPrice, sellPrice, rarity, productIconBuilder, productItemBuilder, isCacheable);
        products.put(id, product);
        return product;
    }

    public Product getProduct(String id) {
        return products.get(id);
    }

    public boolean containsProduct(String id) {
        return products.containsKey(id);
    }

    public void unload() {
        products.clear();
    }
}