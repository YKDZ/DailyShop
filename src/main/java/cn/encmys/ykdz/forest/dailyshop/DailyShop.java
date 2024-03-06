package cn.encmys.ykdz.forest.dailyshop;

import cn.encmys.ykdz.forest.dailyshop.command.CommandHandler;
import cn.encmys.ykdz.forest.dailyshop.config.Config;
import cn.encmys.ykdz.forest.dailyshop.config.ProductConfig;
import cn.encmys.ykdz.forest.dailyshop.config.RaritiesConfig;
import cn.encmys.ykdz.forest.dailyshop.config.ShopConfig;
import cn.encmys.ykdz.forest.dailyshop.factory.ProductFactory;
import cn.encmys.ykdz.forest.dailyshop.factory.RarityFactory;
import cn.encmys.ykdz.forest.dailyshop.factory.ShopFactory;
import dev.jorel.commandapi.CommandAPI;
import dev.jorel.commandapi.CommandAPIBukkitConfig;
import net.milkbowl.vault.economy.Economy;
import org.bukkit.plugin.RegisteredServiceProvider;
import org.bukkit.plugin.java.JavaPlugin;

public final class DailyShop extends JavaPlugin {
    private static DailyShop instance;
    private static RarityFactory rarityFactory;
    private static ProductFactory productFactory;
    private static ShopFactory shopFactory;
    private static Economy economy;

    public static DailyShop getInstance() {
        return instance;
    }

    public static RarityFactory getRarityFactory() {
        return rarityFactory;
    }

    public static ProductFactory getProductFactory() {
        return productFactory;
    }

    public static ShopFactory getShopFactory() {
        return shopFactory;
    }

    public static Economy getEconomy() {
        return economy;
    }

    @Override
    public void onLoad() {
        instance = this;

        CommandAPI.onLoad(new CommandAPIBukkitConfig(instance));
    }

    @Override
    public void onEnable() {
        if (!setupEconomy() ) {
            getLogger().severe(String.format("[%s] - Disabled due to no Vault dependency found!", getDescription().getName()));
            getServer().getPluginManager().disablePlugin(this);
            return;
        }

        Config.load();
        RaritiesConfig.load();
        ProductConfig.load();
        ShopConfig.load();

        rarityFactory = new RarityFactory();
        productFactory = new ProductFactory();
        shopFactory = new ShopFactory();

        CommandAPI.onEnable();
        new CommandHandler(instance).load();
    }

    @Override
    public void onDisable() {
        CommandAPI.onDisable();
    }

    public static void reload() {
        productFactory.unload();
        shopFactory.unload();

        Config.load();
        RaritiesConfig.load();
        ProductConfig.load();
        ShopConfig.load();

        productFactory = new ProductFactory();
        shopFactory = new ShopFactory();
    }

    private boolean setupEconomy() {
        if (getServer().getPluginManager().getPlugin("Vault") == null) {
            return false;
        }
        RegisteredServiceProvider<Economy> rsp = getServer().getServicesManager().getRegistration(Economy.class);
        if (rsp == null) {
            return false;
        }
        economy = rsp.getProvider();
        return economy != null;
    }
}
