package cn.encmys.ykdz.forest.dailyshop.api.item.decorator;

import cn.encmys.ykdz.forest.dailyshop.api.item.BaseItem;
import org.bukkit.event.inventory.ClickType;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public abstract class BaseItemDecorator {
    protected BaseItem baseItem;
    // 通用属性
    protected String name;
    protected long period;
    protected List<String> lore;
    protected int amount;
    protected List<String> itemFlags;
    protected Integer customModelData;
    protected List<String> patternsData = new ArrayList<>();
    protected List<String> fireworkEffectData = new ArrayList<>();
    // 普通图标属性
    protected Map<ClickType, List<String>> commands = new HashMap<>();
    protected ClickType featuresScroll;
    protected int featuresScrollAmount;
    protected ClickType featuresSettleCart;
    protected ClickType featuresBackToShop;
    protected ClickType featuresOpenCart;
    protected ClickType featuresSwitchShoppingMode;
    protected ClickType featuresSwitchCartMode;
    protected ClickType featuresCleanCart;
    protected ClickType featuresClearCart;

    public abstract ClickType getFeaturesSettleCart();

    public abstract BaseItemDecorator setFeaturesSettleCart(ClickType featuresSettleCart);

    public abstract BaseItem getBaseItem();

    public abstract BaseItemDecorator setName(String name);

    public abstract BaseItemDecorator setLore(List<String> lore);

    public abstract int getAmount();

    public abstract String getName();

    public abstract List<String> getItemFlags();

    public abstract BaseItemDecorator setItemFlags(List<String> itemFlags);

    public abstract BaseItemDecorator setAmount(int amount);

    public abstract List<String> getLore();

    public abstract Integer getCustomModelData();

    public abstract BaseItemDecorator setCustomModelData(Integer customModelData);

    public abstract List<String> getPatternsData();

    public abstract BaseItemDecorator setBannerPatterns(List<String> patternsData);

    public abstract Map<ClickType, List<String>> getCommands();

    public abstract BaseItemDecorator setCommands(Map<ClickType, List<String>> commands);

    public abstract ClickType getFeaturesScroll();

    public abstract BaseItemDecorator setFeaturesScroll(ClickType featuresScroll);

    public abstract long getPeriod();

    public abstract BaseItemDecorator setUpdatePeriod(long period);

    public abstract List<String> getFireworkEffectData();

    public abstract BaseItemDecorator setFireworkEffectData(List<String> fireworkEffectData);

    public abstract ClickType getFeaturesBackToShop();

    public abstract BaseItemDecorator setFeaturesBackToShop(ClickType featuresBackToShop);

    public abstract ClickType getFeaturesOpenCart();

    public abstract BaseItemDecorator setFeaturesOpenCart(ClickType featuresOpenCart);

    public abstract int getFeaturesScrollAmount();

    public abstract BaseItemDecorator setFeaturesScrollAmount(int featuresScrollAmount);

    public abstract ClickType getFeaturesSwitchShoppingMode();

    public abstract BaseItemDecorator setFeaturesSwitchShoppingMode(ClickType featuresSwitchShoppingMode);

    public abstract ClickType getFeaturesSwitchCartMode();

    public abstract BaseItemDecorator setFeaturesSwitchCartMode(ClickType featuresChangeCartMode);

    public abstract ClickType getFeaturesCleanCart();

    public abstract BaseItemDecorator setFeaturesCleanCart(ClickType featuresCleanCart);

    public abstract ClickType getFeaturesClearCart();

    public abstract BaseItemDecorator setFeaturesClearCart(ClickType featuresClearCart);
}
