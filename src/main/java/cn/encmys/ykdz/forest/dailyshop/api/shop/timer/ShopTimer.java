package cn.encmys.ykdz.forest.dailyshop.api.shop.timer;

public interface ShopTimer {
    boolean isTimeToRestock();
    String getFormattedTimeUntilRestock();
}
