package cn.encmys.ykdz.forest.dailyshop;

import cn.encmys.ykdz.forest.dailyshop.adventure.AdventureManager;
import cn.encmys.ykdz.forest.dailyshop.command.CommandHandler;
import cn.encmys.ykdz.forest.dailyshop.config.*;
import cn.encmys.ykdz.forest.dailyshop.database.Database;
import cn.encmys.ykdz.forest.dailyshop.factory.ProductFactory;
import cn.encmys.ykdz.forest.dailyshop.factory.RarityFactory;
import cn.encmys.ykdz.forest.dailyshop.factory.ShopFactory;
import cn.encmys.ykdz.forest.dailyshop.hook.ItemsAdderHook;
import cn.encmys.ykdz.forest.dailyshop.hook.MMOItemsHook;
import cn.encmys.ykdz.forest.dailyshop.hook.OraxenHook;
import cn.encmys.ykdz.forest.dailyshop.hook.PlaceholderAPIHook;
import cn.encmys.ykdz.forest.dailyshop.scheduler.Scheduler;
import cn.encmys.ykdz.forest.dailyshop.util.LogUtils;
import dev.jorel.commandapi.CommandAPI;
import dev.jorel.commandapi.CommandAPIBukkitConfig;
import dev.lone.itemsadder.api.Events.ItemsAdderLoadDataEvent;
import me.rubix327.itemslangapi.ItemsLangAPI;
import me.rubix327.itemslangapi.Lang;
import net.milkbowl.vault.economy.Economy;
import org.bstats.bukkit.Metrics;
import org.bukkit.Bukkit;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.plugin.RegisteredServiceProvider;
import org.bukkit.plugin.java.JavaPlugin;

public final class DailyShop extends JavaPlugin implements Listener {
    private static DailyShop instance;
    private static RarityFactory rarityFactory;
    private static ProductFactory productFactory;
    private static ShopFactory shopFactory;
    private static Scheduler scheduler;
    private static Database database;
    private static Economy economy;
    private static AdventureManager adventureManager;
    private static ItemsLangAPI itemsLangAPI;

    @Override
    public void onLoad() {
        instance = this;

        CommandAPI.onLoad(new CommandAPIBukkitConfig(instance));
    }

    @Override
    public void onEnable() {
        if (ItemsAdderHook.isHooked()) {
            Bukkit.getPluginManager().registerEvents(this, instance);
        }

        adventureManager = new AdventureManager(instance);

        if (!setupEconomy()) {
            LogUtils.error("Disabled due to no Vault dependency found!");
            getServer().getPluginManager().disablePlugin(this);
            return;
        }

        new PlaceholderAPIHook();
        new MMOItemsHook();
        new ItemsAdderHook();
        new OraxenHook();

        Config.load();
        MessageConfig.load();
        RarityConfig.load();
        ProductConfig.load();
        ShopConfig.load();

        if (!setupItemsLangAPI()) {
            return;
        }

        database = new Database(instance.getDataFolder().getPath());

        if (!ItemsAdderHook.isHooked()) {
            init();
        }

        CommandAPI.onEnable();
        new CommandHandler(instance).load();

        if (!setupBStats()) {
            return;
        }
    }

    @EventHandler
    public void waitForItemsAdder(ItemsAdderLoadDataEvent e) {
        init();
    }

    private void init() {
        rarityFactory = new RarityFactory();
        productFactory = new ProductFactory();
        shopFactory = new ShopFactory();

        scheduler = new Scheduler(instance);
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
        itemsLangAPI = ItemsLangAPI.getApi();
        itemsLangAPI.load(Lang.valueOf(Config.language.toUpperCase()));
        return true;
    }

    private boolean setupBStats() {
        int pluginId = 21305;
        Metrics metrics = new Metrics(this, pluginId);
        return true;
    }

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

    public static Scheduler getScheduler() {
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

        itemsLangAPI.load(Lang.valueOf(Config.language.toUpperCase()));

        rarityFactory = new RarityFactory();
        productFactory = new ProductFactory();
        shopFactory = new ShopFactory();
    }
}
