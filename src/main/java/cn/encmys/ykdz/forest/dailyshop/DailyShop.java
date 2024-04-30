package cn.encmys.ykdz.forest.dailyshop;

import cn.encmys.ykdz.forest.dailyshop.adventure.AdventureManager;
import cn.encmys.ykdz.forest.dailyshop.api.database.Database;
import cn.encmys.ykdz.forest.dailyshop.command.CommandHandler;
import cn.encmys.ykdz.forest.dailyshop.config.*;
import cn.encmys.ykdz.forest.dailyshop.database.SQLiteDatabase;
import cn.encmys.ykdz.forest.dailyshop.hook.*;
import cn.encmys.ykdz.forest.dailyshop.product.factory.ProductFactory;
import cn.encmys.ykdz.forest.dailyshop.rarity.factory.RarityFactory;
import cn.encmys.ykdz.forest.dailyshop.scheduler.Scheduler;
import cn.encmys.ykdz.forest.dailyshop.shop.factory.ShopFactory;
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
    public static DailyShop INSTANCE;
    public static RarityFactory RARITY_FACTORY;
    public static ProductFactory PRODUCT_FACTORY;
    public static ShopFactory SHOP_FACTORY;
    public static Scheduler SCHEDULER;
    public static Database DATABASE;
    public static Economy ECONOMY;
    public static AdventureManager ADVENTURE_MANAGER;
    public static ItemsLangAPI ITEMSLANG_API;
    public static Metrics METRICS;

    @Override
    public void onLoad() {
        INSTANCE = this;

        CommandAPI.onLoad(new CommandAPIBukkitConfig(INSTANCE));
    }

    @Override
    public void onEnable() {
        if (ItemsAdderHook.isHooked()) {
            Bukkit.getPluginManager().registerEvents(this, INSTANCE);
        }

        ADVENTURE_MANAGER = new AdventureManager(INSTANCE);

        if (!setupEconomy()) {
            LogUtils.error("Disabled due to no Vault dependency found!");
            getServer().getPluginManager().disablePlugin(this);
            return;
        }

        new PlaceholderAPIHook();
        new MMOItemsHook();
        new ItemsAdderHook();
        new OraxenHook();
        new MythicMobsHook();
        new NeigeItemsHook();
        new CustomCropsHook();
        new CustomFishingHook();

        Config.load();
        MessageConfig.load();
        RarityConfig.load();
        ProductConfig.load();
        ShopConfig.load();

        if (!setupItemsLangAPI()) {
            return;
        }

        DATABASE = new SQLiteDatabase();

        if (!ItemsAdderHook.isHooked()) {
            init();
        }

        CommandAPI.onEnable();
        new CommandHandler(INSTANCE).load();

        if (!setupBStats()) {
            return;
        }
    }

    @EventHandler
    public void waitForItemsAdder(ItemsAdderLoadDataEvent e) {
        init();
    }

    private void init() {
        RARITY_FACTORY = new RarityFactory();
        PRODUCT_FACTORY = new ProductFactory();
        SHOP_FACTORY = new ShopFactory();

        SCHEDULER = new Scheduler();
    }

    @Override
    public void onDisable() {
        SHOP_FACTORY.unload();
        PRODUCT_FACTORY.unload();

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
        ECONOMY = rsp.getProvider();
        return ECONOMY != null;
    }

    private boolean setupItemsLangAPI() {
        ITEMSLANG_API = ItemsLangAPI.getApi();
        ITEMSLANG_API.load(Lang.valueOf(Config.language.toUpperCase()));
        return true;
    }

    private boolean setupBStats() {
        int pluginId = 21305;
        METRICS = new Metrics(this, pluginId);
        return true;
    }

    public static void reload() {
        SHOP_FACTORY.unload();
        PRODUCT_FACTORY.unload();

        Config.load();
        MessageConfig.load();
        RarityConfig.load();
        ProductConfig.load();
        ShopConfig.load();

        ITEMSLANG_API.load(Lang.valueOf(Config.language.toUpperCase()));

        RARITY_FACTORY = new RarityFactory();
        PRODUCT_FACTORY = new ProductFactory();
        SHOP_FACTORY = new ShopFactory();
    }
}
