package cn.encmys.ykdz.forest.dailyshop.item.decorator;

import cn.encmys.ykdz.forest.dailyshop.api.config.record.shop.IconRecord;
import cn.encmys.ykdz.forest.dailyshop.api.item.BaseItem;
import cn.encmys.ykdz.forest.dailyshop.api.item.decorator.BaseItemDecorator;
import cn.encmys.ykdz.forest.dailyshop.api.utils.EnumUtils;
import cn.encmys.ykdz.forest.dailyshop.item.builder.BaseItemBuilder;
import org.bukkit.event.inventory.ClickType;
import org.jetbrains.annotations.NotNull;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class BaseItemDecoratorImpl extends BaseItemDecorator {
    public BaseItemDecoratorImpl(@NotNull BaseItem item, boolean setDefaultName) {
        this.item = item;
        if (setDefaultName) {
            setName(item.getDisplayName());
        }
    }

    public static BaseItemDecorator get(IconRecord record, boolean setDefaultName) {
        BaseItem item = BaseItemBuilder.get(record.item());
        if (item == null) {
            return null;
        }
        BaseItemDecorator decorator = new BaseItemDecoratorImpl(item, setDefaultName)
                .setLore(record.lore())
                .setAmount(record.amount())
                .setUpdatePeriod(record.updatePeriod())
                .setItemFlags(record.itemFlags())
                .setCustomModelData(record.customModalData())
                .setBannerPatterns(record.bannerPatterns());
        if (setDefaultName) {
            decorator.setName(record.name());
        }
        if (record.commands() != null) {
            decorator.setCommands(new HashMap<>() {{
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
                    .setFeaturesBackToShop(EnumUtils.getEnumFromName(ClickType.class, record.features().getString("back-to-shop")))
                    .setFeaturesSettleCart(EnumUtils.getEnumFromName(ClickType.class, record.features().getString("settle-cart")))
                    .setFeaturesOpenCart(EnumUtils.getEnumFromName(ClickType.class, record.features().getString("open-cart")))
                    .setFeaturesSwitchShoppingMode(EnumUtils.getEnumFromName(ClickType.class, record.features().getString("switch-shopping-mode")));

        }
        return decorator;
    }

    @Override
    public BaseItem getBaseItem() {
        return item;
    }

    @Override
    public BaseItemDecorator setName(String name) {
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
    public int getAmount() {
        return amount;
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public List<String> getItemFlags() {
        return itemFlags;
    }

    @Override
    public BaseItemDecorator setItemFlags(List<String> itemFlags) {
        this.itemFlags = itemFlags;
        return this;
    }

    @Override
    public BaseItemDecorator setAmount(int amount) {
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
    public List<String> getPatternsData() {
        return patternsData;
    }

    @Override
    public BaseItemDecorator setBannerPatterns(List<String> patternsData) {
        this.patternsData = patternsData;
        return this;
    }

    @Override
    public Map<ClickType, List<String>> getCommands() {
        return commands;
    }

    @Override
    public BaseItemDecorator setCommands(Map<ClickType, List<String>> commands) {
        this.commands = commands;
        return this;
    }

    @Override
    public long getPeriod() {
        return period;
    }

    @Override
    public BaseItemDecorator setUpdatePeriod(long period) {
        this.period = period;
        return this;
    }

    @Override
    public List<String> getFireworkEffectData() {
        return fireworkEffectData;
    }

    @Override
    public BaseItemDecorator setFireworkEffectData(List<String> fireworkEffectData) {
        this.fireworkEffectData = fireworkEffectData;
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
}