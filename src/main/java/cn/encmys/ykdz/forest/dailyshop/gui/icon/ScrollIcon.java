package cn.encmys.ykdz.forest.dailyshop.gui.icon;

import cn.encmys.ykdz.forest.dailyshop.api.gui.icon.Icon;
import org.bukkit.Bukkit;
import org.bukkit.scheduler.BukkitTask;
import xyz.xenondevs.invui.InvUI;
import xyz.xenondevs.invui.gui.ScrollGui;
import xyz.xenondevs.invui.item.ItemProvider;
import xyz.xenondevs.invui.item.impl.controlitem.ScrollItem;

public abstract class ScrollIcon extends ScrollItem implements Icon {
    private BukkitTask task;

    public void startUpdater(long period) {
        if (task != null) task.cancel();
        task = Bukkit.getScheduler().runTaskTimer(InvUI.getInstance().getPlugin(), this::notifyWindows, 0, period);
    }

    public void cancelUpdater() {
        task.cancel();
        task = null;
    }

    public ScrollIcon(int scroll) {
        super(scroll);
    }

    @Override
    public abstract ItemProvider getItemProvider(ScrollGui<?> gui);
}
