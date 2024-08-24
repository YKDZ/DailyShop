package cn.encmys.ykdz.forest.dailyshop.item;

import cn.encmys.ykdz.forest.dailyshop.api.DailyShop;
import cn.encmys.ykdz.forest.dailyshop.api.item.BaseItem;
import cn.encmys.ykdz.forest.dailyshop.api.item.enums.BaseItemType;
import io.lumine.mythic.bukkit.MythicBukkit;
import io.lumine.mythic.core.items.MythicItem;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

public class MythicMobsItem implements BaseItem {
    private final String id;
    private MythicBukkit mythicBukkit;

    public MythicMobsItem(String id) {
        this.id = id;
    }

    @Override
    public String getDisplayName() {
        if (mythicBukkit == null || mythicBukkit.isClosed()) {
            this.mythicBukkit = MythicBukkit.inst();
        }
        for (MythicItem item : mythicBukkit.getItemManager().getItems()) {
            if (item.getInternalName().equals(getId())) {
                return DailyShop.ADVENTURE_MANAGER.legacyToMiniMessage(item.getDisplayName());
            }
        }
        return null;
    }

    @Override
    public boolean isSimilar(ItemStack item) {
        if (mythicBukkit == null || mythicBukkit.isClosed()) {
            this.mythicBukkit = MythicBukkit.inst();
        }
        return mythicBukkit.getItemManager().isMythicItem(item) && mythicBukkit.getItemManager().getMythicTypeFromItem(item).equals(getId());
    }

    @Override
    public boolean isExist() {
        if (mythicBukkit == null || mythicBukkit.isClosed()) {
            this.mythicBukkit = MythicBukkit.inst();
        }
        return mythicBukkit.getItemManager().getItem(id).isPresent();
    }

    @Override
    public BaseItemType getItemType() {
        return BaseItemType.MYTHIC_MOBS;
    }

    @Override
    public ItemStack build(Player player) {
        if (mythicBukkit == null || mythicBukkit.isClosed()) {
            this.mythicBukkit = MythicBukkit.inst();
        }
        return mythicBukkit.getItemManager().getItemStack(id);
    }

    public String getId() {
        return id;
    }
}
