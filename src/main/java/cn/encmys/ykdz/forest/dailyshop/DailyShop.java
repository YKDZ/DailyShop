package cn.encmys.ykdz.forest.dailyshop;

import cn.encmys.ykdz.forest.dailyshop.command.CommandHandler;
import cn.encmys.ykdz.forest.dailyshop.config.Config;
import cn.encmys.ykdz.forest.dailyshop.config.ProductConfig;
import cn.encmys.ykdz.forest.dailyshop.config.RaritiesConfig;
import cn.encmys.ykdz.forest.dailyshop.config.ShopConfig;
import cn.encmys.ykdz.forest.dailyshop.factory.ProductFactory;
import cn.encmys.ykdz.forest.dailyshop.factory.ShopFactory;
import dev.jorel.commandapi.CommandAPI;
import dev.jorel.commandapi.CommandAPIBukkitConfig;
import org.bukkit.plugin.java.JavaPlugin;

public final class DailyShop extends JavaPlugin {
    private static DailyShop instance;
    private static ProductFactory productFactory;
    private static ShopFactory shopFactory;

    @Override
    public void onLoad() {
        instance = this;

        CommandAPI.onLoad(new CommandAPIBukkitConfig(instance));
    }

    @Override
    public void onEnable() {
        Config.load();
        RaritiesConfig.load();
        ProductConfig.load();
        ShopConfig.load();

        CommandAPI.onEnable();

        productFactory = new ProductFactory();
        shopFactory = new ShopFactory();

        new CommandHandler(instance).load();
    }

    @Override
    public void onDisable() {
        CommandAPI.onDisable();
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
