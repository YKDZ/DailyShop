package cn.encmys.ykdz.forest.dailyshop.builder;

import cn.encmys.ykdz.forest.dailyshop.DailyShop;
import cn.encmys.ykdz.forest.dailyshop.adventure.AdventureManager;
import cn.encmys.ykdz.forest.dailyshop.api.product.Product;
import cn.encmys.ykdz.forest.dailyshop.config.Config;
import cn.encmys.ykdz.forest.dailyshop.config.MessageConfig;
import cn.encmys.ykdz.forest.dailyshop.config.ShopConfig;
import cn.encmys.ykdz.forest.dailyshop.factory.ProductFactory;
import cn.encmys.ykdz.forest.dailyshop.util.TextUtils;
import me.rubix327.itemslangapi.ItemsLangAPI;
import net.kyori.adventure.text.Component;
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
    private static final ItemsLangAPI itemsLangAPI = DailyShop.getItemsLangAPI();
    private Material material;
    private String name;
    private List<String> descLore;
    private List<String> loreFormat;
    private int amount;
    private List<String> itemFLags;
    private String nameFormat;
    private String bundleContentsLineFormat;

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

    public List<String> getItemFLags() {
        return itemFLags;
    }

    public void setItemFLags(List<String> itemFLags) {
        this.itemFLags = itemFLags;
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
                setNameFormat(ShopConfig.getProductNameFormat(shopId));
                setLoreFormat(ShopConfig.getProductLoreFormat(shopId));
                setBundleContentsLineFormat(ShopConfig.getBundleContentsLineFormat(shopId));

                // Handle lore for bundle contents
                List<String> bundleContents = product.getBundleContents();
                List<String> bundleContentsLore = new ArrayList<>();
                if (bundleContents != null && !bundleContents.isEmpty()) {
                    for (String contentId : product.getBundleContents()) {
                        Product content = productFactory.getProduct(contentId);
                        bundleContentsLore.add(TextUtils.parseInternalVariables(getBundleContentsLineFormat(), new HashMap<>() {{
                            put("name", content.getIconBuilder().getName());
                            put("amount", String.valueOf(content.getProductItemBuilder().getAmount()));
                        }}));
                    }
                }

                // Vars for the product itself
                Map<String, String> vars = new HashMap<>() {{
                    put("name", getName());
                    put("amount", String.valueOf(getAmount()));
                    put("buy-price", decimalFormat.format(product.getBuyPriceProvider().getPrice(shopId)));
                    put("sell-price", decimalFormat.format(product.getSellPriceProvider().getPrice(shopId)));
                    put("rarity", product.getRarity().getName());
                }};

                Component name = adventureManager.getComponentFromMiniMessage(TextUtils.parseInternalVariables(getNameFormat(), vars));

                List<Component> lores = adventureManager.getComponentFromMiniMessage(TextUtils.insertListInternalVariables(TextUtils.parseInternalVariables(getLoreFormat(), vars), new HashMap<>() {{
                    put("desc-lore", getDescLore());
                    put("bundle-contents", bundleContentsLore);
                }}));

                return new ItemBuilder(getMaterial())
                        .setAmount(getAmount())
                        .addLoreLines(adventureManager.componentToLegacy(lores).toArray(new String[0]))
                        .setDisplayName(adventureManager.componentToLegacy(name));
            }

            @Override
            public void handleClick(@NotNull ClickType clickType, @NotNull Player player, @NotNull InventoryClickEvent event) {
                HashMap<String, String> vars = new HashMap<>() {{
                    put("name", getName());
                    put("amount", String.valueOf(getAmount()));
                    put("shop", DailyShop.getShopFactory().getShop(shopId).getName());
                    put("money", String.valueOf(product.getBuyPriceProvider().getPrice(shopId)));
                }};

                if (clickType == ClickType.LEFT) {
                    if (!product.sellTo(shopId, player)) {
                        adventureManager.sendMessageWithPrefix(player, TextUtils.parseInternalVariables(MessageConfig.messages_action_buy_failure, vars));
                        return;
                    }
                    adventureManager.sendMessageWithPrefix(player, TextUtils.parseInternalVariables(MessageConfig.messages_action_buy_success, vars));
                    player.playSound(player, ShopConfig.getBuySound(shopId), 1f, 1f);
                } else if (clickType == ClickType.RIGHT) {
                    if (!product.buyFrom(shopId, player)) {
                        adventureManager.sendMessageWithPrefix(player, TextUtils.parseInternalVariables(MessageConfig.messages_action_sell_failure, vars));
                        return;
                    }
                    adventureManager.sendMessageWithPrefix(player, TextUtils.parseInternalVariables(MessageConfig.messages_action_sell_success, vars));
                    player.playSound(player, ShopConfig.getSellSound(shopId), 1f, 1f);
                } else if (clickType == ClickType.SHIFT_RIGHT) {
                    adventureManager.sendMessageWithPrefix(player, TextUtils.parseInternalVariables(MessageConfig.messages_action_sellAll_failure, vars));
                    if (!product.buyAllFrom(shopId, player)) {
                        return;
                    }
                    adventureManager.sendMessageWithPrefix(player, TextUtils.parseInternalVariables(MessageConfig.messages_action_sellAll_success, vars));
                    player.playSound(player, ShopConfig.getSellSound(shopId), 1f, 1f);
                }

                notifyWindows();
            }
        };
    }
}

//    @Override
//    public ItemProvider getItemProvider() {
//        ProductFactory productFactory = DailyShop.getProductFactory();
//        DecimalFormat decimalFormat = Config.getDecimalFormat();
//
//        List<String> bundleContentsLore = new ArrayList<>();
//        for (String contentId : product.getBundleContents()) {
//            Product content = productFactory.getProduct(contentId);
//            bundleContentsLore.add(TextUtils.parseVariables(ShopConfig.getBundleContentsLineFormat(shopId), new HashMap<>() {{
//                put("name", content.getDisplayName());
//                put("amount", String.valueOf(content.getAmount()));
//            }}));
//        }
//
//        Map<String, String> vars = new HashMap<>() {{
//            put("name", product.getDisplayName());
//            put("amount", String.valueOf(product.getAmount()));
//            put("buy-price", decimalFormat.format(product.getBuyPriceProvider().getPrice(shopId)));
//            put("sell-price", decimalFormat.format(product.getSellPriceProvider().getPrice(shopId)));
//            put("rarity", product.getRarity().getName());
//        }};
//
//        Component name = adventureManager.getComponentFromMiniMessage(TextUtils.parseVariables(ShopConfig.getProductNameFormat(shopId), vars));
//
//        List<Component> lores = adventureManager.getComponentFromMiniMessage(TextUtils.insertListVariables(TextUtils.parseVariables(ShopConfig.getProductLoreFormat(shopId), vars), new HashMap<>() {{
//            put("desc-lore", product.getDescLore());
//            put("bundle-contents", bundleContentsLore);
//        }}));
//
//        return new ItemBuilder(product.getMaterial())
//                .setAmount(product.getAmount())
//                .addLoreLines(adventureManager.componentToLegacy(lores).toArray(new String[0]))
//                .setDisplayName(adventureManager.componentToLegacy(name));
//    }
//
//    @Override
//    public void handleClick(@NotNull ClickType clickType, @NotNull Player player, @NotNull InventoryClickEvent event) {
//        HashMap<String, String> vars = new HashMap<>() {{
//            put("name", product.getDisplayName());
//            put("amount", String.valueOf(product.getAmount()));
//            put("shop", DailyShop.getShopFactory().getShop(shopId).getName());
//            put("money", String.valueOf(product.getBuyPriceProvider().getPrice(shopId)));
//        }};
//
//        if (clickType == ClickType.LEFT) {
//            product.sellTo(shopId, player);
//            adventureManager.sendMessageWithPrefix(player, TextUtils.parseVariables(MessageConfig.messages_action_buy, vars));
//            player.playSound(player, ShopConfig.getBuySound(shopId), 1f, 1f);
//        } else if (clickType == ClickType.RIGHT) {
//            product.buyFrom(shopId, player);
//            adventureManager.sendMessageWithPrefix(player, TextUtils.parseVariables(MessageConfig.messages_action_sell, vars));
//            player.playSound(player, ShopConfig.getSellSound(shopId), 1f, 1f);
//        } else if (clickType == ClickType.SHIFT_RIGHT) {
//            product.buyAllFrom(shopId, player);
//            adventureManager.sendMessageWithPrefix(player, TextUtils.parseVariables(MessageConfig.messages_action_sellAll, vars));
//            player.playSound(player, ShopConfig.getSellSound(shopId), 1f, 1f);
//        }
//
//        notifyWindows();
//    }
