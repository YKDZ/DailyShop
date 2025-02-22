package cn.encmys.ykdz.forest.hyphashop.utils;

import cn.encmys.ykdz.forest.hyphashop.api.HyphaShop;
import net.milkbowl.vault.economy.EconomyResponse;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

public class BalanceUtils {
    public static EconomyResponse addBalance(@NotNull Player player, double amount) {
        return HyphaShop.ECONOMY.depositPlayer(player, amount);
    }

    public static EconomyResponse removeBalance(@NotNull Player player, double amount) {
        return HyphaShop.ECONOMY.withdrawPlayer(player, amount);
    }

    public static double checkBalance(@NotNull Player player) {
        return HyphaShop.ECONOMY.getBalance(player);
    }
}
