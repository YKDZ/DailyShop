package cn.encmys.ykdz.forest.dailyshop.item;

import cn.encmys.ykdz.forest.dailyshop.api.item.BaseItem;
import cn.encmys.ykdz.forest.dailyshop.util.SkullUtils;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.Nullable;

public class SkullItem extends VanillaItem implements BaseItem {
    private final String url;

    public SkullItem(String url) {
        super(Material.PLAYER_HEAD);
        this.url = url;
    }

    @Override
    public ItemStack build(@Nullable Player player) {
        return SkullUtils.generateSkullFromURLTexture(getUrl());
    }

    public String getUrl() {
        return url;
    }
}
