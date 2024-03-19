package cn.encmys.ykdz.forest.dailyshop.builder;

import cn.encmys.ykdz.forest.dailyshop.DailyShop;
import cn.encmys.ykdz.forest.dailyshop.adventure.AdventureManager;
import cn.encmys.ykdz.forest.dailyshop.api.item.ProductItem;
import cn.encmys.ykdz.forest.dailyshop.gui.icon.NormalIcon;
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
import xyz.xenondevs.invui.item.Item;
import xyz.xenondevs.invui.item.ItemProvider;
import xyz.xenondevs.invui.item.builder.ItemBuilder;

import java.util.ArrayList;
import java.util.List;

public class NormalIconBuilder {
    private static final AdventureManager adventureManager = DailyShop.getAdventureManager();
    private ProductItem item;
    private String name;
    private List<String> lore = new ArrayList<>();
    private int amount;
    private List<String> itemFlags = new ArrayList<>();
    private long period;
    private List<String> commands = new ArrayList<>();

    private NormalIconBuilder() {
    }

    public static NormalIconBuilder mmoitems(String type, String id) {
        ProductItem item = new MMOItemsItem(type, id);
        return new NormalIconBuilder()
                .setItem(item)
                .setName(item.getDisplayName());
    }

    public static NormalIconBuilder itemsadder(String namespacedId) {
        ProductItem item = new ItemsAdderItem(namespacedId);
        return new NormalIconBuilder()
                .setItem(item)
                .setName(item.getDisplayName());
    }

    public static NormalIconBuilder oraxen(String id) {
        ProductItem item = new OraxenItem(id);
        return new NormalIconBuilder()
                .setItem(item)
                .setName(item.getDisplayName());
    }

    public static NormalIconBuilder vanilla(Material material) {
        ProductItem item = new VanillaItem(material);
        return new NormalIconBuilder()
                .setItem(new VanillaItem(material))
                .setName(item.getDisplayName());
    }

    public NormalIconBuilder setItem(ProductItem item) {
        this.item = item;
        return this;
    }

    public ProductItem getItem() {
        return item;
    }

    public NormalIconBuilder setName(String name) {
        if (name == null) {
            return this;
        }
        this.name = name;
        return this;
    }

    public NormalIconBuilder setLore(List<String> lore) {
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

    public void setItemFlags(List<String> itemFLags) {
        this.itemFlags = itemFLags;
    }

    public NormalIconBuilder setAmount(int amount) {
        this.amount = amount;
        return this;
    }

    public List<String> getLore() {
        return lore;
    }

    public long getPeriod() {
        return period;
    }

    public NormalIconBuilder setPeriod(long period) {
        this.period = period;
        return this;
    }

    public List<String> getCommands() {
        return commands;
    }

    public NormalIconBuilder setCommands(List<String> commands) {
        this.commands = commands;
        return this;
    }

    public Item build() {
        NormalIcon icon = new NormalIcon() {
            @Override
            public ItemProvider getItemProvider() {
                return new ItemBuilder(getItem().build(null))
                        .setDisplayName(TextUtils.decorateText(getName(), null))
                        .setAmount(getAmount())
                        .setLegacyLore(TextUtils.decorateText(getLore(), null));
            }

            @Override
            public void handleClick(@NotNull ClickType clickType, @NotNull Player player, @NotNull InventoryClickEvent event) {
                if (!getCommands().isEmpty()) {
                    CommandUtils.dispatchCommands(player, getCommands());
                }
            }
        };

        if (getPeriod() > 0) {
            icon.startUpdater(getPeriod());
        }

        return icon;
    }
}
