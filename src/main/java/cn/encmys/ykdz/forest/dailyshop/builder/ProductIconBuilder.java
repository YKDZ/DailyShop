package cn.encmys.ykdz.forest.dailyshop.builder;

import cn.encmys.ykdz.forest.dailyshop.DailyShop;
import cn.encmys.ykdz.forest.dailyshop.adventure.AdventureManager;
import cn.encmys.ykdz.forest.dailyshop.api.product.Product;
import cn.encmys.ykdz.forest.dailyshop.config.Config;
import cn.encmys.ykdz.forest.dailyshop.config.MessageConfig;
import cn.encmys.ykdz.forest.dailyshop.config.ShopConfig;
import cn.encmys.ykdz.forest.dailyshop.factory.ProductFactory;
import cn.encmys.ykdz.forest.dailyshop.hook.MMOItemsHook;
import cn.encmys.ykdz.forest.dailyshop.product.BundleProduct;
import cn.encmys.ykdz.forest.dailyshop.shop.Shop;
import cn.encmys.ykdz.forest.dailyshop.util.TextUtils;
import me.rubix327.itemslangapi.ItemsLangAPI;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;
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
    private static final ItemsLangAPI itemsLangAPI = DailyShop.getItemsLangAPI();
    private ItemStack base;
    private Material material;
    private String name;
    private List<String> descLore;
    private List<String> loreFormat;
    private int amount;
    private List<String> itemFlags;
    private String nameFormat;
    private String bundleContentsLineFormat;

    private ProductIconBuilder() {
    }

    public static ProductIconBuilder mmoitems(String type, String id) {
        ItemStack item = MMOItemsHook.buildItem(type, id);
        return new ProductIconBuilder()
                .setBase(item);
    }

    public static ProductIconBuilder vanilla(Material material) {
        return new ProductIconBuilder()
                .setMaterial(material);
    }

    public ProductIconBuilder setBase(ItemStack base) {
        this.base = base;
        return this;
    }

    public ItemStack getBase() {
        return base;
    }

    public Material getMaterial() {
        return material;
    }

    public ProductIconBuilder setMaterial(Material material) {
        this.material = material;
        return this;
    }

    public ProductIconBuilder setName(String name) {
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
        return name == null ? itemsLangAPI.translate(material, Config.language) : name;
    }

    public List<String> getItemFlags() {
        return itemFlags;
    }

    public void setItemFlags(List<String> itemFLags) {
        this.itemFlags = itemFLags;
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

    private List<String> getDescLore() {
        return descLore;
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
                            bundleContentsLore.add(TextUtils.parseInternalVariables(getBundleContentsLineFormat(), new HashMap<>() {{
                                put("name", adventureManager.legacyToMiniMessage(content.getIconBuilder().getName()));
                                put("amount", String.valueOf(content.getProductItemBuilder().getAmount()));
                            }}));
                        }
                    }
                }

                // Vars for the product itself
                Map<String, String> vars = new HashMap<>() {{
                    put("name", adventureManager.legacyToMiniMessage(getName()));
                    put("amount", String.valueOf(getAmount()));
                    put("buy-price", decimalFormat.format(shop.getBuyPrice(product.getId())));
                    put("sell-price", decimalFormat.format(shop.getSellPrice(product.getId())));
                    put("rarity", product.getRarity().getName());
                }};

                String name = TextUtils.decorateText(TextUtils.parseInternalVariables(getNameFormat(), vars), null);

                List<String> lores = TextUtils.decorateText(TextUtils.insertListInternalVariables(TextUtils.parseInternalVariables(getLoreFormat(), vars), new HashMap<>() {{
                    put("desc-lore", getDescLore());
                    put("bundle-contents", bundleContentsLore);
                }}), null);

                if (base != null) {
                    return new ItemBuilder(getBase())
                            .setAmount(getAmount())
                            .addLoreLines(lores.toArray(new String[0]))
                            .setDisplayName(name);
                }
                return new ItemBuilder(getMaterial())
                        .setAmount(getAmount())
                        .addLoreLines(lores.toArray(new String[0]))
                        .setDisplayName(name);
            }

            @Override
            public void handleClick(@NotNull ClickType clickType, @NotNull Player player, @NotNull InventoryClickEvent event) {
                Shop shop = DailyShop.getShopFactory().getShop(shopId);
                HashMap<String, String> vars = new HashMap<>() {{
                    put("name", adventureManager.legacyToMiniMessage(getName()));
                    put("amount", String.valueOf(getAmount()));
                    put("shop", DailyShop.getShopFactory().getShop(shopId).getName());
                    put("cost", decimalFormat.format(shop.getBuyPrice(product.getId())));
                    put("earn", decimalFormat.format(shop.getSellPrice(product.getId())));
                }};

                if (clickType == ClickType.LEFT) {
                    if (!product.sellTo(shopId, player)) {
                        adventureManager.sendMessageWithPrefix(player, TextUtils.parseInternalVariables(MessageConfig.messages_action_buy_failure, vars));
                        return;
                    }
                    adventureManager.sendMessageWithPrefix(player, TextUtils.parseInternalVariables(MessageConfig.messages_action_buy_success, vars));
                    player.playSound(player.getLocation(), ShopConfig.getBuySound(shopId), 1f, 1f);
                } else if (clickType == ClickType.RIGHT) {
                    if (!product.buyFrom(shopId, player)) {
                        adventureManager.sendMessageWithPrefix(player, TextUtils.parseInternalVariables(MessageConfig.messages_action_sell_failure, vars));
                        return;
                    }
                    adventureManager.sendMessageWithPrefix(player, TextUtils.parseInternalVariables(MessageConfig.messages_action_sell_success, vars));
                    player.playSound(player.getLocation(), ShopConfig.getSellSound(shopId), 1f, 1f);
                } else if (clickType == ClickType.SHIFT_RIGHT) {
                    int stack = product.buyAllFrom(shopId, player);
                    if (stack == 0) {
                        adventureManager.sendMessageWithPrefix(player, TextUtils.parseInternalVariables(MessageConfig.messages_action_sellAll_failure, vars));
                        return;
                    }
                    vars.put("earn", decimalFormat.format(shop.getSellPrice(product.getId()) * stack));
                    vars.put("stack", String.valueOf(stack));
                    adventureManager.sendMessageWithPrefix(player, TextUtils.parseInternalVariables(MessageConfig.messages_action_sellAll_success, vars));
                    player.playSound(player.getLocation(), ShopConfig.getSellSound(shopId), 1f, 1f);
                }

                // Not needed before Total market volume Feature
                // notifyWindows();
            }
        };
    }
}