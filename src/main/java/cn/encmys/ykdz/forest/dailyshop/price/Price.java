package cn.encmys.ykdz.forest.dailyshop.price;

import cn.encmys.ykdz.forest.dailyshop.enums.PriceMode;
import org.bukkit.configuration.ConfigurationSection;

import java.util.Random;

public class Price {
    private static final Random random = new Random();
    private PriceMode priceMode;
    // Fixed Mode
    private double fixed;
    // Gaussian Mode
    private double mean = 0d;
    private double dev = 0d;
    // Min max Mode
    private double min = 0d;
    private double max = 0d;
    // Whether round the price
    private boolean round = false;

    public Price(ConfigurationSection priceSection) {
        buildPrice(priceSection);
    }

    public Price(double fixed) {
        this.fixed = fixed;
        this.priceMode = PriceMode.FIXED;
    }

    public Price(double mean, double dev, boolean round) {
        this.mean = mean;
        this.dev = dev;
        this.round = round;
        priceMode = PriceMode.GAUSSIAN;
    }

    public Price(double min, double max) {
        this.min = min;
        this.max = max;
        priceMode = PriceMode.MINMAX;
    }

    private void buildPrice(ConfigurationSection priceSection) {
        if (priceSection.contains("fixed")) {
            this.fixed = priceSection.getDouble("fixed");
            priceMode = PriceMode.FIXED;
        } else if (priceSection.contains("mean") && priceSection.contains("dev")) {
            this.mean = priceSection.getDouble("mean");
            this.dev = priceSection.getDouble("dev");
            this.round = priceSection.getBoolean("round", false);
            priceMode = PriceMode.GAUSSIAN;
        } else if (priceSection.contains("min") && priceSection.contains("max")) {
            this.min = priceSection.getDouble("min");
            this.max = priceSection.getDouble("max");
            this.round = priceSection.getBoolean("round", false);
            priceMode = PriceMode.MINMAX;
        } else if (priceSection.getBoolean("bundle-auto-new")) {
            priceMode = PriceMode.BUNDLE_AUTO_NEW;
        } else if (priceSection.getBoolean("bundle-auto-reuse")) {
            priceMode = PriceMode.BUNDLE_AUTO_REUSE;
        } else {
            throw new IllegalArgumentException("Invalid price setting.");
        }
    }

    public double getNewPrice() {
        switch (priceMode) {
            case GAUSSIAN -> {
                return round ? Math.round(mean + dev * random.nextGaussian()) : mean + dev * random.nextGaussian();
            }
            case FIXED -> {
                return fixed;
            }
            case MINMAX -> {
                return round ? Math.round(min + (max - min) * random.nextDouble()) : min + (max - min) * random.nextDouble();
            }
        }
        return -1;
    }

    public PriceMode getPriceMode() {
        return priceMode;
    }
}
