package cn.encmys.ykdz.forest.dailyshop.builder;

import cn.encmys.ykdz.forest.dailyshop.DailyShop;
import cn.encmys.ykdz.forest.dailyshop.adventure.AdventureManager;
import cn.encmys.ykdz.forest.dailyshop.api.item.ProductItem;
import cn.encmys.ykdz.forest.dailyshop.api.product.Product;
import cn.encmys.ykdz.forest.dailyshop.config.Config;
import cn.encmys.ykdz.forest.dailyshop.config.MessageConfig;
import cn.encmys.ykdz.forest.dailyshop.config.ShopConfig;
import cn.encmys.ykdz.forest.dailyshop.factory.ProductFactory;
import cn.encmys.ykdz.forest.dailyshop.hook.ItemsAdderHook;
import cn.encmys.ykdz.forest.dailyshop.hook.MMOItemsHook;
import cn.encmys.ykdz.forest.dailyshop.hook.OraxenHook;
import cn.encmys.ykdz.forest.dailyshop.item.ItemsAdderItem;
import cn.encmys.ykdz.forest.dailyshop.item.MMOItemsItem;
import cn.encmys.ykdz.forest.dailyshop.item.OraxenItem;
import cn.encmys.ykdz.forest.dailyshop.item.VanillaItem;
import cn.encmys.ykdz.forest.dailyshop.product.BundleProduct;
import cn.encmys.ykdz.forest.dailyshop.product.enums.FailureReason;
import cn.encmys.ykdz.forest.dailyshop.shop.Shop;
import cn.encmys.ykdz.forest.dailyshop.util.TextUtils;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.jetbrains.annotations.NotNull;
import xyz.xenondevs.invui.item.Item;
import xyz.xenondevs.invui.item.ItemProvider;
import xyz.xenondevs.invui.item.builder.ItemBuilder;
import xyz.xenondevs.invui.item.impl.AbstractItem;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ProductIconBuilder {
    private static final DecimalFormat decimalFormat = Config.getDecimalFormat();
    private static final AdventureManager adventureManager = DailyShop.getAdventureManager();
    private ProductItem item;
    private String name;
    private List<String> descLore;
    private List<String> loreFormat;
    private int amount;
    private List<String> itemFlags;
    private String nameFormat;
    private String bundleContentsLineFormat;
    private Integer customModelData;

    private ProductIconBuilder() {
    }

    public static ProductIconBuilder get(String item) {
        if (item.startsWith("MI:") && MMOItemsHook.isHooked()) {
            String[] typeId = item.substring(3).split(":");
            String type = typeId[0];
            String id = typeId[1];
            return ProductIconBuilder.mmoitems(type, id);
        } else if (item.startsWith("IA:") && ItemsAdderHook.isHooked()) {
            String namespacedId = item.substring(3);
            return ProductIconBuilder.itemsadder(namespacedId);
        } else if (item.startsWith("OXN:") && OraxenHook.isHooked()) {
            String id = item.substring(3);
            return ProductIconBuilder.oraxen(id);
        } else {
            Material material = Material.valueOf(item);
            return ProductIconBuilder.vanilla(material);
        }
    }

    public static ProductIconBuilder mmoitems(String type, String id) {
        ProductItem item = new MMOItemsItem(type, id);
        return new ProductIconBuilder()
                .setItem(item)
                .setName(item.getDisplayName());
    }

    public static ProductIconBuilder itemsadder(String namespacedId) {
        ProductItem item = new ItemsAdderItem(namespacedId);
        return new ProductIconBuilder()
                .setItem(item)
                .setName(item.getDisplayName());
    }

    public static ProductIconBuilder oraxen(String id) {
        ProductItem item = new OraxenItem(id);
        return new ProductIconBuilder()
                .setItem(item)
                .setName(item.getDisplayName());
    }

    public static ProductIconBuilder vanilla(Material material) {
        ProductItem item = new VanillaItem(material);
        return new ProductIconBuilder()
                .setItem(new VanillaItem(material))
                .setName(item.getDisplayName());
    }

    public ProductIconBuilder setItem(ProductItem item) {
        this.item = item;
        return this;
    }

    public ProductItem getItem() {
        return item;
    }

    public ProductIconBuilder setName(String name) {
        if (name == null) {
            return this;
        }
        this.name = name;
        return this;
    }

    public ProductIconBuilder setDescLore(List<String> descLore) {
        this.descLore = descLore;
        return this;
    }

    public ProductIconBuilder setLoreFormat(List<String> loreFormat) {
        this.loreFormat = loreFormat;
        return this;
    }

    public ProductIconBuilder setNameFormat(String nameFormat) {
        this.nameFormat = nameFormat;
        return this;
    }

    public ProductIconBuilder setBundleContentsLineFormat(String bundleContentsLineFormat) {
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

    public ProductIconBuilder setItemFlags(List<String> itemFlags) {
        this.itemFlags = itemFlags;
        return this;
    }

    public ProductIconBuilder setAmount(int amount) {
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

    public List<String> getDescLore() {
        return descLore;
    }

    public Integer getCustomModelData() {
        return customModelData;
    }

    public ProductIconBuilder setCustomModelData(Integer customModelData) {
        this.customModelData = customModelData;
        return this;
    }

    public Item build(String shopId, Product product) {
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
                    List<String> bundleContents = ((BundleProduct) product).getBundleContents();
                    if (bundleContents != null && !bundleContents.isEmpty()) {
                        for (String contentId : bundleContents) {
                            Product content = productFactory.getProduct(contentId);
                            bundleContentsLore.add(TextUtils.decorateTextInMiniMessage(getBundleContentsLineFormat(), null, new HashMap<>() {{
                                put("name", content.getProductIconBuilder().getName());
                                put("amount", String.valueOf(content.getProductItemBuilder().getAmount()));
                            }}));
                        }
                    }
                }

                // Vars for the product itself
                Map<String, String> vars = new HashMap<>() {{
                    put("name", getName());
                    put("amount", String.valueOf(getAmount()));
                    put("buy-price", shop.getBuyPrice(product.getId()) != -1d ? decimalFormat.format(shop.getBuyPrice(product.getId())) : null);
                    put("sell-price", shop.getSellPrice(product.getId()) != -1d ? decimalFormat.format(shop.getSellPrice(product.getId())) : null);
                    put("rarity", product.getRarity().getName());
                }};

                Map<String, List<String>> listVars = new HashMap<>() {{
                    put("desc-lore", getDescLore());
                    put("bundle-contents", bundleContentsLore);
                }};

                return new ItemBuilder(
                        new cn.encmys.ykdz.forest.dailyshop.util.ItemBuilder(getItem().build(null))
                                .setCustomModelData(getCustomModelData())
                                .setItemFlags(getItemFlags())
                                .setLore(TextUtils.decorateTextWithListVar(getLoreFormat(), null, listVars, vars))
                                .setDisplayName(TextUtils.decorateTextWithVar(getNameFormat(), null, vars))
                                .build(getAmount()));
            }

            @Override
            public void handleClick(@NotNull ClickType clickType, @NotNull Player player, @NotNull InventoryClickEvent event) {
                Shop shop = DailyShop.getShopFactory().getShop(shopId);
                Map<String, String> vars = new HashMap<>() {{
                    put("name", getName());
                    put("amount", String.valueOf(getAmount()));
                    put("shop", DailyShop.getShopFactory().getShop(shopId).getName());
                    put("cost", decimalFormat.format(shop.getBuyPrice(product.getId())));
                    put("earn", decimalFormat.format(shop.getSellPrice(product.getId())));
                }};

                if (clickType == ClickType.LEFT) {
                    FailureReason failure = product.sellTo(shopId, player);
                    if (failure != FailureReason.SUCCESS) {
                        switch (failure) {
                            case DISABLE -> adventureManager.sendMessageWithPrefix(player, TextUtils.decorateTextInMiniMessage(MessageConfig.messages_action_buy_failure_disable, player, vars));
                            case MONEY -> adventureManager.sendMessageWithPrefix(player, TextUtils.decorateTextInMiniMessage(MessageConfig.messages_action_buy_failure_money, player, vars));
                        }
                        return;
                    }
                    adventureManager.sendMessageWithPrefix(player, TextUtils.decorateTextInMiniMessage(MessageConfig.messages_action_buy_success, player, vars));
                    player.playSound(player.getLocation(), ShopConfig.getBuySound(shopId), 1f, 1f);
                } else if (clickType == ClickType.RIGHT) {
                    FailureReason failure = product.buyFrom(shopId, player);
                    if (failure != FailureReason.SUCCESS) {
                        switch (failure) {
                            case DISABLE -> adventureManager.sendMessageWithPrefix(player, TextUtils.decorateTextInMiniMessage(MessageConfig.messages_action_sell_failure_disable, player, vars));
                            case NOT_ENOUGH -> adventureManager.sendMessageWithPrefix(player, TextUtils.decorateTextInMiniMessage(MessageConfig.messages_action_sell_failure_notEnough, player, vars));
                        }
                        return;
                    }
                    adventureManager.sendMessageWithPrefix(player, TextUtils.decorateTextInMiniMessage(MessageConfig.messages_action_sell_success, player, vars));
                    player.playSound(player.getLocation(), ShopConfig.getSellSound(shopId), 1f, 1f);
                } else if (clickType == ClickType.SHIFT_RIGHT) {
                    if (shop.getSellPrice(product.getId()) == -1d) {
                        adventureManager.sendMessageWithPrefix(player, TextUtils.decorateTextInMiniMessage(MessageConfig.messages_action_sellAll_failure_disable, player, vars));
                        return;
                    }

                    int stack = product.buyAllFrom(shopId, player);
                    if (stack == 0) {
                        adventureManager.sendMessageWithPrefix(player, TextUtils.decorateTextInMiniMessage(MessageConfig.messages_action_sellAll_failure_notEnough, player, vars));
                        return;
                    }
                    vars.put("earn", decimalFormat.format(shop.getSellPrice(product.getId()) * stack));
                    vars.put("stack", String.valueOf(stack));
                    adventureManager.sendMessageWithPrefix(player, TextUtils.decorateTextInMiniMessage(MessageConfig.messages_action_sellAll_success, player, vars));
                    player.playSound(player.getLocation(), ShopConfig.getSellSound(shopId), 1f, 1f);
                }

                // Not needed before total market volume feature
                // notifyWindows();
            }
        };
    }
}