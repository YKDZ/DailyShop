package cn.encmys.ykdz.forest.dailyshop.price;

import cn.encmys.ykdz.forest.dailyshop.api.price.Price;
import cn.encmys.ykdz.forest.dailyshop.api.price.enums.PriceMode;
import org.bukkit.configuration.ConfigurationSection;
import org.jetbrains.annotations.NotNull;

import java.util.Map;

public class PriceImpl extends Price {

    public PriceImpl(ConfigurationSection priceSection) {
        buildPrice(priceSection);
    }

    @Override
    protected void buildPrice(@NotNull ConfigurationSection priceSection) {
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
        } else if (priceSection.contains("formula")) {
            priceMode = PriceMode.FORMULA;
            formula = priceSection.getString("formula");
            for (String entry : priceSection.getStringList("vars")) {
                String[] split = entry.split(":");
                if (split.length != 2) {
                    throw new IllegalArgumentException("Invalid variable '" + entry + "'.");
                }
                formulaVars.put(split[0], split[1]);
            }
        } else if (priceSection.getBoolean("disable")) {
            priceMode = PriceMode.DISABLE;
        } else {
            throw new IllegalArgumentException("Invalid price setting.");
        }
    }

    @Override
    public double getNewPrice() {
        double price = 0;
        switch (priceMode) {
            case GAUSSIAN ->
                    price = round ? Math.round(mean + dev * random.nextGaussian()) : mean + dev * random.nextGaussian();
            case FIXED -> price = fixed;
            case MINMAX ->
                    price = round ? Math.round(min + (max - min) * random.nextDouble()) : min + (max - min) * random.nextDouble();
        }
        if (price <= 0) {
            return -1;
        } else {
            return price;
        }
    }

    @Override
    public PriceMode getPriceMode() {
        return priceMode;
    }

    @Override
    public String getFormula() {
        return formula;
    }

    @Override
    public Map<String, String> getFormulaVars() {
        return formulaVars;
    }

    @Override
    public boolean isRound() {
        return round;
    }
}