package cn.encmys.ykdz.forest.hyphashop.price;

import cn.encmys.ykdz.forest.hyphascript.context.Context;
import cn.encmys.ykdz.forest.hyphashop.api.price.Price;
import cn.encmys.ykdz.forest.hyphashop.api.price.enums.PriceMode;
import cn.encmys.ykdz.forest.hyphashop.utils.ScriptUtils;
import org.bukkit.configuration.ConfigurationSection;
import org.jetbrains.annotations.NotNull;

public class PriceImpl extends Price {

    public PriceImpl(@NotNull ConfigurationSection priceSection) {
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
        }
        // 只有 context 没有 formula 是不合法的
        // 默认配置中价格块的 formula 会被继承到商品的价格块里
        // 以保证合法的配置中不会出现仅 context 无 formula 的情况
        // 所以只要出现即不合法
        else if (priceSection.contains("formula")) {
            priceMode = PriceMode.FORMULA;
            String context = priceSection.getString("context");
            scriptContext = ScriptUtils.extractContext(context == null ? "" : context);
            formula = priceSection.getString("formula");
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
    public boolean isRound() {
        return round;
    }

    @Override
    public Context getScriptContext() {
        return scriptContext;
    }
}