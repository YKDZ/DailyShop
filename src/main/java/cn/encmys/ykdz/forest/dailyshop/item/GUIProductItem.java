package cn.encmys.ykdz.forest.dailyshop.item;

import cn.encmys.ykdz.forest.dailyshop.DailyShop;
import cn.encmys.ykdz.forest.dailyshop.adventure.AdventureManager;
import cn.encmys.ykdz.forest.dailyshop.api.product.Product;
import cn.encmys.ykdz.forest.dailyshop.config.Config;
import cn.encmys.ykdz.forest.dailyshop.config.ShopConfig;
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
        DecimalFormat decimalFormat = Config.getDecimalFormat();
        Map<String, String> vars = new HashMap<>() {{
            put("name", product.getDisplayName());
            put("desc-lore", TextUtils.catLines(product.getDescLore()));
            put("buy-price", decimalFormat.format(product.getBuyPriceProvider().getPrice(shopId)));
            put("sell-price", decimalFormat.format(product.getSellPriceProvider().getPrice(shopId)));
            put("rarity", product.getRarity().getName());
        }};
        Component name = adventureManager.getComponentFromMiniMessage(TextUtils.parseVariables(ShopConfig.getProductNameFormat(shopId), vars));
        List<Component> lores = adventureManager.getComponentFromMiniMessage(TextUtils.parseVariables(ShopConfig.getProductLoreFormat(shopId), vars));

        return new ItemBuilder(product.getMaterial())
                .setAmount(product.getAmount())
                .addLoreLines(adventureManager.componentToLegacy(lores).toArray(new String[0]))
                .setDisplayName(adventureManager.componentToLegacy(name));
    }

    @Override
    public void handleClick(@NotNull ClickType clickType, @NotNull Player player, @NotNull InventoryClickEvent event) {
        if (clickType.isLeftClick()) {
            product.sellTo(shopId, player);
        } else if (clickType.isRightClick()) {
            product.buyFrom(shopId, player);
        }

        notifyWindows();
    }
}
