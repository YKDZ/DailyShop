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
import cn.encmys.ykdz.forest.dailyshop.product.VanillaProduct;
import cn.encmys.ykdz.forest.dailyshop.rarity.Rarity;
import org.bukkit.Material;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.YamlConfiguration;

import javax.management.openmbean.InvalidKeyException;
import java.util.ArrayList;
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

            List<String> bundlesId = new ArrayList<>();
            for (String productId : products.getKeys(false)) {
                ConfigurationSection productSection = products.getConfigurationSection(productId);

                // Record Bundle
                if (!productSection.getStringList("bundle-contents").isEmpty()) {
                    bundlesId.add(productId);
                    continue;
                }

                if (productSection.contains("commands")) {
                    buildCommandProduct(productId, productSection, defaultSettings);
                } else {
                    buildVanillaProduct(productId, productSection, defaultSettings);
                }
            }

            // Handle Bundle
            for (String bundleId : bundlesId) {
                ConfigurationSection productSection = products.getConfigurationSection(bundleId);
                buildBundleProduct(bundleId, productSection, defaultSettings);
            }
        }
    }

    public Product buildVanillaProduct(String id, ConfigurationSection productSection, ConfigurationSection defaultSettings) {
        if (products.containsKey(id)) {
            throw new InvalidKeyException("Product ID is duplicated: " + id);
        }

        // Item
        String item = productSection.getString("item", "DIRT");
        Material material = Material.valueOf(item);

        // Price
        Price buyPrice = new Price(
                productSection.getConfigurationSection( "buy-price") != null ? productSection.getConfigurationSection( "buy-price") : defaultSettings.getConfigurationSection("buy-price")
        );
        Price sellPrice = new Price(
                productSection.getConfigurationSection( "sell-price") != null ? productSection.getConfigurationSection( "sell-price") : defaultSettings.getConfigurationSection("sell-price")
        );

        // Icon Builder
        ProductIconBuilder productIconBuilder = new ProductIconBuilder()
                .setDescLore(productSection.getStringList("desc-lore"))
                .setName(productSection.getString("name"))
                .setAmount(productSection.getInt("amount", defaultSettings.getInt("amount", 1)))
                .setMaterial(material);

        // Item Builder
        ProductItemBuilder productItemBuilder = new ProductItemBuilder()
                .setName(productSection.getString("name"))
                .setAmount(productSection.getInt("amount", defaultSettings.getInt("amount", 1)))
                .setMaterial(material);

        // Rarity
        Rarity rarity = rarityFactory.getRarity(productSection.getString( "rarity", defaultSettings.getString("rarity", RarityConfig.getAllId().get(0))));

        // Cache Item
        boolean isCacheable = productSection.getBoolean("cacheable", defaultSettings.getBoolean("cacheable", true));

        Product product = new VanillaProduct(id, buyPrice, sellPrice, rarity, productIconBuilder, productItemBuilder, isCacheable);
        products.put(id, product);
        return product;
    }

    public Product buildBundleProduct(String id, ConfigurationSection productSection, ConfigurationSection defaultSettings) {
        if (products.containsKey(id)) {
            throw new InvalidKeyException("Product ID is duplicated: " + id);
        }

        // Item
        String item = productSection.getString("item", "DIRT");
        Material material = Material.valueOf(item);

        // Price
        Price buyPrice = new Price(
                productSection.getConfigurationSection( "buy-price") != null ? productSection.getConfigurationSection( "buy-price") : defaultSettings.getConfigurationSection("buy-price")
        );
        Price sellPrice = new Price(
                productSection.getConfigurationSection( "sell-price") != null ? productSection.getConfigurationSection( "sell-price") : defaultSettings.getConfigurationSection("sell-price")
        );

        // Icon Builder
        ProductIconBuilder productIconBuilder = new ProductIconBuilder()
                .setDescLore(productSection.getStringList("desc-lore"))
                .setName(productSection.getString("name"))
                .setAmount(productSection.getInt("amount", defaultSettings.getInt("amount", 1)))
                .setMaterial(material);

        // Item Builder
        ProductItemBuilder productItemBuilder = new ProductItemBuilder()
                .setName(productSection.getString("name"))
                .setAmount(productSection.getInt("amount", defaultSettings.getInt("amount", 1)))
                .setMaterial(material);

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

        // Item
        String item = productSection.getString("item", "DIRT");
        Material material = Material.valueOf(item);

        // Price
        Price buyPrice = new Price(
                productSection.getConfigurationSection( "buy-price") != null ? productSection.getConfigurationSection( "buy-price") : defaultSettings.getConfigurationSection("buy-price")
        );
        Price sellPrice = new Price(
                productSection.getConfigurationSection( "sell-price") != null ? productSection.getConfigurationSection( "sell-price") : defaultSettings.getConfigurationSection("sell-price")
        );

        // Icon Builder
        ProductIconBuilder productIconBuilder = new ProductIconBuilder()
                .setDescLore(productSection.getStringList("desc-lore"))
                .setName(productSection.getString("name"))
                .setAmount(productSection.getInt("amount", defaultSettings.getInt("amount", 1)))
                .setMaterial(material);

        // Item Builder
        ProductItemBuilder productItemBuilder = new ProductItemBuilder()
                .setName(productSection.getString("name"))
                .setAmount(productSection.getInt("amount", defaultSettings.getInt("amount", 1)))
                .setMaterial(material);

        // Rarity
        Rarity rarity = rarityFactory.getRarity(productSection.getString( "rarity", defaultSettings.getString("rarity", RarityConfig.getAllId().get(0))));

        // Bundle Contents
        List<String> commands = productSection.getStringList("commands");

        Product product = new CommandProduct(id, buyPrice, sellPrice, rarity, productIconBuilder, productItemBuilder, commands);
        products.put(id, product);
        return product;
    }

    public Product getProduct(String id) {
        return products.get(id);
    }

    public void unload() {
        products.clear();
    }
}