package cn.encmys.ykdz.forest.dailyshop.price;

import cn.encmys.ykdz.forest.dailyshop.enums.PriceMode;
import org.bukkit.configuration.ConfigurationSection;

import java.util.Random;

public class PriceProvider {
    private static final Random random = new Random();
    private double gaussian;
    private PriceMode priceMode;
    private double price;
    private double mean = 0d;
    private double dev = 0d;
    private boolean round = false;

    public PriceProvider(ConfigurationSection priceSection) {
        buildPrice(priceSection);
    }

    public PriceProvider(double fixed) {
        this.price = fixed;
        this.priceMode = PriceMode.FIXED;
    }

    public PriceProvider(double mean, double dev, boolean round) {
        gaussian = random.nextGaussian();
        price = round ?
                Math.round(mean + dev * gaussian) :
                mean + dev * gaussian;
        priceMode = PriceMode.GAUSSIAN;
    }

    private void buildPrice(ConfigurationSection priceSection) {
        if(priceSection.contains("fixed")) {
            price = priceSection.getDouble("fixed");
            priceMode = PriceMode.FIXED;
        } else if(priceSection.contains("mean") && priceSection.contains("dev")) {
            gaussian = random.nextGaussian();
            price = priceSection.getBoolean("round", false) ?
                            Math.round(priceSection.getDouble("mean") + priceSection.getDouble("dev") * gaussian) :
                            priceSection.getDouble("mean") + priceSection.getDouble("dev") * gaussian;
            priceMode = PriceMode.GAUSSIAN;
        } else {
            throw new IllegalArgumentException("Invalid price setting.");
        }
    }

    public double getPrice() {
        return price;
    }

    public PriceMode getPriceMode() {
        return priceMode;
    }

    public void update() {
        switch (priceMode) {
            case GAUSSIAN -> {
                gaussian = random.nextGaussian();
                price = round ?
                        Math.round(mean + dev * gaussian) :
                        mean + dev * gaussian;
            }
        }
    }
}
