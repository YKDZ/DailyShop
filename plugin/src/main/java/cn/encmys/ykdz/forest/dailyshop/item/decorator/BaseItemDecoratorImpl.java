package cn.encmys.ykdz.forest.dailyshop.item.decorator;

import cn.encmys.ykdz.forest.dailyshop.api.config.record.shop.IconRecord;
import cn.encmys.ykdz.forest.dailyshop.api.item.BaseItem;
import cn.encmys.ykdz.forest.dailyshop.api.item.decorator.BaseItemDecorator;
import cn.encmys.ykdz.forest.dailyshop.builder.BaseItemBuilder;
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
                .setScroll(record.scroll())
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
                put(ClickType.DROP, record.commands().getStringList("drop"));
                put(ClickType.DOUBLE_CLICK, record.commands().getStringList("double-click"));
                put(ClickType.MIDDLE, record.commands().getStringList("middle"));
            }});
        }
        return decorator;
    }

    @Override
    public BaseItem getItem() {
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
    public BaseItemDecorator setLoreFormat(List<String> loreFormat) {
        this.loreFormat = loreFormat;
        return this;
    }

    @Override
    public BaseItemDecorator setNameFormat(String nameFormat) {
        this.nameFormat = nameFormat;
        return this;
    }

    @Override
    public BaseItemDecorator setBundleContentsLineFormat(String bundleContentsLineFormat) {
        this.bundleContentsLineFormat = bundleContentsLineFormat;
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
    public String getNameFormat() {
        return nameFormat;
    }

    @Override
    public List<String> getLoreFormat() {
        return loreFormat;
    }

    @Override
    public String getBundleContentsLineFormat() {
        return bundleContentsLineFormat;
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
    public BaseItemDecorator setScroll(int scroll) {
        this.scroll = scroll;
        return this;
    }

    @Override
    public int getScroll() {
        return scroll;
    }

    @Override
    public int getScrollShift() {
        return scrollShift;
    }

    // Multi product column in VERTICAL mode or
    // multi product row in HORIZONTAL mode
    // will cause the overflow of max-scroll.
    @Override
    public BaseItemDecorator setScrollShift(int scrollShift) {
        this.scrollShift = scrollShift;
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
}