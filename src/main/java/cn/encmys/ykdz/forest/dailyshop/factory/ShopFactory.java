package cn.encmys.ykdz.forest.dailyshop.factory;

import cn.encmys.ykdz.forest.dailyshop.DailyShop;
import cn.encmys.ykdz.forest.dailyshop.api.product.Product;
import cn.encmys.ykdz.forest.dailyshop.config.ShopConfig;
import cn.encmys.ykdz.forest.dailyshop.shop.Shop;
import org.bukkit.configuration.file.YamlConfiguration;

import javax.management.openmbean.InvalidKeyException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class ShopFactory {
    private static final HashMap<String, Shop> shops = new HashMap<>();

    public ShopFactory() {
        for (String id : ShopConfig.getAllId()) {
            buildShop(id);
        }
    }

    public Shop buildShop(String id) {
        if (shops.containsKey(id)) {
            throw new InvalidKeyException("Shop ID is duplicated: " + id);
        }

        YamlConfiguration config = ShopConfig.getConfig(id);

        List<Product> products = new ArrayList<>();
        for (String productId : config.getStringList("products")) {
            products.add(DailyShop.getProductFactory().getProduct(productId));
        }

        Shop shop = new Shop(
                id,
                config.getInt("restock-timer"),
                products,
                config.getInt("size"),
                config.getConfigurationSection("shop-gui"));

        shops.put(id, shop);
        return shop;
    }

    public Shop getShop(String id) {
        return shops.get(id);
    }

    public void unload() {
        shops.clear();
    }
}
