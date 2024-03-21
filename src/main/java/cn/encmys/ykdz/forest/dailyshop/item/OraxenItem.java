package cn.encmys.ykdz.forest.dailyshop.item;

import cn.encmys.ykdz.forest.dailyshop.DailyShop;
import cn.encmys.ykdz.forest.dailyshop.adventure.AdventureManager;
import cn.encmys.ykdz.forest.dailyshop.api.item.ProductItem;
import io.th0rgal.oraxen.api.OraxenItems;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.Nullable;

public class OraxenItem implements ProductItem {
    private static final AdventureManager adventureManager = DailyShop.getAdventureManager();
    private final String id;

    public OraxenItem(String id) {
        this.id = id;
    }

    @Override
    public String getDisplayName() {
        if (OraxenItems.exists(getId())) {
            return adventureManager.legacyToMiniMessage(OraxenItems.getOptionalItemById(getId()).get().getDisplayName());
        } else {
            return null;
        }
    }

    @Override
    public boolean isSimilar(ItemStack item) {
        return OraxenItems.getIdByItem(item).equals(getId());
    }

    @Override
    public ItemStack build(@Nullable Player player) {
        return OraxenItems.getItemById(getId()).build();
    }

    public String getId() {
        return id;
    }
}
