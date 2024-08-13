package cn.encmys.ykdz.forest.dailyshop.item;

import cn.encmys.ykdz.forest.dailyshop.api.DailyShop;
import cn.encmys.ykdz.forest.dailyshop.api.item.BaseItem;
import cn.encmys.ykdz.forest.dailyshop.api.item.enums.BaseItemType;
import io.lumine.mythic.api.items.ItemManager;
import io.lumine.mythic.bukkit.MythicBukkit;
import io.lumine.mythic.core.items.MythicItem;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

public class MythicMobsItem implements BaseItem {
    private final String id;

    public MythicMobsItem(String id) {
        this.id = id;
    }

    @Override
    public String getDisplayName() {
        try (MythicBukkit mythicBukkit = MythicBukkit.inst()) {
            ItemManager itemManager = mythicBukkit.getItemManager();
            for (MythicItem item : itemManager.getItems()) {
                if (item.getInternalName().equals(getId())) {
                    return DailyShop.ADVENTURE_MANAGER.legacyToMiniMessage(item.getDisplayName());
                }
            }
            return null;
        }
    }

    @Override
    public boolean isSimilar(ItemStack item) {
        try (MythicBukkit mythicBukkit = MythicBukkit.inst()) {
            ItemManager itemManager = mythicBukkit.getItemManager();
            return itemManager.isMythicItem(item) && itemManager.getMythicTypeFromItem(item).equals(getId());
        }
    }

    @Override
    public boolean isExist() {
        try (MythicBukkit mythicBukkit = MythicBukkit.inst()) {
            ItemManager itemManager = mythicBukkit.getItemManager();
            return itemManager.getItem(id).isPresent();
        }
    }

    @Override
    public BaseItemType getItemType() {
        return BaseItemType.MYTHIC_MOBS;
    }

    @Override
    public ItemStack build(Player player) {
        try (MythicBukkit mythicBukkit = MythicBukkit.inst()) {
            return mythicBukkit.getItemManager().getItemStack(id);
        }
    }

    public String getId() {
        return id;
    }
}
