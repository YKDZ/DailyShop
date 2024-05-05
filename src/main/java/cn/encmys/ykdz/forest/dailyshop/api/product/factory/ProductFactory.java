package cn.encmys.ykdz.forest.dailyshop.api.product.factory;

import cn.encmys.ykdz.forest.dailyshop.api.product.Product;
import org.bukkit.configuration.ConfigurationSection;

import java.util.HashMap;

public interface ProductFactory {
    void buildProduct(String id, ConfigurationSection productSection, ConfigurationSection defaultSettings);

    HashMap<String, Product> getAllProducts();

    Product getProduct(String id);

    boolean containsProduct(String id);

    void unload();
}
