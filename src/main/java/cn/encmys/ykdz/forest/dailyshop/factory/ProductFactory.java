package cn.encmys.ykdz.forest.dailyshop.factory;

import cn.encmys.ykdz.forest.dailyshop.api.product.Product;
import cn.encmys.ykdz.forest.dailyshop.config.ProductConfig;
import cn.encmys.ykdz.forest.dailyshop.price.PriceProvider;
import cn.encmys.ykdz.forest.dailyshop.product.BundleProduct;
import cn.encmys.ykdz.forest.dailyshop.product.VanillaProduct;
import org.bukkit.Material;
import org.bukkit.configuration.file.YamlConfiguration;
import org.jetbrains.annotations.Nullable;

import javax.management.openmbean.InvalidKeyException;
import java.util.HashMap;
import java.util.List;

public class ProductFactory {
    private static HashMap<String, Product> products = new HashMap<>();

    public ProductFactory() {
        for(String id : ProductConfig.getAllId()) {
            YamlConfiguration config = ProductConfig.getConfig(id);
            config.getConfigurationSection("");

        }
    }

    public static Product getProduct(String id) {
        return products.get(id);
    }

    public static Product buildVanillaProduct(String id,
                                              PriceProvider priceProvider,
                                              String rarity,
                                              Material material,
                                              int amount ,
                                              @Nullable String displayName,
                                              @Nullable List<String> displayedLore,
                                              @Nullable List<String> productLore) {
        if(products.containsKey(id)) {
            throw new InvalidKeyException("Product ID is duplicated: " + id);
        }

        Product product = new VanillaProduct(id, priceProvider, rarity, material, amount, displayName, displayedLore, productLore);
        products.put(id, product);
        return product;
    }

    public static Product buildBundleProduct(String id,
                                             PriceProvider priceProvider,
                                             String rarity,
                                             Material material,
                                             int amount ,
                                             @Nullable String displayName,
                                             @Nullable List<String> displayedLore,
                                             @Nullable List<String> contents) {
        if(products.containsKey(id)) {
            throw new InvalidKeyException("Product ID is duplicated: " + id);
        }

        Product product = new BundleProduct(id, priceProvider, rarity, material, amount, displayName, displayedLore, contents);
        products.put(id, product);
        return product;
    }
}
