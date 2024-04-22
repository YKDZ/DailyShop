package cn.encmys.ykdz.forest.dailyshop.shop.logger.log;

import cn.encmys.ykdz.forest.dailyshop.shop.logger.enums.TransitionLogType;
import com.google.gson.annotations.Expose;

import java.util.Date;
import java.util.UUID;

public class TransitionLog {
    @Expose
    private UUID customer;
    @Expose
    private TransitionLogType type;
    @Expose
    private Date transitionTime;
    @Expose
    private double price;
    @Expose
    private String productName;
    @Expose
    private double productMaterial;

    private TransitionLog() {}

    public TransitionLog buyLog(UUID customer) {
        return new TransitionLog()
                .setCustomer(customer)
                .setType(TransitionLogType.BUY)
                .setTransitionTime(new Date());
    }

    public TransitionLog sellLog(UUID customer) {
        return new TransitionLog()
                .setCustomer(customer)
                .setType(TransitionLogType.SELL)
                .setTransitionTime(new Date());
    }

    public TransitionLog setCustomer(UUID customer) {
        this.customer = customer;
        return this;
    }

    public TransitionLog setType(TransitionLogType type) {
        this.type = type;
        return this;
    }

    public TransitionLog setTransitionTime(Date transitionTime) {
        this.transitionTime = transitionTime;
        return this;
    }

    public TransitionLog setPrice(double price) {
        this.price = price;
        return this;
    }

    public TransitionLog setProductName(String productName) {
        this.productName = productName;
        return this;
    }

    public TransitionLog setProductMaterial(double productMaterial) {
        this.productMaterial = productMaterial;
        return this;
    }
}
