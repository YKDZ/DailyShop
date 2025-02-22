package cn.encmys.ykdz.forest.hyphashop.listener;

import cn.encmys.ykdz.forest.hyphashop.api.HyphaShop;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;

public class PlayerListener implements Listener {
    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {
        Player player = event.getPlayer();
        HyphaShop.INSTANCE.getServer().getScheduler().runTaskAsynchronously(
                HyphaShop.INSTANCE, () -> HyphaShop.PROFILE_FACTORY.buildProfile(player)
        );
    }

    @EventHandler
    public void onPlayerQuit(PlayerQuitEvent event) {
        Player player = event.getPlayer();
        HyphaShop.INSTANCE.getServer().getScheduler().runTaskAsynchronously(
                HyphaShop.INSTANCE, () -> {
                    HyphaShop.PROFILE_FACTORY.save(player.getUniqueId());
                    HyphaShop.PROFILE_FACTORY.removeProfile(player);
                }
        );
    }
}
