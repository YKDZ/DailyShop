package cn.encmys.ykdz.forest.dailyshop.scheduler;

import cn.encmys.ykdz.forest.dailyshop.DailyShopImpl;
import io.papermc.paper.threadedregions.scheduler.AsyncScheduler;
import io.papermc.paper.threadedregions.scheduler.GlobalRegionScheduler;
import io.papermc.paper.threadedregions.scheduler.ScheduledTask;
import org.jetbrains.annotations.NotNull;

import java.util.concurrent.TimeUnit;
import java.util.function.Consumer;

public class Scheduler {
    private static final GlobalRegionScheduler globalScheduler = DailyShopImpl.INSTANCE.getServer().getGlobalRegionScheduler();
    private static final AsyncScheduler asyncScheduler = DailyShopImpl.INSTANCE.getServer().getAsyncScheduler();

    @NotNull
    public static ScheduledTask runTask(Consumer<ScheduledTask> task) {
        return globalScheduler.run(DailyShopImpl.INSTANCE, task);
    }

    @NotNull
    public static ScheduledTask runTaskAtFixedRate(Consumer<ScheduledTask> task, long delay, long period) {
        // delay 不能小于等于 0，故 + 1
        return globalScheduler.runAtFixedRate(DailyShopImpl.INSTANCE, task, delay + 1, period);
    }

    @NotNull
    public static ScheduledTask runAsyncTaskAtFixedRate(Consumer<ScheduledTask> task, long delay, long period) {
        // delay 不能小于等于 0，故 + 1
        return asyncScheduler.runAtFixedRate(DailyShopImpl.INSTANCE, task, delay / 20, period, TimeUnit.SECONDS);
    }

    @NotNull
    public static ScheduledTask runAsyncTask(Consumer<ScheduledTask> task) {
        return asyncScheduler.runNow(DailyShopImpl.INSTANCE, task);
    }
}
