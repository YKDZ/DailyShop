package cn.encmys.ykdz.forest.hyphashop;

import cn.encmys.ykdz.forest.hyphashop.api.HyphaShop;
import cn.encmys.ykdz.forest.hyphashop.config.*;
import cn.encmys.ykdz.forest.hyphashop.database.factory.DatabaseFactoryImpl;
import cn.encmys.ykdz.forest.hyphashop.hook.ItemsAdderHook;
import cn.encmys.ykdz.forest.hyphashop.hook.MMOItemsHook;
import cn.encmys.ykdz.forest.hyphashop.hook.MythicMobsHook;
import cn.encmys.ykdz.forest.hyphashop.hook.PlaceholderAPIHook;
import cn.encmys.ykdz.forest.hyphashop.listener.ItemsAdderListener;
import cn.encmys.ykdz.forest.hyphashop.listener.PlayerListener;
import cn.encmys.ykdz.forest.hyphashop.product.factory.ProductFactoryImpl;
import cn.encmys.ykdz.forest.hyphashop.profile.factory.ProfileFactoryImpl;
import cn.encmys.ykdz.forest.hyphashop.rarity.factory.RarityFactoryImpl;
import cn.encmys.ykdz.forest.hyphashop.scheduler.ConnTasksImpl;
import cn.encmys.ykdz.forest.hyphashop.shop.factory.ShopFactoryImpl;
import cn.encmys.ykdz.forest.hyphashop.utils.LogUtils;
import cn.encmys.ykdz.forest.hyphautils.HyphaPluginUtils;
import net.milkbowl.vault.economy.Economy;
import org.bstats.bukkit.Metrics;
import org.bukkit.Bukkit;
import org.bukkit.plugin.RegisteredServiceProvider;

import java.util.concurrent.CompletableFuture;

public final class HyphaShopImpl extends HyphaShop {
    @Override
    public void reload() {
        HyphaShop.PROFILE_FACTORY.unload();
        HyphaShop.SHOP_FACTORY.unload();
        HyphaShop.PRODUCT_FACTORY.unload();

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

        HyphaShop.DATABASE_FACTORY = new DatabaseFactoryImpl();
        HyphaShop.PROFILE_FACTORY = new ProfileFactoryImpl();
        HyphaShop.RARITY_FACTORY = new RarityFactoryImpl();
        HyphaShop.PRODUCT_FACTORY = new ProductFactoryImpl();
        HyphaShop.SHOP_FACTORY = new ShopFactoryImpl();
    }

    @Override
    public void onLoad() {
        INSTANCE = this;
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

        if (!setupEconomy()) {
            LogUtils.error("Plugin disabled due to no Vault dependency found!");
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

        HyphaShop.DATABASE_FACTORY = new DatabaseFactoryImpl();
        HyphaShopImpl.PROFILE_FACTORY = new ProfileFactoryImpl();
        HyphaShopImpl.RARITY_FACTORY = new RarityFactoryImpl();
        HyphaShopImpl.PRODUCT_FACTORY = new ProductFactoryImpl();
        HyphaShopImpl.SHOP_FACTORY = new ShopFactoryImpl();

        HyphaShopImpl.CONN_TASKS = new ConnTasksImpl();

        setupBStats();
    }

    @Override
    public void onDisable() {
        PROFILE_FACTORY.unload();
        SHOP_FACTORY.unload();
        PRODUCT_FACTORY.unload();
    }

    @Override
    public boolean setupEconomy() {
        if (!HyphaPluginUtils.isExist("Vault")) {
            return false;
        }
        RegisteredServiceProvider<Economy> rsp = getServer().getServicesManager().getRegistration(Economy.class);
        if (rsp == null) {
            return false;
        }
        HyphaShopImpl.ECONOMY = rsp.getProvider();
        return true;
    }

    @Override
    public void setupBStats() {
        int pluginId = 21305;
        HyphaShopImpl.METRICS = new Metrics(this, pluginId);
    }
}
