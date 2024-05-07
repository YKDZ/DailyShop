package cn.encmys.ykdz.forest.dailyshop.item;

import cn.encmys.ykdz.forest.dailyshop.api.item.BaseItem;
import cn.encmys.ykdz.forest.dailyshop.api.item.enums.BaseItemType;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.Nullable;
import pers.neige.neigeitems.item.ItemInfo;
import pers.neige.neigeitems.manager.ItemManager;
import pers.neige.neigeitems.utils.ItemUtils;

public class NeigeItemsItem implements BaseItem {
    private final String id;

    public NeigeItemsItem(String id) {
        this.id = id;
    }

    @Override
    public String getDisplayName() {
        return ItemManager.INSTANCE.getItem(getId()).getConfigSection().getString("name");
    }

    @Override
    public boolean isSimilar(ItemStack item) {
        return ItemManager.INSTANCE.isNiItem(item) != null && getId().equals(getIdFromItem(item));
    }

    @Override
    public boolean isExist() {
        return false;
    }

    @Override
    public BaseItemType getItemType() {
        return BaseItemType.NEIGEITEMS;
    }

    @Override
    public ItemStack build(@Nullable Player player) {
        return ItemManager.INSTANCE.getItemStack(id, player);
    }

    public String getId() {
        return id;
    }

    public String getIdFromItem(ItemStack item) {
        ItemInfo itemInfo = ItemUtils.isNiItem(item);
        if (itemInfo != null) {
            return itemInfo.getId();
        }
        return null;
    }
}
