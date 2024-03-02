package cn.encmys.ykdz.forest.dailyshop.factory;

import cn.encmys.ykdz.forest.dailyshop.DailyShop;
import cn.encmys.ykdz.forest.dailyshop.api.product.Product;
import cn.encmys.ykdz.forest.dailyshop.config.ShopConfig;
import cn.encmys.ykdz.forest.dailyshop.shop.Shop;
import org.bukkit.configuration.file.YamlConfiguration;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class ShopFactory {
    private static HashMap<String, Shop> shops = new HashMap<>();

    public ShopFactory() {

    }

    public Shop buildShop(String id) {
        YamlConfiguration config = ShopConfig.getConfig(id);

        List<Product> products = new ArrayList<>();
        for(String productId : config.getStringList("products")) {
            DailyShop.getProductFactory().getProduct(productId);
        }

        Shop shop = new Shop(
                id,
                config.getInt("restock-timer"),
                products,
                config.getConfigurationSection("shop-gui"));

        shops.put(id, shop);
        return shop;
    }

    public Shop getShop(String id) {
        return shops.get(id);
    }
}
