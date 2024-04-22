package cn.encmys.ykdz.forest.dailyshop.shop.logger;

import cn.encmys.ykdz.forest.dailyshop.shop.Shop;
import cn.encmys.ykdz.forest.dailyshop.shop.logger.log.TransitionLog;
import com.google.gson.annotations.Expose;

import java.util.HashSet;
import java.util.Set;

public class ShopLogger {
    private final Shop shop;
    @Expose
    private final Set<TransitionLog> logs = new HashSet<>();

    public ShopLogger(Shop shop) {
        this.shop = shop;
    }

    public void logBuy() {

    }

    public void logSell() {

    }
}
