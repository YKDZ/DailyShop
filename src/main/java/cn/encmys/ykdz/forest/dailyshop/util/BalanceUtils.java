package cn.encmys.ykdz.forest.dailyshop.util;

import cn.encmys.ykdz.forest.dailyshop.DailyShop;
import net.milkbowl.vault.economy.Economy;
import net.milkbowl.vault.economy.EconomyResponse;
import org.bukkit.entity.Player;

public class BalanceUtils {
    private static final Economy economy = DailyShop.getEconomy();

    public static EconomyResponse addBalance(Player player, double amount) {
        return economy.depositPlayer(player, amount);
    }

    public static EconomyResponse removeBalance(Player player, double amount) {
        return economy.withdrawPlayer(player, amount);
    }

    public static double checkBalance(Player player) {
        return economy.getBalance(player);
    }
}
