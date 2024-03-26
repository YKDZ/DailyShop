package cn.encmys.ykdz.forest.dailyshop.item;

import cn.encmys.ykdz.forest.dailyshop.DailyShop;
import cn.encmys.ykdz.forest.dailyshop.adventure.AdventureManager;
import cn.encmys.ykdz.forest.dailyshop.api.item.BaseItem;
import cn.encmys.ykdz.forest.dailyshop.item.enums.BaseItemType;
import net.Indyuce.mmoitems.MMOItems;
import net.Indyuce.mmoitems.api.Type;
import net.Indyuce.mmoitems.api.item.mmoitem.MMOItem;
import net.Indyuce.mmoitems.api.player.PlayerData;
import net.Indyuce.mmoitems.stat.type.ItemStat;
import net.Indyuce.mmoitems.stat.type.NameData;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Locale;

public class MMOItemsItem implements BaseItem {
    private static final AdventureManager adventureManager = DailyShop.getAdventureManager();
    private final String type;
    private final String id;

    public MMOItemsItem(@NotNull String type, @NotNull String id) {
        this.type = type;
        this.id = id;
    }

    @Override
    public String getDisplayName() {
        MMOItem mmoItem = MMOItems.plugin.getMMOItem(Type.get(getType()), getId().toUpperCase(Locale.ENGLISH));

        if (mmoItem == null) {
            return null;
        }

        for (ItemStat stat : mmoItem.getStats()) {
            if (stat.getId().equals("NAME")) {
                return adventureManager.legacyToMiniMessage(((NameData) mmoItem.getData(stat)).getMainName());
            }
        }

        return null;
    }

    @Override
    public boolean isSimilar(ItemStack item) {
        String itemType = MMOItems.getTypeName(item);
        String itemId = MMOItems.getID(item);
        return itemType.equals(getType()) && itemId.equals(getId());
    }

    @Override
    public BaseItemType getItemType() {
        return BaseItemType.MMOITEMS;
    }

    @Override
    public ItemStack build(@Nullable Player player) {
        MMOItem mmoItem;
        if (player == null) {
            mmoItem = MMOItems.plugin.getMMOItem(Type.get(getType()), getId().toUpperCase(Locale.ENGLISH));
        } else {
            mmoItem = MMOItems.plugin.getMMOItem(Type.get(getType()), getId().toUpperCase(Locale.ENGLISH), PlayerData.get(player));
        }
        return mmoItem == null ? new ItemStack(Material.AIR) : mmoItem.newBuilder().build();
    }

    public String getType()  {
        return type;
    }

    public String getId() {
        return id;
    }
}
