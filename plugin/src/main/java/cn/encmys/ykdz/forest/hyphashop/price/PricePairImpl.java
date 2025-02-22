package cn.encmys.ykdz.forest.hyphashop.price;

import cn.encmys.ykdz.forest.hyphashop.api.price.PricePair;
import com.google.gson.annotations.Expose;

public class PricePairImpl implements PricePair {
    @Expose
    private double buy;
    @Expose
    private double sell;

    public PricePairImpl(double buy, double sell) {
        this.buy = buy;
        this.sell = sell;
    }

    @Override
    public double getBuy() {
        return buy;
    }

    @Override
    public void setBuy(double buy) {
        this.buy = buy;
    }

    @Override
    public double getSell() {
        return sell;
    }

    @Override
    public void setSell(double sell) {
        this.sell = sell;
    }
}
