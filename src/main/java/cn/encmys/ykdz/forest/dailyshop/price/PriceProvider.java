package cn.encmys.ykdz.forest.dailyshop.price;

import cn.encmys.ykdz.forest.dailyshop.enums.PriceMode;
import org.bukkit.configuration.ConfigurationSection;

import javax.annotation.Nullable;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;

public class PriceProvider {
    private static final Random random = new Random();
    private PriceMode priceMode;
    private final Map<String, Double> prices = new HashMap<>();
    //
    private double fixed;
    //
    private double mean = 0d;
    private double dev = 0d;
    private boolean round = false;
    //
    private double min = 0d;
    private double max = 0d;

    public PriceProvider(ConfigurationSection priceSection) {
        buildPrice(priceSection);
    }

    public PriceProvider(double fixed) {
        this.fixed = fixed;
        this.prices.put("INTERNAL_SHOP", fixed);
        this.priceMode = PriceMode.FIXED;
    }

    public PriceProvider(double mean, double dev, boolean round) {
        this.mean = mean;
        this.dev = dev;
        this.round = round;
        double gaussian = random.nextGaussian();
        this.prices.put("INTERNAL_SHOP", round ?
                Math.round(mean + dev * gaussian) :
                mean + dev * gaussian);
        priceMode = PriceMode.GAUSSIAN;
    }

    private void buildPrice(ConfigurationSection priceSection) {
        if(priceSection.contains("fixed")) {
            this.fixed = priceSection.getDouble("fixed");
            priceMode = PriceMode.FIXED;
            update("INTERNAL_SHOP");
        } else if(priceSection.contains("mean") && priceSection.contains("dev")) {
            this.mean = priceSection.getDouble("mean");
            this.dev = priceSection.getDouble("dev");
            this.round = priceSection.getBoolean("round", false);
            priceMode = PriceMode.GAUSSIAN;
            update("INTERNAL_SHOP");
        } else {
            throw new IllegalArgumentException("Invalid price setting.");
        }
    }

    public double getPrice(@Nullable String shopId) {
        return shopId == null ? prices.get("INTERNAL_SHOP") : prices.get(shopId);
    }

    public PriceMode getPriceMode() {
        return priceMode;
    }

    public void update(String shopId) {
        switch (priceMode) {
            case GAUSSIAN -> prices.put(shopId,
                    round ? Math.round(mean + dev * random.nextGaussian()) : mean + dev * random.nextGaussian());
            case FIXED -> prices.put(shopId, fixed);
        }
    }
}
