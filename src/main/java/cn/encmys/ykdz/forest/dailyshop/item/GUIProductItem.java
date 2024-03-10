package cn.encmys.ykdz.forest.dailyshop.item;

import cn.encmys.ykdz.forest.dailyshop.DailyShop;
import cn.encmys.ykdz.forest.dailyshop.adventure.AdventureManager;
import cn.encmys.ykdz.forest.dailyshop.api.product.Product;
import cn.encmys.ykdz.forest.dailyshop.config.Config;
import cn.encmys.ykdz.forest.dailyshop.config.MessageConfig;
import cn.encmys.ykdz.forest.dailyshop.config.ShopConfig;
import cn.encmys.ykdz.forest.dailyshop.factory.ProductFactory;
import cn.encmys.ykdz.forest.dailyshop.util.TextUtils;
import net.kyori.adventure.text.Component;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.jetbrains.annotations.NotNull;
import xyz.xenondevs.invui.item.ItemProvider;
import xyz.xenondevs.invui.item.builder.ItemBuilder;
import xyz.xenondevs.invui.item.impl.AbstractItem;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class GUIProductItem extends AbstractItem {
    private static final AdventureManager adventureManager = DailyShop.getAdventureManager();
    private final String shopId;
    private final Product product;

    public GUIProductItem(String shopId, Product product) {
        super();
        this.shopId = shopId;
        this.product = product;
    }

    @Override
    public ItemProvider getItemProvider() {
        ProductFactory productFactory = DailyShop.getProductFactory();
        DecimalFormat decimalFormat = Config.getDecimalFormat();

        List<String> bundleContentsLore = new ArrayList<>();
        for (String contentId : product.getBundleContents()) {
            Product content = productFactory.getProduct(contentId);
            bundleContentsLore.add(TextUtils.parseVariables(ShopConfig.getBundleContentsLineFormat(shopId), new HashMap<>() {{
                put("name", content.getDisplayName());
                put("amount", String.valueOf(content.getAmount()));
            }}));
        }

        Map<String, String> vars = new HashMap<>() {{
            put("name", product.getDisplayName());
            put("amount", String.valueOf(product.getAmount()));
            put("buy-price", decimalFormat.format(product.getBuyPriceProvider().getPrice(shopId)));
            put("sell-price", decimalFormat.format(product.getSellPriceProvider().getPrice(shopId)));
            put("rarity", product.getRarity().getName());
        }};

        Component name = adventureManager.getComponentFromMiniMessage(TextUtils.parseVariables(ShopConfig.getProductNameFormat(shopId), vars));

        List<Component> lores = adventureManager.getComponentFromMiniMessage(TextUtils.insertListVariables(TextUtils.parseVariables(ShopConfig.getProductLoreFormat(shopId), vars), new HashMap<>() {{
            put("desc-lore", product.getDescLore());
            put("bundle-contents", bundleContentsLore);
        }}));

        return new ItemBuilder(product.getMaterial())
                .setAmount(product.getAmount())
                .addLoreLines(adventureManager.componentToLegacy(lores).toArray(new String[0]))
                .setDisplayName(adventureManager.componentToLegacy(name));
    }

    @Override
    public void handleClick(@NotNull ClickType clickType, @NotNull Player player, @NotNull InventoryClickEvent event) {
        HashMap<String, String> vars = new HashMap<>() {{
            put("name", product.getDisplayName());
            put("amount", String.valueOf(product.getAmount()));
            put("shop", DailyShop.getShopFactory().getShop(shopId).getName());
            put("money", String.valueOf(product.getBuyPriceProvider().getPrice(shopId)));
        }};

        if (clickType == ClickType.LEFT) {
            product.sellTo(shopId, player);
            adventureManager.sendMessageWithPrefix(player, TextUtils.parseVariables(MessageConfig.messages_action_buy, vars));
            player.playSound(player, ShopConfig.getBuySound(shopId), 1f, 1f);
        } else if (clickType == ClickType.RIGHT) {
            product.buyFrom(shopId, player);
            adventureManager.sendMessageWithPrefix(player, TextUtils.parseVariables(MessageConfig.messages_action_sell, vars));
            player.playSound(player, ShopConfig.getSellSound(shopId), 1f, 1f);
        } else if (clickType == ClickType.SHIFT_RIGHT) {
            product.buyAllFrom(shopId, player);
            adventureManager.sendMessageWithPrefix(player, TextUtils.parseVariables(MessageConfig.messages_action_sellAll, vars));
            player.playSound(player, ShopConfig.getSellSound(shopId), 1f, 1f);
        }

        notifyWindows();
    }
}
