package cn.encmys.ykdz.forest.dailyshop;

import org.bukkit.plugin.java.JavaPlugin;

public final class DailyShop extends JavaPlugin {
    private static DailyShop instance;

    @Override
    public void onEnable() {
        instance = this;
    }

    @Override
    public void onDisable() {
        // Plugin shutdown logic
    }

    public static DailyShop getInstance() {
        return instance;
    }
}
