package cn.encmys.ykdz.forest.dailyshop.listener;

import cn.encmys.ykdz.forest.dailyshop.api.DailyShop;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;

public class PlayerListener implements Listener {
    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {
        Player player = event.getPlayer();
        DailyShop.INSTANCE.getServer().getScheduler().runTaskAsynchronously(
                DailyShop.INSTANCE, () -> DailyShop.PROFILE_FACTORY.buildProfile(player)
        );
    }

    @EventHandler
    public void onPlayerQuit(PlayerQuitEvent event) {
        Player player = event.getPlayer();
        DailyShop.INSTANCE.getServer().getScheduler().runTaskAsynchronously(
                DailyShop.INSTANCE, () -> {
                    DailyShop.PROFILE_FACTORY.save(player.getUniqueId());
                    DailyShop.PROFILE_FACTORY.removeProfile(player);
                }
        );
    }
}
