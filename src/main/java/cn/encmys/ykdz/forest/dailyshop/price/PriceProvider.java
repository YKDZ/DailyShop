package cn.encmys.ykdz.forest.dailyshop.price;

import org.bukkit.configuration.ConfigurationSection;

public class PriceProvider {
    private final ConfigurationSection buySection;
    private final ConfigurationSection sellSection;

    public PriceProvider(ConfigurationSection buySection, ConfigurationSection sellSection) {
        this.buySection = buySection;
        this.sellSection = sellSection;
    }

    public double getBuyPrice() {
        if(buySection.contains("fixed")) {
            return buySection.getDouble("fixed");
        } else if
    }

    public double getSellPrice() {

    }
}
