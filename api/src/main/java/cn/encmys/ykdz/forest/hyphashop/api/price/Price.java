package cn.encmys.ykdz.forest.hyphashop.api.price;

import cn.encmys.ykdz.forest.hyphascript.context.Context;
import cn.encmys.ykdz.forest.hyphashop.api.price.enums.PriceMode;
import org.bukkit.configuration.ConfigurationSection;
import org.jetbrains.annotations.NotNull;

import java.util.Random;

public abstract class Price {
    protected static final Random random = new Random();

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
    protected Context scriptContext;
    protected String formula;

    protected abstract void buildPrice(@NotNull ConfigurationSection priceSection);

    public abstract double getNewPrice();

    public abstract PriceMode getPriceMode();

    public abstract String getFormula();

    public abstract boolean isRound();

    public abstract Context getScriptContext();
}
