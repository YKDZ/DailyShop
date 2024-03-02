package cn.encmys.ykdz.forest.dailyshop.price;

import org.bukkit.configuration.ConfigurationSection;

import java.util.Random;

public class PriceProvider {
    private final static Random random = new Random();
    private final ConfigurationSection buySection;
    private final ConfigurationSection sellSection;

    public PriceProvider(ConfigurationSection buySection, ConfigurationSection sellSection) {
        this.buySection = buySection;
        this.sellSection = sellSection;
    }

    public double getBuyPrice() {
        return getPrice(buySection);
    }

    public double getSellPrice() {
        return getPrice(sellSection);
    }

    private double getPrice(ConfigurationSection configSection) {
        if(configSection.contains("fixed")) {
            return configSection.getDouble("fixed");
        } else if(configSection.contains("mean") && configSection.contains("dev")) {
            double result = random.nextGaussian() * Math.sqrt(configSection.getDouble("dev")) + configSection.getDouble("mean");
            if(configSection.getBoolean("round", false)) {
                return Math.round(result);
            }
            return result;
        }
        throw new IllegalArgumentException("Illegal price setting");
    }
}
