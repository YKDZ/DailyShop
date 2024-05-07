package cn.encmys.ykdz.forest.dailyshop.api.scheduler;

public interface Scheduler {
    void runRestockTimer();
    void runDataSaver();
}
