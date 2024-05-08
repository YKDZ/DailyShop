package cn.encmys.ykdz.forest.dailyshop.api;

import cn.encmys.ykdz.forest.dailyshop.api.adventure.AdventureManager;
import cn.encmys.ykdz.forest.dailyshop.api.database.Database;
import cn.encmys.ykdz.forest.dailyshop.api.product.factory.ProductFactory;
import cn.encmys.ykdz.forest.dailyshop.api.rarity.factory.RarityFactory;
import cn.encmys.ykdz.forest.dailyshop.api.scheduler.Scheduler;
import cn.encmys.ykdz.forest.dailyshop.api.shop.factory.ShopFactory;
import cn.encmys.ykdz.forest.dailyshop.api.shop.order.builder.ShopOrderBuilder;
import dev.lone.itemsadder.api.Events.ItemsAdderLoadDataEvent;
import me.rubix327.itemslangapi.ItemsLangAPI;
import net.milkbowl.vault.economy.Economy;
import org.bstats.bukkit.Metrics;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.plugin.java.JavaPlugin;

public abstract class DailyShop extends JavaPlugin implements Listener {
    public static DailyShop INSTANCE;
    public static RarityFactory RARITY_FACTORY;
    public static ProductFactory PRODUCT_FACTORY;
    public static ShopFactory SHOP_FACTORY;
    public static ShopOrderBuilder SHOP_ORDER_BUILDER;
    public static Scheduler SCHEDULER;
    public static Database DATABASE;
    public static Economy ECONOMY;
    public static AdventureManager ADVENTURE_MANAGER;
    public static ItemsLangAPI ITEMSLANG_API;
    public static Metrics METRICS;

    public abstract void init();

    public abstract void reload();

    @EventHandler
    protected abstract void waitForItemsAdder(ItemsAdderLoadDataEvent e);

    public abstract boolean setupEconomy();

    public abstract boolean setupItemsLangAPI();

    public abstract boolean setupBStats();
}
