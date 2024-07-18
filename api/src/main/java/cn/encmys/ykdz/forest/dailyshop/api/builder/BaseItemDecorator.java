package cn.encmys.ykdz.forest.dailyshop.api.builder;

import cn.encmys.ykdz.forest.dailyshop.api.item.BaseItem;
import cn.encmys.ykdz.forest.dailyshop.api.product.Product;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.Nullable;
import xyz.xenondevs.invui.item.Item;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public abstract class BaseItemDecorator {
    protected BaseItem item;
    // Global
    protected String name;
    protected long period;
    protected List<String> lore;
    protected int amount;
    protected List<String> itemFlags;
    protected Integer customModelData;
    protected List<String> patternsData = new ArrayList<>();
    protected List<String> fireworkEffectData = new ArrayList<>();
    // Icon Only
    protected String nameFormat;
    protected List<String> loreFormat;
    protected String bundleContentsLineFormat;
    // Normal Icon Only
    protected Map<ClickType, List<String>> commands = new HashMap<>();
    protected int scroll;
    protected int scrollShift;

    public abstract BaseItemDecorator setItem(BaseItem item, boolean setDefaultName);

    public abstract BaseItem getItem();

    public abstract BaseItemDecorator setName(String name);

    public abstract BaseItemDecorator setLore(List<String> lore);

    public abstract BaseItemDecorator setLoreFormat(List<String> loreFormat);

    public abstract BaseItemDecorator setNameFormat(String nameFormat);

    public abstract BaseItemDecorator setBundleContentsLineFormat(String bundleContentsLineFormat);

    public abstract int getAmount();

    public abstract String getName();

    public abstract List<String> getItemFlags();

    public abstract BaseItemDecorator setItemFlags(List<String> itemFlags);

    public abstract BaseItemDecorator setAmount(int amount);

    protected abstract String getNameFormat();

    public abstract List<String> getLoreFormat();

    public abstract String getBundleContentsLineFormat();

    public abstract List<String> getLore();

    public abstract Integer getCustomModelData();

    public abstract BaseItemDecorator setCustomModelData(Integer customModelData);

    public abstract List<String> getPatternsData();

    public abstract BaseItemDecorator setPatternsData(List<String> patternsData);

    public abstract Map<ClickType, List<String>> getCommands();

    public abstract BaseItemDecorator setCommands(Map<ClickType, List<String>> commands);

    public abstract BaseItemDecorator setScroll(int scroll);

    public abstract int getScroll();

    public abstract int getScrollShift();

    // Multi product column in VERTICAL mode or
    // multi product row in HORIZONTAL mode
    // will cause the overflow of max-scroll.
    public abstract BaseItemDecorator setScrollShift(int scrollShift);

    public abstract long getPeriod();

    public abstract BaseItemDecorator setPeriod(long period);

    public abstract List<String> getFireworkEffectData();

    public abstract BaseItemDecorator setFireworkEffectData(List<String> fireworkEffectData);

    public abstract Item buildProductIcon(Player player, String shopId, Product product);

    public abstract Item buildNormalIcon();

    public abstract ItemStack buildProductItem(@Nullable Player player);
}
