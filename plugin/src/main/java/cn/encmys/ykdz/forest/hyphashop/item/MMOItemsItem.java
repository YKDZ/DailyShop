package cn.encmys.ykdz.forest.hyphashop.item;

import cn.encmys.ykdz.forest.hyphashop.api.item.BaseItem;
import cn.encmys.ykdz.forest.hyphashop.api.item.decorator.BaseItemDecorator;
import cn.encmys.ykdz.forest.hyphashop.api.item.enums.BaseItemType;
import cn.encmys.ykdz.forest.hyphautils.HyphaAdventureUtils;
import net.Indyuce.mmoitems.MMOItems;
import net.Indyuce.mmoitems.api.Type;
import net.Indyuce.mmoitems.api.item.mmoitem.MMOItem;
import net.Indyuce.mmoitems.api.player.PlayerData;
import net.Indyuce.mmoitems.stat.type.NameData;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Locale;
import java.util.Objects;

public class MMOItemsItem implements BaseItem {
    private final @NotNull Type type;
    private final @NotNull String id;

    public MMOItemsItem(@NotNull String type, @NotNull String id) {
        this.type = Objects.requireNonNull(Type.get(type));
        this.id = id;
    }

    @Override
    public @Nullable String getDisplayName(@NotNull BaseItemDecorator decorator) {
        MMOItem mmoItem = MMOItems.plugin.getMMOItem(getType(), getId().toUpperCase(Locale.ENGLISH));

        if (mmoItem == null) {
            return null;
        }

        for (var stat : mmoItem.getStats()) {
            if (stat.getId().equals("NAME")) {
                return HyphaAdventureUtils.legacyToMiniMessage(((NameData) mmoItem.getData(stat)).getMainName());
            }
        }

        return null;
    }

    @Override
    public boolean isSimilar(@NotNull ItemStack item) {
        Type itemType = Type.get(MMOItems.getTypeName(item));
        String itemId = MMOItems.getID(item);
        if (itemType != null && itemId != null) {
            return itemType.equals(getType()) && itemId.equals(getId());
        } else {
            return false;
        }
    }

    @Override
    public boolean isExist() {
        return MMOItems.plugin.getMMOItem(type, id) != null;
    }

    @Override
    public @NotNull BaseItemType getItemType() {
        return BaseItemType.MMOITEMS;
    }

    @Override
    public @NotNull ItemStack build(@Nullable Player player) {
        MMOItem mmoItem;
        if (player == null) {
            mmoItem = MMOItems.plugin.getMMOItem(getType(), getId().toUpperCase(Locale.ENGLISH));
        } else {
            mmoItem = MMOItems.plugin.getMMOItem(getType(), getId().toUpperCase(Locale.ENGLISH), PlayerData.get(player));
        }
        return mmoItem == null ? new ItemStack(Material.AIR) : Objects.requireNonNull(mmoItem.newBuilder().build());
    }

    public @NotNull Type getType() {
        return type;
    }

    public @NotNull String getId() {
        return id;
    }
}
