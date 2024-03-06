package cn.encmys.ykdz.forest.dailyshop.price;

import org.bukkit.configuration.ConfigurationSection;

import java.util.Random;

public class PriceProvider {
    private final static double gaussian = new Random().nextGaussian();
    private final double buyPrice;
    private final double sellPrice;

    public PriceProvider(ConfigurationSection buySection, ConfigurationSection sellSection) {
        this.buyPrice = buildPrice(buySection);
        this.sellPrice = buildPrice(sellSection);
    }

    public double getBuyPrice() {
        return buyPrice;
    }

    public double getSellPrice() {
        return sellPrice;
    }

    private double buildPrice(ConfigurationSection configSection) {
        if (configSection == null) {
            return -1d;
        }

        if (configSection.contains("fixed")) {
            return configSection.getDouble("fixed");
        } else if (configSection.contains("mean") && configSection.contains("dev")) {
            double result = gaussian * Math.sqrt(configSection.getDouble("dev")) + configSection.getDouble("mean");
            if (configSection.getBoolean("round", false)) {
                return Math.round(result);
            }
            return result;
        }
        throw new IllegalArgumentException("Illegal price setting");
    }
}
