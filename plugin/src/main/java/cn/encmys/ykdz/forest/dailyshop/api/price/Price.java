package cn.encmys.ykdz.forest.dailyshop.api.price;

import cn.encmys.ykdz.forest.dailyshop.api.price.enums.PriceMode;
import org.bukkit.configuration.ConfigurationSection;
import org.jetbrains.annotations.NotNull;

import java.util.HashMap;
import java.util.Map;
import java.util.Random;

public abstract class Price {
    protected static final Random random = new Random();
    protected final Map<String, String> formulaVars = new HashMap<>();
    protected PriceMode priceMode;
    // Fixed Mode
    protected double fixed;
    // Gaussian Mode
    protected double mean = 0d;
    protected double dev = 0d;
    // Min max Mode
    protected double min = 0d;
    protected double max = 0d;
    // Whether round the price
    protected boolean round = false;
    // Formula
    protected String formula;

    protected abstract void buildPrice(@NotNull ConfigurationSection priceSection);

    public abstract double getNewPrice();

    public abstract PriceMode getPriceMode();

    public abstract String getFormula();

    public abstract Map<String, String> getFormulaVars();

    public abstract boolean isRound();
}
