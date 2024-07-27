package cn.encmys.ykdz.forest.dailyshop.listener;

import cn.encmys.ykdz.forest.dailyshop.api.DailyShop;
import cn.encmys.ykdz.forest.dailyshop.api.profile.Profile;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;

public class PlayerListener implements Listener {
    @EventHandler
    public void initProfile(PlayerJoinEvent event) {
        Profile profile = DailyShop.PROFILE_FACTORY.getProfile(event.getPlayer());
        if (profile == null) {
            DailyShop.PROFILE_FACTORY.buildProfile(event.getPlayer());
        }
    }
}
