package cn.encmys.ykdz.forest.dailyshop.price;

import org.bukkit.configuration.ConfigurationSection;

import java.util.Random;

public class PriceProvider {
    private static Random random;
    private double gaussian;
    private double buyPrice;
    private double sellPrice;

    public PriceProvider(ConfigurationSection buySection, ConfigurationSection sellSection) {
        this.gaussian = random.nextGaussian();
        this.buyPrice = buildPrice(buySection);
        this.sellPrice = buildPrice(sellSection);
    }

    /**
     * Build a Fixed PriceProvider quickly. Set price value to -1 means disable it.
     * @param buyPrice Fixed buy price
     * @param sellPrice Fixed sell price
     */
    public PriceProvider(double buyPrice, double sellPrice) {
        this.buyPrice = buyPrice;
        this.sellPrice = sellPrice;
    }

    /**
     * @param buyMean Gaussian mean of buy price
     * @param buyDev Gaussian dev of buy price
     * @param buyRound Whether round the buy price
     * @param sellMean Gaussian mean of sell price
     * @param sellDev Gaussian dev of sell price
     * @param sellRound Whether round the sell price
     */
    public PriceProvider(double buyMean, double buyDev, boolean buyRound, double sellMean, double sellDev, boolean sellRound) {
        this.buyPrice = buildPrice(buyMean, buyDev, buyRound);
        this.sellPrice = buildPrice(sellMean, sellDev, sellRound);
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

    private double buildPrice(double mean, double dev, boolean round) {
        double result = gaussian * Math.sqrt(dev) + mean;
        if (round) {
            return Math.round(result);
        }
        return result;
    }

    public void updatePrice() {
        gaussian = random.nextGaussian();
    }
}
