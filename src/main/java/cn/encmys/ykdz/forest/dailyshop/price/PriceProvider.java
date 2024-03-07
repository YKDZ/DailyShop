package cn.encmys.ykdz.forest.dailyshop.price;

import cn.encmys.ykdz.forest.dailyshop.enums.PriceMode;
import org.bukkit.configuration.ConfigurationSection;

import java.util.HashMap;
import java.util.Map;
import java.util.Random;

public class PriceProvider {
    private static final Random random = new Random();
    private PriceMode priceMode;
    private Map<String, Double> prices = new HashMap<>();
    private double mean = 0d;
    private double dev = 0d;
    private boolean round = false;
    private double min = 0d;
    private double max = 0d;

    public PriceProvider(ConfigurationSection priceSection) {
        buildPrice(priceSection);
    }

    public PriceProvider(double fixed) {
        this.prices.put("defaultPrice", fixed);
        this.priceMode = PriceMode.FIXED;
    }

    public PriceProvider(double mean, double dev, boolean round) {
        double gaussian = random.nextGaussian();
        this.prices.put("defaultPrice", round ?
                Math.round(mean + dev * gaussian) :
                mean + dev * gaussian);
        priceMode = PriceMode.GAUSSIAN;
    }

    private void buildPrice(ConfigurationSection priceSection) {
        if(priceSection.contains("fixed")) {
            prices.put("defaultPrice", priceSection.getDouble("fixed"));
            priceMode = PriceMode.FIXED;
        } else if(priceSection.contains("mean") && priceSection.contains("dev")) {
            double gaussian = random.nextGaussian();
            prices.put("defaultPrice", priceSection.getBoolean("round", false) ?
                            Math.round(priceSection.getDouble("mean") + priceSection.getDouble("dev") * gaussian) :
                            priceSection.getDouble("mean") + priceSection.getDouble("dev") * gaussian);
            priceMode = PriceMode.GAUSSIAN;
        } else {
            throw new IllegalArgumentException("Invalid price setting.");
        }
    }

    public double getPrice() {
        return prices.get("defaultPrice");
    }

    public double getPrice(String shopId) {
        return prices.get(shopId);
    }

    public PriceMode getPriceMode() {
        return priceMode;
    }

    public void update(String shopId) {
        switch (priceMode) {
            case GAUSSIAN -> prices.put(shopId,
                    round ? Math.round(mean + dev * random.nextGaussian()) : mean + dev * random.nextGaussian());
        }
    }
}
