package cn.encmys.ykdz.forest.dailyshop.item;

import cn.encmys.ykdz.forest.dailyshop.api.item.BaseItem;
import cn.encmys.ykdz.forest.dailyshop.api.item.enums.BaseItemType;
import net.momirealms.customcrops.api.CustomCropsPlugin;
import net.momirealms.customcrops.api.manager.ItemManager;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.Nullable;

public class CustomCropsItem implements BaseItem {
    private final String id;

    public CustomCropsItem(String id) {
        this.id = id;
    }

    @Override
    public String getDisplayName() {
        return build(null).getItemMeta().getDisplayName();
    }

    @Override
    public boolean isSimilar(ItemStack item) {
        ItemManager manager = CustomCropsPlugin.get().getItemManager();
        return manager.getItemID(item).equals(getId());
    }

    @Override
    public boolean isExist() {
        return true;
    }

    @Override
    public BaseItemType getItemType() {
        return BaseItemType.CUSTOMCROPS;
    }

    @Override
    public ItemStack build(@Nullable Player player) {
        return CustomCropsPlugin.get().getItemManager().getItemStack(player, getId());
    }

    public String getId() {
        return id;
    }
}
