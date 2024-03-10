package cn.encmys.ykdz.forest.dailyshop;

import cn.encmys.ykdz.forest.dailyshop.adventure.AdventureManager;
import cn.encmys.ykdz.forest.dailyshop.command.CommandHandler;
import cn.encmys.ykdz.forest.dailyshop.config.*;
import cn.encmys.ykdz.forest.dailyshop.data.Database;
import cn.encmys.ykdz.forest.dailyshop.factory.ProductFactory;
import cn.encmys.ykdz.forest.dailyshop.factory.RarityFactory;
import cn.encmys.ykdz.forest.dailyshop.factory.ShopFactory;
import cn.encmys.ykdz.forest.dailyshop.hook.PlaceholderAPIHook;
import cn.encmys.ykdz.forest.dailyshop.scheduler.Scheduler;
import dev.jorel.commandapi.CommandAPI;
import dev.jorel.commandapi.CommandAPIBukkitConfig;
import me.rubix327.itemslangapi.ItemsLangAPI;
import me.rubix327.itemslangapi.Lang;
import net.milkbowl.vault.economy.Economy;
import org.bukkit.Bukkit;
import org.bukkit.plugin.RegisteredServiceProvider;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.logging.Level;

public final class DailyShop extends JavaPlugin {
    private static DailyShop instance;
    private static RarityFactory rarityFactory;
    private static ProductFactory productFactory;
    private static ShopFactory shopFactory;
    private static Scheduler scheduler;
    private static Database database;
    private static Economy economy;
    private static AdventureManager adventureManager;
    private static ItemsLangAPI itemsLangAPI;

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

    public static Database getDatabase() {
        return database;
    }

    public static Scheduler getRestockScheduler() {
        return scheduler;
    }

    public static AdventureManager getAdventureManager() {
        return adventureManager;
    }

    public static ItemsLangAPI getItemsLangAPI() {
        return itemsLangAPI;
    }

    public static void reload() {
        shopFactory.unload();
        productFactory.unload();

        Config.load();
        MessageConfig.load();
        RarityConfig.load();
        ProductConfig.load();
        ShopConfig.load();

        rarityFactory = new RarityFactory();
        productFactory = new ProductFactory();
        shopFactory = new ShopFactory();
    }

    @Override
    public void onLoad() {
        instance = this;

        CommandAPI.onLoad(new CommandAPIBukkitConfig(instance));
    }

    @Override
    public void onEnable() {
        if (!setupEconomy()) {
            getLogger().severe(String.format("[%s] - Disabled due to no Vault dependency found!", getDescription().getName()));
            getServer().getPluginManager().disablePlugin(this);
            return;
        }

        if (setupItemsLangAPI()) {
            getLogger().log(Level.INFO, "Hooked into ItemsLangAPI!");
        }

        adventureManager = new AdventureManager(instance);

        Config.load();
        MessageConfig.load();
        RarityConfig.load();
        ProductConfig.load();
        ShopConfig.load();

        database = new Database(instance.getDataFolder().getPath());

        rarityFactory = new RarityFactory();
        productFactory = new ProductFactory();
        shopFactory = new ShopFactory();

        scheduler = new Scheduler(instance);

        CommandAPI.onEnable();
        new CommandHandler(instance).load();

        if(Bukkit.getPluginManager().getPlugin("PlaceholderAPI") != null) {
            new PlaceholderAPIHook(this).register();
        }
    }

    @Override
    public void onDisable() {
        shopFactory.unload();
        productFactory.unload();

        CommandAPI.onDisable();
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

    private boolean setupItemsLangAPI() {
        if (getServer().getPluginManager().getPlugin("ItemsLangAPI") == null) {
            return false;
        }
        itemsLangAPI = ItemsLangAPI.getApi();
        itemsLangAPI.load(Lang.EN_US, Lang.ZH_CN);
        return true;
    }
}
