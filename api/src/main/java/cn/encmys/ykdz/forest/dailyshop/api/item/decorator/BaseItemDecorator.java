package cn.encmys.ykdz.forest.dailyshop.api.item.decorator;

import cn.encmys.ykdz.forest.dailyshop.api.item.BaseItem;
import org.bukkit.DyeColor;
import org.bukkit.FireworkEffect;
import org.bukkit.block.banner.PatternType;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.inventory.ItemFlag;

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
    protected String amount;
    protected Integer customModelData;
    protected Map<ItemFlag, Boolean> itemFlags = new HashMap<>();
    protected Map<PatternType, DyeColor> bannerPatterns = new HashMap<>();
    protected List<FireworkEffect> fireworkEffects = new ArrayList<>();
    protected Map<Enchantment, Integer> enchantments = new HashMap<>();
    // 普通图标属性
    protected Map<ClickType, List<String>> commands = new HashMap<>();
    protected ClickType featuresScroll;
    protected int featuresScrollAmount;
    protected ClickType featuresPageChange;
    protected int featuresPageChangeAmount;
    protected ClickType featuresSettleCart;
    protected ClickType featuresBackToShop;
    protected ClickType featuresOpenCart;
    protected ClickType featuresSwitchShoppingMode;
    protected ClickType featuresSwitchCartMode;
    protected ClickType featuresCleanCart;
    protected ClickType featuresClearCart;
    protected ClickType featuresLoadMoreLog;
    protected ClickType featuresOpenShop;
    protected String featuresOpenShopTarget;
    protected ClickType featuresOpenOrderHistory;

    public abstract ClickType getFeaturesSettleCart();

    public abstract BaseItemDecorator setFeaturesSettleCart(ClickType featuresSettleCart);

    public abstract BaseItem getBaseItem();

    public abstract BaseItemDecorator setName(String name);

    public abstract BaseItemDecorator setLore(List<String> lore);

    public abstract String getAmount();

    public abstract String getName();

    public abstract BaseItemDecorator setAmount(String amount);

    public abstract List<String> getLore();

    public abstract Integer getCustomModelData();

    public abstract BaseItemDecorator setCustomModelData(Integer customModelData);

    public abstract Map<ItemFlag, Boolean> getItemFlags();

    public abstract BaseItemDecorator setItemFlags(List<String> itemFlagsData);

    public abstract List<FireworkEffect> getFireworkEffects();

    public abstract BaseItemDecorator setFireworkEffects(List<String> fireworkEffectsData);

    public abstract Map<PatternType, DyeColor> getBannerPatterns();

    public abstract BaseItemDecorator setBannerPatterns(List<String> bannerPatternsData);

    public abstract Map<Enchantment, Integer> getEnchantments();

    public abstract BaseItemDecorator setEnchantments(List<String> enchantmentsData);

    public abstract Map<ClickType, List<String>> getCommandsData();

    public abstract BaseItemDecorator setCommandsData(Map<ClickType, List<String>> commandsData);

    public abstract ClickType getFeaturesScroll();

    public abstract BaseItemDecorator setFeaturesScroll(ClickType featuresScroll);

    public abstract long getUpdatePeriod();

    public abstract BaseItemDecorator setUpdatePeriod(long period);

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

    public abstract ClickType getFeaturesPageChange();

    public abstract BaseItemDecorator setFeaturesPageChange(ClickType featuresPageChange);

    public abstract int getFeaturesPageChangeAmount();

    public abstract BaseItemDecorator setFeaturesPageChangeAmount(int featuresPageChangeAmount);

    public abstract ClickType getFeaturesLoadMoreLog();

    public abstract BaseItemDecorator setFeaturesLoadMoreLog(ClickType featuresLoadMoreLog);

    public abstract BaseItemDecorator setFeaturesOpenShop(ClickType featuresOpenShop);

    public abstract ClickType getFeaturesOpenShop();

    public abstract BaseItemDecorator setFeaturesOpenShopTarget(String featuresOpenShopTarget);

    public abstract String getFeaturesOpenShopTarget();

    public abstract BaseItemDecorator setFeaturesOpenOrderHistory(ClickType featuresOpenOrderHistory);

    public abstract ClickType getFeaturesOpenOrderHistory();
}
