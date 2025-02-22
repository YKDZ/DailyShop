package cn.encmys.ykdz.forest.hyphashop.api.product.factory;

import cn.encmys.ykdz.forest.hyphashop.api.product.Product;
import org.bukkit.configuration.ConfigurationSection;
import org.jetbrains.annotations.Nullable;

import java.util.Map;

public interface ProductFactory {
    void buildProduct(String id, ConfigurationSection productSection, ConfigurationSection defaultSettings);

    Map<String, Product> getProducts();

    @Nullable
    Product getProduct(String id);

    boolean containsProduct(String id);

    void unload();

    void save();
}
