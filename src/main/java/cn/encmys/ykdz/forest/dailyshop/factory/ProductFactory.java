package cn.encmys.ykdz.forest.dailyshop.factory;

import cn.encmys.ykdz.forest.dailyshop.api.product.Product;
import cn.encmys.ykdz.forest.dailyshop.product.BundleProduct;
import cn.encmys.ykdz.forest.dailyshop.product.VanillaProduct;
import org.bukkit.Material;
import org.jetbrains.annotations.Nullable;

import javax.management.openmbean.InvalidKeyException;
import java.util.HashMap;
import java.util.List;

public class ProductFactory {
    private static HashMap<String, Product> products = new HashMap<>();

    public static Product getProduct(String id) {
        return products.get(id);
    }

    public static Product buildVanillaProduct(String id,
                                              Material material,
                                              int amount ,
                                              @Nullable String displayName,
                                              @Nullable List<String> displayedLore,
                                              @Nullable List<String> productLore) {
        if(products.containsKey(id)) {
            throw new InvalidKeyException("Product ID is duplicated: " + id);
        }

        Product product = new VanillaProduct(id, material, amount, displayName, displayedLore, productLore);
        products.put(id, product);
        return product;
    }

    public static Product buildBundleProduct(String id,
                                              Material material,
                                              int amount ,
                                              @Nullable String displayName,
                                              @Nullable List<String> displayedLore,
                                              @Nullable List<String> contents) {
        if(products.containsKey(id)) {
            throw new InvalidKeyException("Product ID is duplicated: " + id);
        }

        Product product = new BundleProduct(id, material, amount, displayName, displayedLore, contents);
        products.put(id, product);
        return product;
    }
}
