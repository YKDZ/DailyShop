package cn.encmys.ykdz.forest.dailyshop.item;

import cn.encmys.ykdz.forest.dailyshop.api.item.BaseItem;
import cn.encmys.ykdz.forest.dailyshop.item.enums.BaseItemType;
import net.momirealms.customfishing.api.CustomFishingPlugin;
import net.momirealms.customfishing.api.manager.ItemManager;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.Nullable;

public class CustomFishingItem implements BaseItem {
    private final String namespace;
    private final String id;

    public CustomFishingItem(String namespace, String id) {
        this.namespace = namespace;
        this.id = id;
    }

    @Override
    public String getDisplayName() {
        return build(null).getItemMeta().getDisplayName();
    }

    @Override
    public boolean isSimilar(ItemStack item) {
        ItemManager manager = CustomFishingPlugin.get().getItemManager();
        return manager.isCustomFishingItem(item) && manager.getCustomFishingItemID(item).equals(getId());
    }

    @Override
    public BaseItemType getItemType() {
        return BaseItemType.CUSTOMFISHING;
    }

    @Override
    public ItemStack build(@Nullable Player player) {
        return CustomFishingPlugin.get().getItemManager().build(player, getNamespace(), getId());
    }

    public String getNamespace() {
        return namespace;
    }

    public String getId() {
        return id;
    }
}
