package cn.encmys.ykdz.forest.dailyshop.gui.icon;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.scheduler.BukkitTask;
import org.jetbrains.annotations.NotNull;
import xyz.xenondevs.invui.InvUI;
import xyz.xenondevs.invui.item.ItemProvider;
import xyz.xenondevs.invui.item.impl.AbstractItem;

public abstract class NormalIcon extends AbstractItem {
    private BukkitTask task;

    public void startUpdater(long period) {
        if (task != null) task.cancel();
        task = Bukkit.getScheduler().runTaskTimer(InvUI.getInstance().getPlugin(), this::notifyWindows, 0, period);
    }

    public void cancelUpdater() {
        task.cancel();
        task = null;
    }

    @Override
    public abstract ItemProvider getItemProvider();

    @Override
    public abstract void handleClick(@NotNull ClickType clickType, @NotNull Player player, @NotNull InventoryClickEvent event);
}
