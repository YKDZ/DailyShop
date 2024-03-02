package cn.encmys.ykdz.forest.dailyshop;

import cn.encmys.ykdz.forest.dailyshop.config.Config;
import cn.encmys.ykdz.forest.dailyshop.config.ProductConfig;
import cn.encmys.ykdz.forest.dailyshop.config.RaritiesConfig;
import cn.encmys.ykdz.forest.dailyshop.config.ShopConfig;
import cn.encmys.ykdz.forest.dailyshop.factory.ProductFactory;
import cn.encmys.ykdz.forest.dailyshop.factory.ShopFactory;
import org.bukkit.plugin.java.JavaPlugin;

public final class DailyShop extends JavaPlugin {
    private static DailyShop instance;
    private static ProductFactory productFactory;
    private static ShopFactory shopFactory;

    @Override
    public void onEnable() {
        instance = this;

        Config.load();
        RaritiesConfig.load();
        ProductConfig.load();
        ShopConfig.load();

        productFactory = new ProductFactory();
        shopFactory = new ShopFactory();
    }

    @Override
    public void onDisable() {
        // Plugin shutdown logic
    }

    public static DailyShop getInstance() {
        return instance;
    }

    public static ProductFactory getProductFactory() {
        return productFactory;
    }

    public static ShopFactory getShopFactory() {
        return shopFactory;
    }
}
