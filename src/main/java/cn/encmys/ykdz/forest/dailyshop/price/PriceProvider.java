package cn.encmys.ykdz.forest.dailyshop.price;

import cn.encmys.ykdz.forest.dailyshop.enums.PriceMode;
import org.bukkit.configuration.ConfigurationSection;

import java.util.HashMap;
import java.util.Map;
import java.util.Random;

public class PriceProvider {
    private static final Random random = new Random();
    private final Map<String, Double> prices = new HashMap<>();
    private PriceMode priceMode;
    // Fixed Mode
    private double fixed;
    // Gaussian Mode
    private double mean = 0d;
    private double dev = 0d;
    // Min max Mode
    private double min = 0d;
    private double max = 0d;
    // Auto Mode (Bundle)
    private boolean auto;
    // Whether round the price
    private boolean round = false;

    public PriceProvider(ConfigurationSection priceSection) {
        buildPrice(priceSection);
    }

    public PriceProvider(double fixed) {
        this.fixed = fixed;
        this.priceMode = PriceMode.FIXED;
        update("INTERNAL_SHOP");
    }

    public PriceProvider(double mean, double dev, boolean round) {
        this.mean = mean;
        this.dev = dev;
        this.round = round;
        priceMode = PriceMode.GAUSSIAN;
        update("INTERNAL_SHOP");
    }

    public PriceProvider(double min, double max) {
        this.min = min;
        this.max = max;
        priceMode = PriceMode.MINMAX;
        update("INTERNAL_SHOP");
    }


    private void buildPrice(ConfigurationSection priceSection) {
        if (priceSection.contains("fixed")) {
            this.fixed = priceSection.getDouble("fixed");
            priceMode = PriceMode.FIXED;
            update("INTERNAL_SHOP");
        } else if (priceSection.contains("mean") && priceSection.contains("dev")) {
            this.mean = priceSection.getDouble("mean");
            this.dev = priceSection.getDouble("dev");
            this.round = priceSection.getBoolean("round", false);
            priceMode = PriceMode.GAUSSIAN;
            update("INTERNAL_SHOP");
        } else if (priceSection.contains("min") && priceSection.contains("max")) {
            this.min = priceSection.getDouble("min");
            this.max = priceSection.getDouble("max");
            this.round = priceSection.getBoolean("round", false);
            priceMode = PriceMode.MINMAX;
            update("INTERNAL_SHOP");
        } else {
            throw new IllegalArgumentException("Invalid price setting.");
        }
    }

    public double getPrice(String shopId) {
        return shopId == null ? prices.get("INTERNAL_SHOP") : prices.get(shopId);
    }

    public void update(String shopId) {
        switch (priceMode) {
            case GAUSSIAN -> prices.put(shopId,
                    round ? Math.round(mean + dev * random.nextGaussian()) : mean + dev * random.nextGaussian());
            case FIXED -> prices.put(shopId, fixed);
            case MINMAX -> prices.put(shopId,
                    round ? Math.round(min + (max - min) * random.nextDouble()) : min + (max - min) * random.nextDouble());
        }
    }
}
