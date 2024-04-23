package cn.encmys.ykdz.forest.dailyshop.builder;

import cn.encmys.ykdz.forest.dailyshop.DailyShop;
import cn.encmys.ykdz.forest.dailyshop.adventure.AdventureManager;
import cn.encmys.ykdz.forest.dailyshop.api.gui.icon.Icon;
import cn.encmys.ykdz.forest.dailyshop.api.item.BaseItem;
import cn.encmys.ykdz.forest.dailyshop.api.product.Product;
import cn.encmys.ykdz.forest.dailyshop.config.Config;
import cn.encmys.ykdz.forest.dailyshop.config.MessageConfig;
import cn.encmys.ykdz.forest.dailyshop.config.ShopConfig;
import cn.encmys.ykdz.forest.dailyshop.hook.ItemsAdderHook;
import cn.encmys.ykdz.forest.dailyshop.hook.MMOItemsHook;
import cn.encmys.ykdz.forest.dailyshop.hook.MythicMobsHook;
import cn.encmys.ykdz.forest.dailyshop.hook.OraxenHook;
import cn.encmys.ykdz.forest.dailyshop.item.*;
import cn.encmys.ykdz.forest.dailyshop.product.BundleProduct;
import cn.encmys.ykdz.forest.dailyshop.product.factory.ProductFactory;
import cn.encmys.ykdz.forest.dailyshop.shop.Shop;
import cn.encmys.ykdz.forest.dailyshop.shop.cashier.ShopCashier;
import cn.encmys.ykdz.forest.dailyshop.shop.gui.icon.NormalIcon;
import cn.encmys.ykdz.forest.dailyshop.shop.gui.icon.ScrollIcon;
import cn.encmys.ykdz.forest.dailyshop.shop.order.ShopOrder;
import cn.encmys.ykdz.forest.dailyshop.shop.order.enums.SettlementResult;
import cn.encmys.ykdz.forest.dailyshop.shop.pricer.ShopPricer;
import cn.encmys.ykdz.forest.dailyshop.util.CommandUtils;
import cn.encmys.ykdz.forest.dailyshop.util.TextUtils;
import org.bukkit.DyeColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.entity.TropicalFish;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import xyz.xenondevs.invui.gui.ScrollGui;
import xyz.xenondevs.invui.item.Item;
import xyz.xenondevs.invui.item.ItemProvider;
import xyz.xenondevs.invui.item.builder.ItemBuilder;
import xyz.xenondevs.invui.item.impl.AbstractItem;

import java.text.DecimalFormat;
import java.util.*;

public class BaseItemDecorator {
    private static final DecimalFormat decimalFormat = Config.getDecimalFormat();
    private static final AdventureManager adventureManager = DailyShop.getAdventureManager();
    private BaseItem item;
    // Global
    private String name;
    private long period;
    private List<String> lore;
    private int amount;
    private List<String> itemFlags;
    private Integer customModelData;
    private List<String> patternsData = new ArrayList<>();
    private List<String> fireworkEffectData = new ArrayList<>();
    // Icon Only
    private String nameFormat;
    private List<String> loreFormat;
    private String bundleContentsLineFormat;
    // Normal Icon Only
    private Map<ClickType, List<String>> commands = new HashMap<>();
    private int scroll;
    private int scrollShift;

    private BaseItemDecorator() {
    }

    public static BaseItemDecorator get(String base, boolean setDefaultName) {
        if (base.startsWith(MMOItemsHook.getIdentifier()) && MMOItemsHook.isHooked()) {
            String[] typeId = base.substring(MMOItemsHook.getIdentifier().length()).split(":");
            String type = typeId[0];
            String id = typeId[1];
            return BaseItemDecorator.mmoitems(type, id, setDefaultName);
        } else if (base.startsWith(ItemsAdderHook.getIdentifier()) && ItemsAdderHook.isHooked()) {
            String namespacedId = base.substring(ItemsAdderHook.getIdentifier().length());
            return BaseItemDecorator.itemsadder(namespacedId, setDefaultName);
        } else if (base.startsWith(OraxenHook.getIdentifier()) && OraxenHook.isHooked()) {
            String id = base.substring(OraxenHook.getIdentifier().length());
            return BaseItemDecorator.oraxen(id, setDefaultName);
        } else if (base.startsWith(MythicMobsHook.getIdentifier()) && MythicMobsHook.isHooked()) {
            String id = base.substring(MythicMobsHook.getIdentifier().length());
            return BaseItemDecorator.mythicmobs(id, setDefaultName);
        } else if (base.startsWith("SKULL:")) {
            String url = base.substring(6);
            return BaseItemDecorator.skull(url, setDefaultName);
        } else if (base.startsWith("FIREWORK:")) {
            String power = base.substring(9);
            return BaseItemDecorator.firework(Integer.parseInt(power), setDefaultName);
        } else if (base.startsWith("POTION:")) {
            String[] data = base.substring(7).split(":");
            Material potionType = Material.POTION;
            if (data[0].equalsIgnoreCase("LINGERING")) {
                potionType = Material.LINGERING_POTION;
            } else if (data[0].equalsIgnoreCase("SPLASH")) {
                potionType = Material.SPLASH_POTION;
            }
            if (data[1].equals("NONE")) {
                return BaseItemDecorator.potion(potionType, data[1], false, false, setDefaultName);
            }
            boolean upgradeable = Boolean.parseBoolean(data[2]);
            boolean extendable = Boolean.parseBoolean(data[3]);
            return BaseItemDecorator.potion(potionType, data[1], upgradeable, extendable, setDefaultName);
        } else if (base.startsWith("FISH_BUCKET:")) {
            String[] data = base.substring(12).split(":");

            TropicalFish.Pattern pattern = TropicalFish.Pattern.valueOf(data[0]);
            DyeColor bodyColor = DyeColor.valueOf(data[1]);
            DyeColor patternColor = DyeColor.valueOf(data[2]);

            return BaseItemDecorator.tropicalFishBucket(pattern, patternColor, bodyColor, setDefaultName);
        } else {
            Material material = Material.matchMaterial(base.toUpperCase());
            if (material == null) {
                return null;
            }
            return BaseItemDecorator.vanilla(material, setDefaultName);
        }
    }

    public static BaseItemDecorator mmoitems(String type, String id, boolean setDefaultName) {
        BaseItem item = new MMOItemsItem(type, id);
        if (!item.isExist()) {
            return null;
        }
        return new BaseItemDecorator()
                .setItem(item, setDefaultName);
    }

    public static BaseItemDecorator itemsadder(String namespacedId, boolean setDefaultName) {
        BaseItem item = new ItemsAdderItem(namespacedId);
        if (!item.isExist()) {
            return null;
        }
        return new BaseItemDecorator()
                .setItem(item, setDefaultName);
    }

    public static BaseItemDecorator oraxen(String id, boolean setDefaultName) {
        BaseItem item = new OraxenItem(id);
        if (!item.isExist()) {
            return null;
        }
        return new BaseItemDecorator()
                .setItem(item, setDefaultName);
    }

    public static BaseItemDecorator mythicmobs(String id, boolean setDefaultName) {
        BaseItem item = new MythicMobsItem(id);
        if (!item.isExist()) {
            return null;
        }
        return new BaseItemDecorator()
                .setItem(item, setDefaultName);
    }

    public static BaseItemDecorator neigeitems(String id, boolean setDefaultName) {
        BaseItem item = new NeigeItemsItem(id);
        if (!item.isExist()) {
            return null;
        }
        return new BaseItemDecorator()
                .setItem(item, setDefaultName);
    }

    public static BaseItemDecorator customcrops(String id, boolean setDefaultName) {
        BaseItem item = new CustomCropsItem(id);
        if (!item.isExist()) {
            return null;
        }
        return new BaseItemDecorator()
                .setItem(item, setDefaultName);
    }

    public static BaseItemDecorator customfishing(String namespace, String id, boolean setDefaultName) {
        BaseItem item = new CustomFishingItem(namespace, id);
        if (!item.isExist()) {
            return null;
        }
        return new BaseItemDecorator()
                .setItem(item, setDefaultName);
    }

    public static BaseItemDecorator vanilla(Material material, boolean setDefaultName) {
        BaseItem item = new VanillaItem(material);
        if (!item.isExist()) {
            return null;
        }
        return new BaseItemDecorator()
                .setItem(item, setDefaultName);
    }

    public static BaseItemDecorator skull(String url, boolean setDefaultName) {
        BaseItem item = new SkullItem(url);
        if (!item.isExist()) {
            return null;
        }
        return new BaseItemDecorator()
                .setItem(item, setDefaultName);
    }

    public static BaseItemDecorator firework(int power, boolean setDefaultName) {
        BaseItem item = new FireworkItem(power);
        if (!item.isExist()) {
            return null;
        }
        return new BaseItemDecorator()
                .setItem(item, setDefaultName);
    }

    public static BaseItemDecorator potion(Material potionType, String effectType, boolean upgradeable, boolean extendable, boolean setDefaultName) {
        BaseItem item = new PotionItem(potionType, effectType, upgradeable, extendable);
        if (!item.isExist()) {
            return null;
        }
        return new BaseItemDecorator()
                .setItem(item, setDefaultName);
    }

    public static BaseItemDecorator tropicalFishBucket(TropicalFish.Pattern pattern, DyeColor patternColor, DyeColor bodyColor, boolean setDefaultName) {
        BaseItem item = new TropicalFishBucketItem(pattern, patternColor, bodyColor);
        if (!item.isExist()) {
            return null;
        }
        return new BaseItemDecorator()
                .setItem(item, setDefaultName);
    }

    public BaseItemDecorator setItem(BaseItem item, boolean setDefaultName) {
        this.item = item;
        if (setDefaultName) {
            setName(item.getDisplayName());
        }
        return this;
    }

    public BaseItem getItem() {
        return item;
    }

    public BaseItemDecorator setName(String name) {
        if (name == null) {
            return this;
        }
        this.name = name;
        return this;
    }

    public BaseItemDecorator setLore(List<String> lore) {
        this.lore = lore;
        return this;
    }

    public BaseItemDecorator setLoreFormat(List<String> loreFormat) {
        this.loreFormat = loreFormat;
        return this;
    }

    public BaseItemDecorator setNameFormat(String nameFormat) {
        this.nameFormat = nameFormat;
        return this;
    }

    public BaseItemDecorator setBundleContentsLineFormat(String bundleContentsLineFormat) {
        this.bundleContentsLineFormat = bundleContentsLineFormat;
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

    public BaseItemDecorator setItemFlags(List<String> itemFlags) {
        this.itemFlags = itemFlags;
        return this;
    }

    public BaseItemDecorator setAmount(int amount) {
        this.amount = amount;
        return this;
    }

    private String getNameFormat() {
        return nameFormat;
    }

    public List<String> getLoreFormat() {
        return loreFormat;
    }

    public String getBundleContentsLineFormat() {
        return bundleContentsLineFormat;
    }

    public List<String> getLore() {
        return lore;
    }

    public Integer getCustomModelData() {
        return customModelData;
    }

    public BaseItemDecorator setCustomModelData(Integer customModelData) {
        this.customModelData = customModelData;
        return this;
    }

    public List<String> getPatternsData() {
        return patternsData;
    }

    public BaseItemDecorator setPatternsData(List<String> patternsData) {
        this.patternsData = patternsData;
        return this;
    }

    public Map<ClickType, List<String>> getCommands() {
        return commands;
    }

    public BaseItemDecorator setCommands(Map<ClickType, List<String>> commands) {
        this.commands = commands;
        return this;
    }

    public BaseItemDecorator setScroll(int scroll) {
        this.scroll = scroll;
        return this;
    }

    public int getScroll() {
        return scroll;
    }

    public int getScrollShift() {
        return scrollShift;
    }

    // Multi product column in VERTICAL mode or
    // multi product row in HORIZONTAL mode
    // will cause the overflow of max-scroll.
    public BaseItemDecorator setScrollShift(int scrollShift) {
        this.scrollShift = scrollShift;
        return this;
    }

    public long getPeriod() {
        return period;
    }

    public BaseItemDecorator setPeriod(long period) {
        this.period = period;
        return this;
    }

    public List<String> getFireworkEffectData() {
        return fireworkEffectData;
    }

    public BaseItemDecorator setFireworkEffectData(List<String> fireworkEffectData) {
        this.fireworkEffectData = fireworkEffectData;
        return this;
    }

    public Item buildProductIcon(String shopId, Product product) {
        ProductFactory productFactory = DailyShop.getProductFactory();
        return new AbstractItem() {
            @Override
            public ItemProvider getItemProvider() {
                Shop shop = DailyShop.getShopFactory().getShop(shopId);
                setNameFormat(ShopConfig.getProductNameFormat(shopId));
                setLoreFormat(ShopConfig.getProductLoreFormat(shopId));
                setBundleContentsLineFormat(ShopConfig.getBundleContentsLineFormat(shopId));

                // Handle lore for bundle contents
                List<String> bundleContentsLore = new ArrayList<>();
                if (product instanceof BundleProduct) {
                    Set<String> bundleContents = ((BundleProduct) product).getBundleContents().keySet();
                    if (!bundleContents.isEmpty()) {
                        for (String contentId : bundleContents) {
                            Product content = productFactory.getProduct(contentId);
                            bundleContentsLore.add(TextUtils.decorateTextInMiniMessage(getBundleContentsLineFormat(), null, new HashMap<>() {{
                                put("name", content.getIconBuilder().getName());
                                put("amount", String.valueOf(content.getProductItemBuilder().getAmount()));
                            }}));
                        }
                    }
                }

                ShopPricer shopPricer = shop.getShopPricer();
                // Vars for the product itself
                Map<String, String> vars = new HashMap<>() {{
                    put("name", getName());
                    put("amount", String.valueOf(getAmount()));
                    put("buy-price", shopPricer.getBuyPrice(product.getId()) != -1d ? decimalFormat.format(shopPricer.getBuyPrice(product.getId())) : ShopConfig.getDisabledPrice(shopId));
                    put("sell-price", shopPricer.getSellPrice(product.getId()) != -1d ? decimalFormat.format(shopPricer.getSellPrice(product.getId())) : ShopConfig.getDisabledPrice(shopId));
                    put("rarity", product.getRarity().getName());
                }};

                Map<String, List<String>> listVars = new HashMap<>() {{
                    put("desc-lore", getLore());
                    put("bundle-contents", bundleContentsLore);
                }};

                return new ItemBuilder(
                        new cn.encmys.ykdz.forest.dailyshop.util.ItemBuilder(getItem().build(null))
                                .setCustomModelData(getCustomModelData())
                                .setItemFlags(getItemFlags())
                                .setLore(TextUtils.decorateTextWithListVar(getLoreFormat(), null, listVars, vars))
                                .setDisplayName(TextUtils.decorateTextWithVar(getNameFormat(), null, vars))
                                .setBannerPatterns(getPatternsData())
                                .setFireworkEffects(getFireworkEffectData())
                                .build(getAmount()));
            }

            @Override
            public void handleClick(@NotNull ClickType clickType, @NotNull Player player, @NotNull InventoryClickEvent event) {
                Shop shop = DailyShop.getShopFactory().getShop(shopId);
                ShopPricer shopPricer = shop.getShopPricer();
                ShopCashier shopCashier = shop.getShopCashier();
                Map<String, String> vars = new HashMap<>() {{
                    put("name", getName());
                    put("amount", String.valueOf(getAmount()));
                    put("shop", DailyShop.getShopFactory().getShop(shopId).getName());
                    put("cost", decimalFormat.format(shopPricer.getBuyPrice(product.getId())));
                    put("earn", decimalFormat.format(shopPricer.getSellPrice(product.getId())));
                }};

                if (clickType == ClickType.LEFT) {
                    SettlementResult result = shopCashier.settle(ShopOrder.sellToOrder(player)
                            .addProduct(product, 1));
                    if (result != SettlementResult.SUCCESS) {
                        switch (result) {
                            case TRANSITION_DISABLED -> adventureManager.sendMessageWithPrefix(player, TextUtils.decorateTextInMiniMessage(MessageConfig.messages_action_buy_failure_disable, player, vars));
                            case NOT_ENOUGH_MONEY -> adventureManager.sendMessageWithPrefix(player, TextUtils.decorateTextInMiniMessage(MessageConfig.messages_action_buy_failure_money, player, vars));
                        }
                        return;
                    }
                    adventureManager.sendMessageWithPrefix(player, TextUtils.decorateTextInMiniMessage(MessageConfig.messages_action_buy_success, player, vars));
                    player.playSound(player.getLocation(), ShopConfig.getBuySound(shopId), 1f, 1f);
                } else if (clickType == ClickType.RIGHT) {
                    SettlementResult result = shopCashier.settle(ShopOrder.buyFromOrder(player)
                            .addProduct(product, 1));
                    if (result != SettlementResult.SUCCESS) {
                        switch (result) {
                            case TRANSITION_DISABLED -> adventureManager.sendMessageWithPrefix(player, TextUtils.decorateTextInMiniMessage(MessageConfig.messages_action_sell_failure_disable, player, vars));
                            case NOT_ENOUGH_PRODUCT -> adventureManager.sendMessageWithPrefix(player, TextUtils.decorateTextInMiniMessage(MessageConfig.messages_action_sell_failure_notEnough, player, vars));
                        }
                        return;
                    }
                    adventureManager.sendMessageWithPrefix(player, TextUtils.decorateTextInMiniMessage(MessageConfig.messages_action_sell_success, player, vars));
                    player.playSound(player.getLocation(), ShopConfig.getSellSound(shopId), 1f, 1f);
                } else if (clickType == ClickType.SHIFT_RIGHT) {
                    ShopOrder order = ShopOrder.buyAllFromOrder(player)
                            .addProduct(product, 1);
                    SettlementResult result = shopCashier.settle(order);
                    if (result == SettlementResult.NOT_ENOUGH_PRODUCT) {
                        adventureManager.sendMessageWithPrefix(player, TextUtils.decorateTextInMiniMessage(MessageConfig.messages_action_sellAll_failure_notEnough, player, vars));
                        return;
                    }
                    int stack = order.getTotalStack();
                    vars.put("earn", decimalFormat.format(shopPricer.getSellPrice(product.getId()) * stack));
                    vars.put("stack", String.valueOf(stack));
                    adventureManager.sendMessageWithPrefix(player, TextUtils.decorateTextInMiniMessage(MessageConfig.messages_action_sellAll_success, player, vars));
                    player.playSound(player.getLocation(), ShopConfig.getSellSound(shopId), 1f, 1f);
                }

                // Not needed before total market volume feature
                // notifyWindows();
            }
        };
    }

    public Item buildNormalIcon() {
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
                                    .setBannerPatterns(getPatternsData())
                                    .setFireworkEffects(getFireworkEffectData())
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
                        put("max-scroll", String.valueOf(gui.getMaxLine() + 1 - getScrollShift() + 1));
                    }};
                    return new ItemBuilder(
                            new cn.encmys.ykdz.forest.dailyshop.util.ItemBuilder(getItem().build(null))
                                    .setCustomModelData(getCustomModelData())
                                    .setItemFlags(getItemFlags())
                                    .setLore(TextUtils.decorateTextWithVar(getLore(), null, vars))
                                    .setDisplayName(TextUtils.decorateTextWithVar(getName(), null, vars))
                                    .setFireworkEffects(getFireworkEffectData())
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

    public ItemStack buildProductItem(@Nullable Player player) {
        return new cn.encmys.ykdz.forest.dailyshop.util.ItemBuilder(getItem().build(player))
                .setDisplayName(TextUtils.decorateText(getName(), player))
                .setLore(TextUtils.decorateText(getLore(), player))
                .setItemFlags(getItemFlags())
                .setCustomModelData(getCustomModelData())
                .setBannerPatterns(getPatternsData())
                .setFireworkEffects(getFireworkEffectData())
                .build(getAmount());
    }
}