package cn.encmys.ykdz.forest.dailyshop.builder;

import cn.encmys.ykdz.forest.dailyshop.api.DailyShop;
import cn.encmys.ykdz.forest.dailyshop.api.builder.BaseItemDecorator;
import cn.encmys.ykdz.forest.dailyshop.api.config.Config;
import cn.encmys.ykdz.forest.dailyshop.api.config.MessageConfig;
import cn.encmys.ykdz.forest.dailyshop.api.config.ShopConfig;
import cn.encmys.ykdz.forest.dailyshop.api.gui.icon.Icon;
import cn.encmys.ykdz.forest.dailyshop.api.item.BaseItem;
import cn.encmys.ykdz.forest.dailyshop.api.product.Product;
import cn.encmys.ykdz.forest.dailyshop.api.product.factory.ProductFactory;
import cn.encmys.ykdz.forest.dailyshop.api.shop.Shop;
import cn.encmys.ykdz.forest.dailyshop.api.shop.cashier.ShopCashier;
import cn.encmys.ykdz.forest.dailyshop.api.shop.order.ShopOrder;
import cn.encmys.ykdz.forest.dailyshop.api.shop.order.enums.SettlementResult;
import cn.encmys.ykdz.forest.dailyshop.api.shop.pricer.ShopPricer;
import cn.encmys.ykdz.forest.dailyshop.gui.icon.NormalIcon;
import cn.encmys.ykdz.forest.dailyshop.gui.icon.ScrollIcon;
import cn.encmys.ykdz.forest.dailyshop.hook.ItemsAdderHook;
import cn.encmys.ykdz.forest.dailyshop.hook.MMOItemsHook;
import cn.encmys.ykdz.forest.dailyshop.hook.MythicMobsHook;
import cn.encmys.ykdz.forest.dailyshop.item.*;
import cn.encmys.ykdz.forest.dailyshop.product.BundleProduct;
import cn.encmys.ykdz.forest.dailyshop.api.utils.CommandUtils;
import cn.encmys.ykdz.forest.dailyshop.api.utils.TextUtils;
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

import java.util.*;

public class BaseItemDecoratorImpl extends BaseItemDecorator {

    private BaseItemDecoratorImpl() {
    }

    public static BaseItemDecorator get(String base, boolean setDefaultName) {
        if (base.startsWith(MMOItemsHook.getIdentifier()) && MMOItemsHook.isHooked()) {
            String[] typeId = base.substring(MMOItemsHook.getIdentifier().length()).split(":");
            String type = typeId[0];
            String id = typeId[1];
            return BaseItemDecoratorImpl.mmoitems(type, id, setDefaultName);
        } else if (base.startsWith(ItemsAdderHook.getIdentifier()) && ItemsAdderHook.isHooked()) {
            String namespacedId = base.substring(ItemsAdderHook.getIdentifier().length());
            return BaseItemDecoratorImpl.itemsadder(namespacedId, setDefaultName);
        } else if (base.startsWith(MythicMobsHook.getIdentifier()) && MythicMobsHook.isHooked()) {
            String id = base.substring(MythicMobsHook.getIdentifier().length());
            return BaseItemDecoratorImpl.mythicmobs(id, setDefaultName);
        } else if (base.startsWith("SKULL:")) {
            String url = base.substring(6);
            return BaseItemDecoratorImpl.skull(url, setDefaultName);
        } else if (base.startsWith("FIREWORK:")) {
            String power = base.substring(9);
            return BaseItemDecoratorImpl.firework(Integer.parseInt(power), setDefaultName);
        } else if (base.startsWith("POTION:")) {
            String[] data = base.substring(7).split(":");
            Material potionType = Material.POTION;
            if (data[0].equalsIgnoreCase("LINGERING")) {
                potionType = Material.LINGERING_POTION;
            } else if (data[0].equalsIgnoreCase("SPLASH")) {
                potionType = Material.SPLASH_POTION;
            }
            if (data[1].equals("NONE")) {
                return BaseItemDecoratorImpl.potion(potionType, data[1], false, false, setDefaultName);
            }
            boolean upgradeable = Boolean.parseBoolean(data[2]);
            boolean extendable = Boolean.parseBoolean(data[3]);
            return BaseItemDecoratorImpl.potion(potionType, data[1], upgradeable, extendable, setDefaultName);
        } else if (base.startsWith("FISH_BUCKET:")) {
            String[] data = base.substring(12).split(":");

            TropicalFish.Pattern pattern = TropicalFish.Pattern.valueOf(data[0]);
            DyeColor bodyColor = DyeColor.valueOf(data[1]);
            DyeColor patternColor = DyeColor.valueOf(data[2]);

            return BaseItemDecoratorImpl.tropicalFishBucket(pattern, patternColor, bodyColor, setDefaultName);
        } else {
            Material material = Material.matchMaterial(base.toUpperCase());
            if (material == null) {
                return null;
            }
            return BaseItemDecoratorImpl.vanilla(material, setDefaultName);
        }
    }

    public static BaseItemDecorator mmoitems(String type, String id, boolean setDefaultName) {
        BaseItem item = new MMOItemsItem(type, id);
        if (!item.isExist()) {
            return null;
        }
        return new BaseItemDecoratorImpl()
                .setItem(item, setDefaultName);
    }

    public static BaseItemDecorator itemsadder(String namespacedId, boolean setDefaultName) {
        BaseItem item = new ItemsAdderItem(namespacedId);
        if (!item.isExist()) {
            return null;
        }
        return new BaseItemDecoratorImpl()
                .setItem(item, setDefaultName);
    }

    public static BaseItemDecorator mythicmobs(String id, boolean setDefaultName) {
        BaseItem item = new MythicMobsItem(id);
        if (!item.isExist()) {
            return null;
        }
        return new BaseItemDecoratorImpl()
                .setItem(item, setDefaultName);
    }

    public static BaseItemDecorator vanilla(Material material, boolean setDefaultName) {
        BaseItem item = new VanillaItem(material);
        if (!item.isExist()) {
            return null;
        }
        return new BaseItemDecoratorImpl()
                .setItem(item, setDefaultName);
    }

    public static BaseItemDecorator skull(String url, boolean setDefaultName) {
        BaseItem item = new SkullItem(url);
        if (!item.isExist()) {
            return null;
        }
        return new BaseItemDecoratorImpl()
                .setItem(item, setDefaultName);
    }

    public static BaseItemDecorator firework(int power, boolean setDefaultName) {
        BaseItem item = new FireworkItem(power);
        if (!item.isExist()) {
            return null;
        }
        return new BaseItemDecoratorImpl()
                .setItem(item, setDefaultName);
    }

    public static BaseItemDecorator potion(Material potionType, String effectType, boolean upgradeable, boolean extendable, boolean setDefaultName) {
        BaseItem item = new PotionItem(potionType, effectType, upgradeable, extendable);
        if (!item.isExist()) {
            return null;
        }
        return new BaseItemDecoratorImpl()
                .setItem(item, setDefaultName);
    }

    public static BaseItemDecorator tropicalFishBucket(TropicalFish.Pattern pattern, DyeColor patternColor, DyeColor bodyColor, boolean setDefaultName) {
        BaseItem item = new TropicalFishBucketItem(pattern, patternColor, bodyColor);
        if (!item.isExist()) {
            return null;
        }
        return new BaseItemDecoratorImpl()
                .setItem(item, setDefaultName);
    }

    @Override
    public BaseItemDecorator setItem(BaseItem item, boolean setDefaultName) {
        this.item = item;
        if (setDefaultName) {
            setName(item.getDisplayName());
        }
        return this;
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
    protected String getNameFormat() {
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
    public BaseItemDecorator setPatternsData(List<String> patternsData) {
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
    public BaseItemDecorator setPeriod(long period) {
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
    public Item buildProductIcon(@Nullable Player player, @NotNull String shopId, @NotNull Product product) {
        ProductFactory productFactory = DailyShop.PRODUCT_FACTORY;
        Item icon = new NormalIcon() {
            @Override
            public ItemProvider getItemProvider() {
                Shop shop = DailyShop.SHOP_FACTORY.getShop(shopId);
                setNameFormat(ShopConfig.getProductNameFormat(shopId));
                setLoreFormat(ShopConfig.getProductLoreFormat(shopId));
                setBundleContentsLineFormat(ShopConfig.getBundleContentsLineFormat(shopId));

                // 处理捆绑包商品的列表 lore
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
                // 关于产品自身的的变量
                Map<String, String> vars = new HashMap<>() {{
                    put("name", getName());
                    put("amount", String.valueOf(getAmount()));
                    put("buy-price", shopPricer.getBuyPrice(product.getId()) != -1d ? MessageConfig.format_decimal.format(shopPricer.getBuyPrice(product.getId())) : ShopConfig.getDisabledPrice(shopId));
                    put("sell-price", shopPricer.getSellPrice(product.getId()) != -1d ? MessageConfig.format_decimal.format(shopPricer.getSellPrice(product.getId())) : ShopConfig.getDisabledPrice(shopId));
                    put("current-global-stock", String.valueOf(product.getProductStock().getCurrentGlobalAmount()));
                    put("initial-global-stock", String.valueOf(product.getProductStock().getInitialGlobalAmount()));
                    put("current-player-stock", String.valueOf(player == null ? -1 : product.getProductStock().getCurrentPlayerAmount(player.getUniqueId())));
                    put("initial-player-stock", String.valueOf(product.getProductStock().getInitialPlayerAmount()));
                    put("rarity", product.getRarity().getName());
                }};
                // 列表行的变量
                Map<String, List<String>> listVars = new HashMap<>() {{
                    put("desc-lore", getLore());
                    put("bundle-contents", bundleContentsLore);
                }};

                return new ItemBuilder(
                        new cn.encmys.ykdz.forest.dailyshop.api.utils.ItemBuilder(getItem().build(null))
                                .setCustomModelData(getCustomModelData())
                                .setItemFlags(getItemFlags())
                                .setLore(TextUtils.parseVar(getLoreFormat(), null, listVars, vars))
                                .setDisplayName(TextUtils.parseVar(getNameFormat(), null, vars))
                                .setBannerPatterns(getPatternsData())
                                .setFireworkEffects(getFireworkEffectData())
                                .build(getAmount()));
            }

            @Override
            public void handleClick(@NotNull ClickType clickType, @NotNull Player player, @NotNull InventoryClickEvent event) {
                Shop shop = DailyShop.SHOP_FACTORY.getShop(shopId);
                ShopPricer shopPricer = shop.getShopPricer();
                ShopCashier shopCashier = shop.getShopCashier();
                Map<String, String> vars = new HashMap<>() {{
                    put("name", getName());
                    put("amount", String.valueOf(getAmount()));
                    put("shop", DailyShop.SHOP_FACTORY.getShop(shopId).getName());
                    put("cost", MessageConfig.format_decimal.format(shopPricer.getBuyPrice(product.getId())));
                    put("earn", MessageConfig.format_decimal.format(shopPricer.getSellPrice(product.getId())));
                }};

                // 玩家从商店购买商品
                if (clickType == ClickType.LEFT) {
                    SettlementResult result = shopCashier.settle(DailyShop.SHOP_ORDER_BUILDER.sellToOrder(player)
                            .addProduct(product, 1));
                    if (result != SettlementResult.SUCCESS) {
                        switch (result) {
                            case TRANSITION_DISABLED -> DailyShop.ADVENTURE_MANAGER.sendMessageWithPrefix(player, TextUtils.decorateTextInMiniMessage(MessageConfig.messages_action_buy_failure_disable, player, vars));
                            case NOT_ENOUGH_MONEY -> DailyShop.ADVENTURE_MANAGER.sendMessageWithPrefix(player, TextUtils.decorateTextInMiniMessage(MessageConfig.messages_action_buy_failure_money, player, vars));
                            case NOT_ENOUGH_GLOBAL_STOCK -> DailyShop.ADVENTURE_MANAGER.sendMessageWithPrefix(player, TextUtils.decorateTextInMiniMessage(MessageConfig.messages_action_buy_failure_stock_global, player, vars));
                            case NOT_ENOUGH_PLAYER_STOCK -> DailyShop.ADVENTURE_MANAGER.sendMessageWithPrefix(player, TextUtils.decorateTextInMiniMessage(MessageConfig.messages_action_buy_failure_stock_player, player, vars));
                            case NOT_ENOUGH_INVENTORY_SPACE -> DailyShop.ADVENTURE_MANAGER.sendMessageWithPrefix(player, TextUtils.decorateTextInMiniMessage(MessageConfig.messages_action_buy_failure_inventory_space, player, vars));
                        }
                        return;
                    } else {
                        DailyShop.ADVENTURE_MANAGER.sendMessageWithPrefix(player, TextUtils.decorateTextInMiniMessage(MessageConfig.messages_action_buy_success, player, vars));
                        player.playSound(player.getLocation(), ShopConfig.getBuySound(shopId), 1f, 1f);
                    }
                }
                // 玩家向商店出售商品
                else if (clickType == ClickType.RIGHT) {
                    SettlementResult result = shopCashier.settle(DailyShop.SHOP_ORDER_BUILDER.buyFromOrder(player)
                            .addProduct(product, 1));
                    if (result != SettlementResult.SUCCESS) {
                        switch (result) {
                            case TRANSITION_DISABLED -> DailyShop.ADVENTURE_MANAGER.sendMessageWithPrefix(player, TextUtils.decorateTextInMiniMessage(MessageConfig.messages_action_sell_failure_disable, player, vars));
                            case NOT_ENOUGH_PRODUCT -> DailyShop.ADVENTURE_MANAGER.sendMessageWithPrefix(player, TextUtils.decorateTextInMiniMessage(MessageConfig.messages_action_sell_failure_notEnough, player, vars));
                        }
                        return;
                    }
                    DailyShop.ADVENTURE_MANAGER.sendMessageWithPrefix(player, TextUtils.decorateTextInMiniMessage(MessageConfig.messages_action_sell_success, player, vars));
                    player.playSound(player.getLocation(), ShopConfig.getSellSound(shopId), 1f, 1f);
                }
                // 玩家向商店出售背包内全部商品
                else if (clickType == ClickType.SHIFT_RIGHT) {
                    ShopOrder order = DailyShop.SHOP_ORDER_BUILDER.buyAllFromOrder(player)
                            .addProduct(product, 1);
                    SettlementResult result = shopCashier.settle(order);
                    if (result != SettlementResult.SUCCESS) {
                        switch (result) {
                            case NOT_ENOUGH_PRODUCT -> DailyShop.ADVENTURE_MANAGER.sendMessageWithPrefix(player, TextUtils.decorateTextInMiniMessage(MessageConfig.messages_action_sellAll_failure_notEnough, player, vars));
                            case TRANSITION_DISABLED -> DailyShop.ADVENTURE_MANAGER.sendMessageWithPrefix(player, TextUtils.decorateTextInMiniMessage(MessageConfig.messages_action_sellAll_failure_disable, player, vars));
                        }
                        return;
                    }
                    int stack = order.getTotalStack();
                    vars.put("earn", MessageConfig.format_decimal.format(shopPricer.getSellPrice(product.getId()) * stack));
                    vars.put("stack", String.valueOf(stack));
                    DailyShop.ADVENTURE_MANAGER.sendMessageWithPrefix(player, TextUtils.decorateTextInMiniMessage(MessageConfig.messages_action_sellAll_success, player, vars));
                    player.playSound(player.getLocation(), ShopConfig.getSellSound(shopId), 1f, 1f);
                }

                notifyWindows();
            }
        };
        // 根据需求设置是否自动刷新
        if (product.getProductStock().isPlayerStock() || product.getProductStock().isGlobalStock()) {
            ((Icon) icon).startUpdater(Config.period_updateProductIcon);
        }
        return icon;
    }

    @Override
    public Item buildNormalIcon() {
        Item icon;

        if (getScroll() == 0) {
            icon = new NormalIcon() {
                @Override
                public ItemProvider getItemProvider() {
                    return new ItemBuilder(
                            new cn.encmys.ykdz.forest.dailyshop.api.utils.ItemBuilder(getItem().build(null))
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
                            new cn.encmys.ykdz.forest.dailyshop.api.utils.ItemBuilder(getItem().build(null))
                                    .setCustomModelData(getCustomModelData())
                                    .setItemFlags(getItemFlags())
                                    .setLore(TextUtils.parseVar(getLore(), null, vars))
                                    .setDisplayName(TextUtils.parseVar(getName(), null, vars))
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

    @Override
    public ItemStack buildProductItem(@Nullable Player player) {
        return new cn.encmys.ykdz.forest.dailyshop.api.utils.ItemBuilder(getItem().build(player))
                .setDisplayName(TextUtils.decorateText(getName(), player))
                .setLore(TextUtils.decorateText(getLore(), player))
                .setItemFlags(getItemFlags())
                .setCustomModelData(getCustomModelData())
                .setBannerPatterns(getPatternsData())
                .setFireworkEffects(getFireworkEffectData())
                .build(getAmount());
    }
}