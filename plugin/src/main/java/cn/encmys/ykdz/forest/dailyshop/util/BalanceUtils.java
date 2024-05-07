package cn.encmys.ykdz.forest.dailyshop.util;

import cn.encmys.ykdz.forest.dailyshop.api.DailyShop;
import net.milkbowl.vault.economy.EconomyResponse;
import org.bukkit.entity.Player;

public class BalanceUtils {
    public static EconomyResponse addBalance(Player player, double amount) {
        return DailyShop.ECONOMY.depositPlayer(player, amount);
    }

    public static EconomyResponse removeBalance(Player player, double amount) {
        return DailyShop.ECONOMY.withdrawPlayer(player, amount);
    }

    public static double checkBalance(Player player) {
        return DailyShop.ECONOMY.getBalance(player);
    }
}
