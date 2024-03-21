package cn.encmys.ykdz.forest.dailyshop.builder;

import cn.encmys.ykdz.forest.dailyshop.api.gui.icon.Icon;
import cn.encmys.ykdz.forest.dailyshop.api.item.ProductItem;
import cn.encmys.ykdz.forest.dailyshop.gui.icon.NormalIcon;
import cn.encmys.ykdz.forest.dailyshop.gui.icon.ScrollIcon;
import cn.encmys.ykdz.forest.dailyshop.hook.ItemsAdderHook;
import cn.encmys.ykdz.forest.dailyshop.hook.MMOItemsHook;
import cn.encmys.ykdz.forest.dailyshop.hook.OraxenHook;
import cn.encmys.ykdz.forest.dailyshop.item.ItemsAdderItem;
import cn.encmys.ykdz.forest.dailyshop.item.MMOItemsItem;
import cn.encmys.ykdz.forest.dailyshop.item.OraxenItem;
import cn.encmys.ykdz.forest.dailyshop.item.VanillaItem;
import cn.encmys.ykdz.forest.dailyshop.util.CommandUtils;
import cn.encmys.ykdz.forest.dailyshop.util.TextUtils;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.jetbrains.annotations.NotNull;
import xyz.xenondevs.invui.gui.ScrollGui;
import xyz.xenondevs.invui.item.Item;
import xyz.xenondevs.invui.item.ItemProvider;
import xyz.xenondevs.invui.item.builder.ItemBuilder;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class IconBuilder {
    private ProductItem item;
    private String name;
    private List<String> lore = new ArrayList<>();
    private int amount;
    private List<String> itemFlags = new ArrayList<>();
    private long period;
    private Map<ClickType, List<String>> commands = new HashMap<>();
    private int scroll;
    private Integer customModelData;

    private IconBuilder() {
    }

    public static IconBuilder get(String item) {
        if (item.startsWith("MI:") && MMOItemsHook.isHooked()) {
            String[] typeId = item.substring(3).split(":");
            String type = typeId[0];
            String id = typeId[1];
            return IconBuilder.mmoitems(type, id);
        } else if (item.startsWith("IA:") && ItemsAdderHook.isHooked()) {
            String namespacedId = item.substring(3);
            return IconBuilder.itemsadder(namespacedId);
        } else if (item.startsWith("OXN:") && OraxenHook.isHooked()) {
            String id = item.substring(3);
            return IconBuilder.oraxen(id);
        } else {
            Material material = Material.valueOf(item);
            return IconBuilder.vanilla(material);
        }
    }

    public static IconBuilder mmoitems(String type, String id) {
        ProductItem item = new MMOItemsItem(type, id);
        return new IconBuilder()
                .setItem(item)
                .setName(item.getDisplayName());
    }

    public static IconBuilder itemsadder(String namespacedId) {
        ProductItem item = new ItemsAdderItem(namespacedId);
        return new IconBuilder()
                .setItem(item)
                .setName(item.getDisplayName());
    }

    public static IconBuilder oraxen(String id) {
        ProductItem item = new OraxenItem(id);
        return new IconBuilder()
                .setItem(item)
                .setName(item.getDisplayName());
    }

    public static IconBuilder vanilla(Material material) {
        ProductItem item = new VanillaItem(material);
        return new IconBuilder()
                .setItem(new VanillaItem(material))
                .setName(item.getDisplayName());
    }

    public IconBuilder setItem(ProductItem item) {
        this.item = item;
        return this;
    }

    public ProductItem getItem() {
        return item;
    }

    public IconBuilder setName(String name) {
        if (name == null) {
            return this;
        }
        this.name = name;
        return this;
    }

    public IconBuilder setLore(List<String> lore) {
        this.lore = lore;
        return this;
    }

    public int getAmount() {
        return amount;
    }

    public String getName() {
        return name;
    }

    public List<String> getItemFlags() {
        return itemFlags;
    }

    public IconBuilder setItemFlags(List<String> itemFlags) {
        this.itemFlags = itemFlags;
        return this;
    }

    public IconBuilder setAmount(int amount) {
        this.amount = amount;
        return this;
    }

    public List<String> getLore() {
        return lore;
    }

    public long getPeriod() {
        return period;
    }

    public IconBuilder setPeriod(long period) {
        this.period = period;
        return this;
    }

    public Map<ClickType, List<String>> getCommands() {
        return commands;
    }

    public IconBuilder setCommands(Map<ClickType, List<String>> commands) {
        this.commands = commands;
        return this;
    }

    public IconBuilder setScroll(int scroll) {
        this.scroll = scroll;
        return this;
    }

    public int getScroll() {
        return scroll;
    }

    public IconBuilder setCustomModelData(Integer customModelData) {
        this.customModelData = customModelData;
        return this;
    }

    public Integer getCustomModelData() {
        return customModelData;
    }

    public Item build() {
        Item icon = null;

        if (getScroll() == 0) {
            icon = new NormalIcon() {
                @Override
                public ItemProvider getItemProvider() {
                    return new ItemBuilder(
                            new cn.encmys.ykdz.forest.dailyshop.util.ItemBuilder(getItem().build(null))
                                    .setCustomModelData(getCustomModelData())
                                    .setItemFlags(getItemFlags())
                                    .setLore(TextUtils.decorateText(getLore(), null))
                                    .setDisplayName(TextUtils.decorateText(getName(), null))
                                    .build(getAmount()));
                }

                @Override
                public void handleClick(@NotNull ClickType clickType, @NotNull Player player, @NotNull InventoryClickEvent event) {
                    switch (clickType) {
                        case LEFT ->
                                CommandUtils.dispatchCommands(player, getCommands().getOrDefault(ClickType.LEFT, null));
                        case RIGHT ->
                                CommandUtils.dispatchCommands(player, getCommands().getOrDefault(ClickType.RIGHT, null));
                        case SHIFT_LEFT ->
                                CommandUtils.dispatchCommands(player, getCommands().getOrDefault(ClickType.SHIFT_LEFT, null));
                        case SHIFT_RIGHT ->
                                CommandUtils.dispatchCommands(player, getCommands().getOrDefault(ClickType.SHIFT_RIGHT, null));
                        case DROP ->
                                CommandUtils.dispatchCommands(player, getCommands().getOrDefault(ClickType.DROP, null));
                        case DOUBLE_CLICK ->
                                CommandUtils.dispatchCommands(player, getCommands().getOrDefault(ClickType.DOUBLE_CLICK, null));
                        case MIDDLE ->
                                CommandUtils.dispatchCommands(player, getCommands().getOrDefault(ClickType.MIDDLE, null));
                        default -> {
                        }
                    }
                }
            };
        } else {
            icon = new ScrollIcon(getScroll()) {
                @Override
                public ItemProvider getItemProvider(ScrollGui<?> gui) {
                    HashMap<String, String> vars = new HashMap<>() {{
                        put("current-scroll", String.valueOf(gui.getCurrentLine() + 1));
                        put("max-scroll", String.valueOf(gui.getMaxLine() + 1));
                    }};
                    return new ItemBuilder(
                            new cn.encmys.ykdz.forest.dailyshop.util.ItemBuilder(getItem().build(null))
                                    .setCustomModelData(getCustomModelData())
                                    .setItemFlags(getItemFlags())
                                    .setLore(TextUtils.decorateTextWithVar(getLore(), null, vars))
                                    .setDisplayName(TextUtils.decorateTextWithVar(getName(), null, vars))
                                    .build(getAmount()));
                }
            };
        }

        // Auto Update
        if (getPeriod() > 0) {
            ((Icon) icon).startUpdater(getPeriod());
        }

        return icon;
    }
}
