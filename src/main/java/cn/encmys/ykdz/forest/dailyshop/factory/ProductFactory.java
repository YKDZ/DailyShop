package cn.encmys.ykdz.forest.dailyshop.factory;

import cn.encmys.ykdz.forest.dailyshop.api.product.Product;
import cn.encmys.ykdz.forest.dailyshop.config.ProductConfig;
import cn.encmys.ykdz.forest.dailyshop.enums.ProductType;
import cn.encmys.ykdz.forest.dailyshop.price.PriceProvider;
import cn.encmys.ykdz.forest.dailyshop.product.BundleProduct;
import cn.encmys.ykdz.forest.dailyshop.product.VanillaProduct;
import org.bukkit.Material;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.YamlConfiguration;
import org.jetbrains.annotations.Nullable;

import javax.management.openmbean.InvalidKeyException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class ProductFactory {
    private static HashMap<String, Product> products = new HashMap<>();
    private static HashMap<String, ProductType> productTypes = new HashMap<>();

    public ProductFactory() {
        for(String configId : ProductConfig.getAllId()) {
            YamlConfiguration config = ProductConfig.getConfig(configId);
            ConfigurationSection products = config.getConfigurationSection("products");
            ConfigurationSection defaultSettings = config.getConfigurationSection("default-settings");

            List<String> bundles = new ArrayList<>();
            for(String productId : products.getKeys(false)) {

                // Record Bundle
                if(!products.getStringList(productId + ".bundle-contents").isEmpty()) {
                    bundles.add(productId);
                    continue;
                }

                String item = products.getString(productId + ".item");
                Material material = Material.matchMaterial(item);
                buildVanillaProduct(
                        productId,
                        new PriceProvider(products.getConfigurationSection(productId + ".buy-price"), products.getConfigurationSection(productId + ".sell-price")),
                        products.getString(productId + ".rarity"),
                        material,
                        products.getInt(productId + ".amount", 1),
                        products.getString(productId + ".display-name"),
                        products.getStringList(productId + ".desc-lore"),
                        products.getStringList(productId + ".product-lore")
                );
            }

            // Handle Bundle
            for(String id : bundles) {
                Material material = Material.matchMaterial(products.getString(id + ".item"));
                buildBundleProduct(
                        id,
                        new PriceProvider(products.getConfigurationSection(id + ".buy-price"), products.getConfigurationSection(id + ".sell-price")),
                        products.getString(id + ".rarity"),
                        material,
                        products.getInt(id + ".amount", 1),
                        products.getString(id + ".display-name"),
                        products.getStringList(id + ".desc-lore"),
                        products.getStringList(id + ".bundle-contents")
                        );
            }
        }
    }

    public Product getProduct(String id) {
        return products.get(id);
    }

    public Product buildVanillaProduct(String id,
                                              PriceProvider priceProvider,
                                              String rarity,
                                              Material material,
                                              int amount ,
                                              @Nullable String displayName,
                                              @Nullable List<String> descLore,
                                              @Nullable List<String> productLore) {
        if(products.containsKey(id)) {
            throw new InvalidKeyException("Product ID is duplicated: " + id);
        }

        Product product = new VanillaProduct(id, priceProvider, rarity, material, amount, displayName, descLore, productLore);
        products.put(id, product);
        productTypes.put(id, ProductType.VANILLA);
        return product;
    }

    public Product buildBundleProduct(String id,
                                             PriceProvider priceProvider,
                                             String rarity,
                                             Material material,
                                             int amount ,
                                             @Nullable String displayName,
                                             @Nullable List<String> descLore,
                                             @Nullable List<String> contents) {
        if(products.containsKey(id)) {
            throw new InvalidKeyException("Product ID is duplicated: " + id);
        }

        Product product = new BundleProduct(id, priceProvider, rarity, material, amount, displayName, descLore, contents);
        products.put(id, product);
        productTypes.put(id, ProductType.BUNDLE);
        return product;
    }
}
