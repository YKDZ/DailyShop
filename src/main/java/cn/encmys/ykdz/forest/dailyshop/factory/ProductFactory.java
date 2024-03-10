package cn.encmys.ykdz.forest.dailyshop.factory;

import cn.encmys.ykdz.forest.dailyshop.DailyShop;
import cn.encmys.ykdz.forest.dailyshop.api.product.Product;
import cn.encmys.ykdz.forest.dailyshop.config.ProductConfig;
import cn.encmys.ykdz.forest.dailyshop.price.PriceProvider;
import cn.encmys.ykdz.forest.dailyshop.product.BundleProduct;
import cn.encmys.ykdz.forest.dailyshop.product.CommandProduct;
import cn.encmys.ykdz.forest.dailyshop.product.VanillaProduct;
import cn.encmys.ykdz.forest.dailyshop.rarity.Rarity;
import org.bukkit.Material;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.YamlConfiguration;
import org.jetbrains.annotations.Nullable;

import javax.management.openmbean.InvalidKeyException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class ProductFactory {
    private static final HashMap<String, Product> products = new HashMap<>();

    public ProductFactory() {
        for (String configId : ProductConfig.getAllId()) {
            RarityFactory rarityFactory = DailyShop.getRarityFactory();
            YamlConfiguration config = ProductConfig.getConfig(configId);
            ConfigurationSection products = config.getConfigurationSection("products");
            ConfigurationSection defaultSettings = config.getConfigurationSection("default-settings");

            List<String> bundles = new ArrayList<>();
            for (String productId : products.getKeys(false)) {
                ConfigurationSection productSection = products.getConfigurationSection(productId);

                // Record Bundle
                if (!productSection.getStringList("bundle-contents").isEmpty()) {
                    bundles.add(productId);
                    continue;
                }

                String item = productSection.getString("item", "DIRT");
                Material material = Material.valueOf(item);
                PriceProvider buyPriceProvider = new PriceProvider(
                        productSection.getConfigurationSection( "buy-price") != null ? productSection.getConfigurationSection( "buy-price") : defaultSettings.getConfigurationSection("buy-price")
                );
                PriceProvider sellPriceProvider = new PriceProvider(
                        productSection.getConfigurationSection( "sell-price") != null ? productSection.getConfigurationSection( "sell-price") : defaultSettings.getConfigurationSection("sell-price")
                );

                if (productSection.contains("commands")) {
                    buildCommandProduct(
                            productId,
                            buyPriceProvider,
                            sellPriceProvider,
                            rarityFactory.getRarity(productSection.getString( "rarity", defaultSettings.getString("rarity"))),
                            material,
                            productSection.getInt( "amount", defaultSettings.getInt("amount", 1)),
                            productSection.getString( "display-name"),
                            productSection.getStringList( "desc-lore"),
                            productSection.getStringList( "commands")
                    );
                } else {
                    buildVanillaProduct(
                            productId,
                            buyPriceProvider,
                            sellPriceProvider,
                            rarityFactory.getRarity(productSection.getString( "rarity", defaultSettings.getString("rarity"))),
                            material,
                            productSection.getInt( "amount", defaultSettings.getInt("amount", 1)),
                            productSection.getString( "display-name"),
                            productSection.getStringList( "desc-lore"),
                            productSection.getStringList( "product-lore")
                    );
                }
            }

            // Handle Bundle
            for (String productId : bundles) {
                ConfigurationSection productSection = products.getConfigurationSection(productId);
                Material material = Material.valueOf(productSection.getString("item", "DIRT"));
                PriceProvider buyPriceProvider = new PriceProvider(
                        productSection.getConfigurationSection( "buy-price") != null ? productSection.getConfigurationSection( ".buy-price") : defaultSettings.getConfigurationSection("buy-price")
                );
                PriceProvider sellPriceProvider = new PriceProvider(
                        productSection.getConfigurationSection("sell-price") != null ? productSection.getConfigurationSection(".sell-price") : defaultSettings.getConfigurationSection("sell-price")
                );
                buildBundleProduct(
                        productId,
                        buyPriceProvider,
                        sellPriceProvider,
                        rarityFactory.getRarity(productSection.getString("rarity", defaultSettings.getString("rarity"))),
                        material,
                        productSection.getInt("amount", defaultSettings.getInt("amount", 1)),
                        productSection.getString( "display-name"),
                        productSection.getStringList( "desc-lore"),
                        productSection.getStringList( "bundle-contents")
                );
            }
        }
    }

    public Product getProduct(String id) {
        return products.get(id);
    }

    public Product buildVanillaProduct(String id,
                                       PriceProvider buyPriceProvider,
                                       PriceProvider sellPriceProvider,
                                       Rarity rarity,
                                       Material material,
                                       int amount,
                                       @Nullable String displayName,
                                       @Nullable List<String> descLore,
                                       @Nullable List<String> productLore) {
        if (products.containsKey(id)) {
            throw new InvalidKeyException("Product ID is duplicated: " + id);
        }

        Product product = new VanillaProduct(id, buyPriceProvider, sellPriceProvider, rarity, material, amount, displayName, descLore, productLore);
        products.put(id, product);
        return product;
    }

    public Product buildCommandProduct(String id,
                                       PriceProvider buyPriceProvider,
                                       PriceProvider sellPriceProvider,
                                       Rarity rarity,
                                       Material material,
                                       int amount,
                                       @Nullable String displayName,
                                       @Nullable List<String> descLore,
                                       List<String> commands) {
        if (products.containsKey(id)) {
            throw new InvalidKeyException("Product ID is duplicated: " + id);
        }

        Product product = new CommandProduct(id, buyPriceProvider, sellPriceProvider, rarity, material, amount, displayName, descLore, commands);
        products.put(id, product);
        return product;
    }

    public Product buildBundleProduct(String id,
                                      PriceProvider buyPriceProvider,
                                      PriceProvider sellPriceProvider,
                                      Rarity rarity,
                                      Material material,
                                      int amount,
                                      @Nullable String displayName,
                                      @Nullable List<String> descLore,
                                      @Nullable List<String> contents) {
        if (products.containsKey(id)) {
            throw new InvalidKeyException("Product ID is duplicated: " + id);
        }

        Product product = new BundleProduct(id, buyPriceProvider, sellPriceProvider, rarity, material, amount, displayName, descLore, contents);
        products.put(id, product);
        return product;
    }

    public void unload() {
        products.clear();
    }
}