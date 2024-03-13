package cn.encmys.ykdz.forest.dailyshop.price;

import com.google.gson.annotations.Expose;

public class PricePair {
    @Expose
    private double buy;
    @Expose
    private double sell;

    public PricePair(double buy, double sell) {
        this.buy = buy;
        this.sell = sell;
    }

    public double getBuy() {
        return buy;
    }

    public void setBuy(double buy) {
        this.buy = buy;
    }

    public double getSell() {
        return sell;
    }

    public void setSell(double sell) {
        this.sell = sell;
    }
}
