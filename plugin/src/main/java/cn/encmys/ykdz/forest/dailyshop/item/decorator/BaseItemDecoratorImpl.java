package cn.encmys.ykdz.forest.dailyshop.item.decorator;

import cn.encmys.ykdz.forest.dailyshop.api.config.record.misc.IconRecord;
import cn.encmys.ykdz.forest.dailyshop.api.item.BaseItem;
import cn.encmys.ykdz.forest.dailyshop.api.item.decorator.BaseItemDecorator;
import cn.encmys.ykdz.forest.dailyshop.api.utils.ConfigUtils;
import cn.encmys.ykdz.forest.dailyshop.api.utils.EnumUtils;
import cn.encmys.ykdz.forest.dailyshop.item.builder.BaseItemBuilder;
import org.bukkit.DyeColor;
import org.bukkit.FireworkEffect;
import org.bukkit.block.banner.PatternType;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.inventory.ItemFlag;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class BaseItemDecoratorImpl extends BaseItemDecorator {
    public BaseItemDecoratorImpl(@NotNull BaseItem baseItem, boolean setDefaultName) {
        this.baseItem = baseItem;
        if (setDefaultName) {
            setName(baseItem.getDisplayName());
        }
    }

    @NotNull
    public static BaseItemDecorator get(IconRecord record, boolean setDefaultName) {
        BaseItem item = BaseItemBuilder.get(record.base());
        if (item == null) {
            throw new IllegalArgumentException("Icon " + record.key() + " has invalid base.");
        }
        BaseItemDecorator decorator = new BaseItemDecoratorImpl(item, setDefaultName)
                .setName(record.name())
                .setLore(record.lore())
                .setAmount(record.amount())
                .setUpdatePeriod(record.updatePeriod())
                .setItemFlags(record.itemFlagsData())
                .setCustomModelData(record.customModalData())
                .setBannerPatterns(record.bannerPatternsData())
                .setEnchantments(record.enchantmentsData());

        if (record.commands() != null) {
            decorator.setCommandsData(new HashMap<>() {{
                put(ClickType.LEFT, record.commands().getStringList("left"));
                put(ClickType.RIGHT, record.commands().getStringList("right"));
                put(ClickType.SHIFT_LEFT, record.commands().getStringList("shift-left"));
                put(ClickType.SHIFT_RIGHT, record.commands().getStringList("shift-right"));
                put(ClickType.DOUBLE_CLICK, record.commands().getStringList("double-click"));
                put(ClickType.DROP, record.commands().getStringList("drop"));
                put(ClickType.CONTROL_DROP, record.commands().getStringList("control-drop"));
                put(ClickType.MIDDLE, record.commands().getStringList("middle"));
                put(ClickType.SWAP_OFFHAND, record.commands().getStringList("swap-offhand"));
                put(ClickType.NUMBER_KEY, record.commands().getStringList("number-key"));
                put(ClickType.WINDOW_BORDER_LEFT, record.commands().getStringList("window-border-left"));
                put(ClickType.WINDOW_BORDER_RIGHT, record.commands().getStringList("window-border-right"));
            }});
        }
        if (record.features() != null) {
            decorator.setFeaturesScroll(EnumUtils.getEnumFromName(ClickType.class, record.features().getString("scroll")))
                    .setFeaturesScrollAmount(record.features().getInt("scroll-amount", 0))
                    .setFeaturesPageChange(EnumUtils.getEnumFromName(ClickType.class, record.features().getString("page-change")))
                    .setFeaturesPageChangeAmount(record.features().getInt("page-change-amount", 0))
                    .setFeaturesBackToShop(EnumUtils.getEnumFromName(ClickType.class, record.features().getString("back-to-shop")))
                    .setFeaturesSettleCart(EnumUtils.getEnumFromName(ClickType.class, record.features().getString("settle-cart")))
                    .setFeaturesOpenCart(EnumUtils.getEnumFromName(ClickType.class, record.features().getString("open-cart")))
                    .setFeaturesSwitchShoppingMode(EnumUtils.getEnumFromName(ClickType.class, record.features().getString("switch-shopping-mode")))
                    .setFeaturesSwitchCartMode(EnumUtils.getEnumFromName(ClickType.class, record.features().getString("switch-cart-mode")))
                    .setFeaturesCleanCart(EnumUtils.getEnumFromName(ClickType.class, record.features().getString("clean-cart")))
                    .setFeaturesClearCart(EnumUtils.getEnumFromName(ClickType.class, record.features().getString("clear-cart")))
                    .setFeaturesLoadMoreLog(EnumUtils.getEnumFromName(ClickType.class, record.features().getString("load-more-log")))
                    .setFeaturesOpenShop(EnumUtils.getEnumFromName(ClickType.class, record.features().getString("open-shop")))
                    .setFeaturesOpenShopTarget(record.features().getString("open-shop-target"))
                    .setFeaturesOpenOrderHistory(EnumUtils.getEnumFromName(ClickType.class, record.features().getString("open-order-history")));

        }
        return decorator;
    }

    @Override
    public BaseItem getBaseItem() {
        return baseItem;
    }

    @Override
    public BaseItemDecorator setName(@Nullable String name) {
        if (name == null) {
            return this;
        }
        this.name = name;
        return this;
    }

    @Override
    public BaseItemDecorator setLore(List<String> lore) {
        this.lore = lore;
        return this;
    }

    @Override
    public String getAmount() {
        return amount;
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public BaseItemDecorator setAmount(String amount) {
        this.amount = amount;
        return this;
    }

    @Override
    public List<String> getLore() {
        return lore;
    }

    @Override
    public Integer getCustomModelData() {
        return customModelData;
    }

    @Override
    public BaseItemDecorator setCustomModelData(Integer customModelData) {
        this.customModelData = customModelData;
        return this;
    }

    @Override
    public Map<ItemFlag, Boolean> getItemFlags() {
        return itemFlags;
    }

    @Override
    public BaseItemDecorator setItemFlags(List<String> itemFlagsData) {
        this.itemFlags = itemFlagsData.stream()
                .map(ConfigUtils::parseItemFlagData)
                .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));
        return this;
    }

    @Override
    public Map<PatternType, DyeColor> getBannerPatterns() {
        return bannerPatterns;
    }

    @Override
    public BaseItemDecorator setBannerPatterns(List<String> bannerPatternsData) {
        this.bannerPatterns = bannerPatternsData.stream()
                .map(ConfigUtils::parseBannerPatternData)
                .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));
        return this;
    }

    @Override
    public Map<Enchantment, Integer> getEnchantments() {
        return enchantments;
    }

    @Override
    public BaseItemDecorator setEnchantments(List<String> enchantmentsData) {
        this.enchantments = enchantmentsData.stream()
                .map(ConfigUtils::parseEnchantmentData)
                .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));
        return this;
    }

    @Override
    public List<FireworkEffect> getFireworkEffects() {
        return fireworkEffects;
    }

    @Override
    public BaseItemDecorator setFireworkEffects(List<String> fireworkEffectsData) {
        this.fireworkEffects = fireworkEffectsData.stream()
                .map(ConfigUtils::parseFireworkEffectData)
                .toList();
        return this;
    }

    @Override
    public Map<ClickType, List<String>> getCommandsData() {
        return commands;
    }

    @Override
    public BaseItemDecorator setCommandsData(Map<ClickType, List<String>> commandsData) {
        this.commands = commandsData;
        return this;
    }

    @Override
    public long getUpdatePeriod() {
        return period;
    }

    @Override
    public BaseItemDecorator setUpdatePeriod(long period) {
        this.period = period;
        return this;
    }

    @Override
    public ClickType getFeaturesSettleCart() {
        return featuresSettleCart;
    }

    @Override
    public BaseItemDecorator setFeaturesSettleCart(ClickType featuresSettleCart) {
        this.featuresSettleCart = featuresSettleCart;
        return this;
    }

    @Override
    public BaseItemDecorator setFeaturesScroll(ClickType featuresScroll) {
        this.featuresScroll = featuresScroll;
        return this;
    }

    @Override
    public ClickType getFeaturesScroll() {
        return featuresScroll;
    }

    @Override
    public ClickType getFeaturesBackToShop() {
        return featuresBackToShop;
    }

    @Override
    public BaseItemDecorator setFeaturesBackToShop(ClickType featuresBackToShop) {
        this.featuresBackToShop = featuresBackToShop;
        return this;
    }

    @Override
    public ClickType getFeaturesOpenCart() {
        return featuresOpenCart;
    }

    @Override
    public BaseItemDecorator setFeaturesOpenCart(ClickType featuresOpenCart) {
        this.featuresOpenCart = featuresOpenCart;
        return this;
    }

    @Override
    public BaseItemDecorator setFeaturesScrollAmount(int featuresScrollAmount) {
        this.featuresScrollAmount = featuresScrollAmount;
        return this;
    }

    @Override
    public int getFeaturesScrollAmount() {
        return featuresScrollAmount;
    }

    @Override
    public ClickType getFeaturesSwitchShoppingMode() {
        return featuresSwitchShoppingMode;
    }

    @Override
    public BaseItemDecorator setFeaturesSwitchShoppingMode(ClickType featuresSwitchShoppingMode) {
        this.featuresSwitchShoppingMode = featuresSwitchShoppingMode;
        return this;
    }

    @Override
    public ClickType getFeaturesSwitchCartMode() {
        return featuresSwitchCartMode;
    }

    @Override
    public BaseItemDecorator setFeaturesSwitchCartMode(ClickType featuresSwitchCartMode) {
        this.featuresSwitchCartMode = featuresSwitchCartMode;
        return this;
    }

    @Override
    public ClickType getFeaturesCleanCart() {
        return featuresCleanCart;
    }

    @Override
    public BaseItemDecorator setFeaturesCleanCart(ClickType featuresCleanCart) {
        this.featuresCleanCart = featuresCleanCart;
        return this;
    }

    @Override
    public ClickType getFeaturesClearCart() {
        return featuresClearCart;
    }

    @Override
    public BaseItemDecorator setFeaturesClearCart(ClickType featuresClearCart) {
        this.featuresClearCart = featuresClearCart;
        return this;
    }

    @Override
    public ClickType getFeaturesPageChange() {
        return featuresPageChange;
    }

    @Override
    public BaseItemDecorator setFeaturesPageChange(ClickType featuresPageChange) {
        this.featuresPageChange = featuresPageChange;
        return this;
    }

    @Override
    public int getFeaturesPageChangeAmount() {
        return featuresPageChangeAmount;
    }

    @Override
    public BaseItemDecorator setFeaturesPageChangeAmount(int featuresPageChangeAmount) {
        this.featuresPageChangeAmount = featuresPageChangeAmount;
        return this;
    }

    @Override
    public ClickType getFeaturesLoadMoreLog() {
        return featuresLoadMoreLog;
    }

    @Override
    public BaseItemDecorator setFeaturesLoadMoreLog(ClickType featuresLoadMoreLog) {
        this.featuresLoadMoreLog = featuresLoadMoreLog;
        return this;
    }

    @Override
    public BaseItemDecorator setFeaturesOpenShop(ClickType featuresOpenShop) {
        this.featuresOpenShop = featuresOpenShop;
        return this;
    }

    @Override
    public ClickType getFeaturesOpenShop() {
        return featuresOpenShop;
    }

    @Override
    public BaseItemDecorator setFeaturesOpenShopTarget(String featuresOpenShopTarget) {
        this.featuresOpenShopTarget = featuresOpenShopTarget;
        return this;
    }

    @Override
    public String getFeaturesOpenShopTarget() {
        return featuresOpenShopTarget;
    }

    @Override
    public BaseItemDecorator setFeaturesOpenOrderHistory(ClickType featuresOpenOrderHistory) {
        this.featuresOpenOrderHistory = featuresOpenOrderHistory;
        return this;
    }

    @Override
    public ClickType getFeaturesOpenOrderHistory() {
        return featuresOpenOrderHistory;
    }
}