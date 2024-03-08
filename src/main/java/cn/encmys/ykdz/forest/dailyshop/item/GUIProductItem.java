package cn.encmys.ykdz.forest.dailyshop.item;

import cn.encmys.ykdz.forest.dailyshop.api.product.Product;
import cn.encmys.ykdz.forest.dailyshop.config.Config;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.jetbrains.annotations.NotNull;
import xyz.xenondevs.invui.item.ItemProvider;
import xyz.xenondevs.invui.item.builder.ItemBuilder;
import xyz.xenondevs.invui.item.impl.AbstractItem;

import java.util.ArrayList;

public class GUIProductItem extends AbstractItem {
    private final String shopId;
    private final Product product;

    public GUIProductItem(String shopId, Product product) {
        super();
        this.shopId = shopId;
        this.product = product;
    }

    @Override
    public ItemProvider getItemProvider() {
        return new ItemBuilder(product.getMaterial())
                .setDisplayName(product.getDisplayName() == null ? "EMPTY" : product.getDisplayName())
                .setAmount(product.getAmount())
                .addLoreLines(new ArrayList<String>() {{
                    addAll(product.getDescLore());
                    add(" ");
                    add("- Buy price: " + Config.getDecimalFormat().format(product.getBuyPriceProvider().getPrice(shopId)));
                    add("- Sell price: " + Config.getDecimalFormat().format(product.getSellPriceProvider().getPrice(shopId)));
                    add(" ");
                    add("Rarity: " + product.getRarity().getName());
                }}.toArray(new String[0]));
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
