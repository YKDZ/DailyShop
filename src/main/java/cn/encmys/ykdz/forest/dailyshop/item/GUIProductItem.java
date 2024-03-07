package cn.encmys.ykdz.forest.dailyshop.item;

import cn.encmys.ykdz.forest.dailyshop.api.product.Product;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.jetbrains.annotations.NotNull;
import xyz.xenondevs.invui.item.ItemProvider;
import xyz.xenondevs.invui.item.builder.ItemBuilder;
import xyz.xenondevs.invui.item.impl.AbstractItem;

import java.util.ArrayList;

public class GUIProductItem extends AbstractItem {
    private final Product product;

    public GUIProductItem(Product product) {
        super();
        this.product = product;
    }

    @Override
    public ItemProvider getItemProvider() {
        return new ItemBuilder(product.getMaterial())
                .setDisplayName(product.getDisplayName() == null ? "EMPTY" : product.getDisplayName())
                .setAmount(product.getAmount())
                .addLoreLines(product.getDescLore().toArray(new String[0]))
                .addLoreLines(new ArrayList<String>() {{
                    add("- Buy price: " + product.getBuyPriceProvider().getPrice());
                    add("- Sell price: " + product.getSellPriceProvider().getPrice());
                    add(" ");
                    add("Rarity: " + product.getRarity().getName());
                }}.toArray(new String[0]));
    }

    @Override
    public void handleClick(@NotNull ClickType clickType, @NotNull Player player, @NotNull InventoryClickEvent event) {
        if (clickType.isLeftClick()) {
            product.sellTo(player);
        } else if (clickType.isRightClick()) {
            product.buyFrom(player);
        }

        notifyWindows();
    }
}
