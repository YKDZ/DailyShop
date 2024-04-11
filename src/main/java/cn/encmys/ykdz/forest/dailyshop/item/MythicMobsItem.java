package cn.encmys.ykdz.forest.dailyshop.item;

import cn.encmys.ykdz.forest.dailyshop.DailyShop;
import cn.encmys.ykdz.forest.dailyshop.adventure.AdventureManager;
import cn.encmys.ykdz.forest.dailyshop.api.item.BaseItem;
import cn.encmys.ykdz.forest.dailyshop.item.enums.BaseItemType;
import io.lumine.mythic.api.items.ItemManager;
import io.lumine.mythic.bukkit.MythicBukkit;
import io.lumine.mythic.core.items.MythicItem;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.Nullable;

public class MythicMobsItem implements BaseItem {
    private static final AdventureManager adventureManager = DailyShop.getAdventureManager();
    private final String id;

    public MythicMobsItem(String id) {
        this.id = id;
    }

    @Override
    public String getDisplayName() {
        ItemManager itemManager = MythicBukkit.inst().getItemManager();
        for (MythicItem item : itemManager.getItems()) {
            if (item.getInternalName().equals(getId())) {
                return adventureManager.legacyToMiniMessage(item.getDisplayName());
            }
        }
        return null;
    }

    @Override
    public boolean isSimilar(ItemStack item) {
        ItemManager itemManager = MythicBukkit.inst().getItemManager();
        return itemManager.isMythicItem(item) && itemManager.getMythicTypeFromItem(item).equals(getId());
    }

    @Override
    public boolean isExist() {
        return false;
    }

    @Override
    public BaseItemType getItemType() {
        return BaseItemType.MYTHICMOBS;
    }

    @Override
    public ItemStack build(@Nullable Player player) {
        return MythicBukkit.inst().getItemManager().getItemStack(id);
    }

    public String getId() {
        return id;
    }
}
