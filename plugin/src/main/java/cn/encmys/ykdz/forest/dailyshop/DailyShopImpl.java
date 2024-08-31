package cn.encmys.ykdz.forest.dailyshop;

import cn.encmys.ykdz.forest.dailyshop.adventure.AdventureManagerImpl;
import cn.encmys.ykdz.forest.dailyshop.api.DailyShop;
import cn.encmys.ykdz.forest.dailyshop.api.config.*;
import cn.encmys.ykdz.forest.dailyshop.api.utils.LogUtils;
import cn.encmys.ykdz.forest.dailyshop.command.CommandHandler;
import cn.encmys.ykdz.forest.dailyshop.database.factory.DatabaseFactoryImpl;
import cn.encmys.ykdz.forest.dailyshop.hook.ItemsAdderHook;
import cn.encmys.ykdz.forest.dailyshop.hook.MMOItemsHook;
import cn.encmys.ykdz.forest.dailyshop.hook.MythicMobsHook;
import cn.encmys.ykdz.forest.dailyshop.hook.PlaceholderAPIHook;
import cn.encmys.ykdz.forest.dailyshop.listener.ItemsAdderListener;
import cn.encmys.ykdz.forest.dailyshop.listener.PlayerListener;
import cn.encmys.ykdz.forest.dailyshop.product.factory.ProductFactoryImpl;
import cn.encmys.ykdz.forest.dailyshop.profile.factory.ProfileFactoryImpl;
import cn.encmys.ykdz.forest.dailyshop.rarity.factory.RarityFactoryImpl;
import cn.encmys.ykdz.forest.dailyshop.scheduler.SchedulerImpl;
import cn.encmys.ykdz.forest.dailyshop.shop.factory.ShopFactoryImpl;
import dev.jorel.commandapi.CommandAPI;
import dev.jorel.commandapi.CommandAPIBukkitConfig;
import net.milkbowl.vault.economy.Economy;
import org.bstats.bukkit.Metrics;
import org.bukkit.Bukkit;
import org.bukkit.plugin.RegisteredServiceProvider;

import java.util.concurrent.CompletableFuture;

public final class DailyShopImpl extends DailyShop {
    @Override
    public void reload() {
        DailyShop.PROFILE_FACTORY.unload();
        DailyShop.SHOP_FACTORY.unload();
        DailyShop.PRODUCT_FACTORY.unload();

        Config.load();
        CompletableFuture.runAsync(MinecraftLangConfig::load);
        MessageConfig.load();
        RarityConfig.load();
        ProductConfig.load();
        ShopConfig.load();
        CartGUIConfig.load();
        StackPickerGUIConfig.load();
        OrderHistoryGUIConfig.load();

        saveDefaultConfig();

        DailyShop.DATABASE_FACTORY = new DatabaseFactoryImpl();
        DailyShop.PROFILE_FACTORY = new ProfileFactoryImpl();
        DailyShop.RARITY_FACTORY = new RarityFactoryImpl();
        DailyShop.PRODUCT_FACTORY = new ProductFactoryImpl();
        DailyShop.SHOP_FACTORY = new ShopFactoryImpl();
    }

    @Override
    public void onLoad() {
        INSTANCE = this;
        CommandAPI.onLoad(new CommandAPIBukkitConfig(INSTANCE));
    }

    @Override
    public void onEnable() {
        if (ItemsAdderHook.isHooked()) {
            Bukkit.getPluginManager().registerEvents(new ItemsAdderListener(), INSTANCE);
        } else {
            init();
        }
    }

    @Override
    public void init() {
        Bukkit.getPluginManager().registerEvents(new PlayerListener(), INSTANCE);

        ADVENTURE_MANAGER = new AdventureManagerImpl(INSTANCE);

        if (!setupEconomy()) {
            LogUtils.error("Disabled due to no Vault dependency found!");
            getServer().getPluginManager().disablePlugin(this);
            return;
        }

        PlaceholderAPIHook.load();
        MMOItemsHook.load();
        ItemsAdderHook.load();
        MythicMobsHook.load();

        Config.load();
        MinecraftLangConfig.load();
        MessageConfig.load();
        RarityConfig.load();
        ProductConfig.load();
        ShopConfig.load();
        CartGUIConfig.load();
        StackPickerGUIConfig.load();
        OrderHistoryGUIConfig.load();

        DailyShop.DATABASE_FACTORY = new DatabaseFactoryImpl();
        DailyShopImpl.PROFILE_FACTORY = new ProfileFactoryImpl();
        DailyShopImpl.RARITY_FACTORY = new RarityFactoryImpl();
        DailyShopImpl.PRODUCT_FACTORY = new ProductFactoryImpl();
        DailyShopImpl.SHOP_FACTORY = new ShopFactoryImpl();

        DailyShopImpl.SCHEDULER = new SchedulerImpl();

        CommandAPI.onEnable();
        CommandHandler.load();

        setupBStats();
    }

    @Override
    public void onDisable() {
        PROFILE_FACTORY.unload();
        SHOP_FACTORY.unload();
        PRODUCT_FACTORY.unload();

        CommandAPI.onDisable();
    }

    @Override
    public boolean setupEconomy() {
        if (getServer().getPluginManager().getPlugin("Vault") == null) {
            return false;
        }
        RegisteredServiceProvider<Economy> rsp = getServer().getServicesManager().getRegistration(Economy.class);
        if (rsp == null) {
            return false;
        }
        DailyShopImpl.ECONOMY = rsp.getProvider();
        return true;
    }

    @Override
    public void setupBStats() {
        int pluginId = 21305;
        DailyShopImpl.METRICS = new Metrics(this, pluginId);
    }
}
