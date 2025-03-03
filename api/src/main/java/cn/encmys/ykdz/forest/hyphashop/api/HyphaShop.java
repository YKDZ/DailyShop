package cn.encmys.ykdz.forest.hyphashop.api;

import cn.encmys.ykdz.forest.hyphashop.api.database.factory.DatabaseFactory;
import cn.encmys.ykdz.forest.hyphashop.api.product.factory.ProductFactory;
import cn.encmys.ykdz.forest.hyphashop.api.profile.factory.ProfileFactory;
import cn.encmys.ykdz.forest.hyphashop.api.rarity.factory.RarityFactory;
import cn.encmys.ykdz.forest.hyphashop.api.scheduler.ConnTasks;
import cn.encmys.ykdz.forest.hyphashop.api.shop.factory.ShopFactory;
import net.milkbowl.vault.economy.Economy;
import org.bstats.bukkit.Metrics;
import org.bukkit.plugin.java.JavaPlugin;

public abstract class HyphaShop extends JavaPlugin {
    public static HyphaShop INSTANCE;
    public static ProfileFactory PROFILE_FACTORY;
    public static RarityFactory RARITY_FACTORY;
    public static ProductFactory PRODUCT_FACTORY;
    public static ShopFactory SHOP_FACTORY;
    public static ConnTasks CONN_TASKS;
    public static DatabaseFactory DATABASE_FACTORY;
    public static Economy ECONOMY;
    public static Metrics METRICS;

    public abstract void reload();

    public abstract void init();

    public abstract boolean setupEconomy();

    public abstract void setupBStats();
}
