package cn.encmys.ykdz.forest.dailyshop.item;

import cn.encmys.ykdz.forest.dailyshop.api.item.BaseItem;
import cn.encmys.ykdz.forest.dailyshop.api.utils.SkullUtils;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.Nullable;

public class SkullItem extends VanillaItem implements BaseItem {
    /**
     * 接受 url、uuid 或玩家名
     */
    private final String data;

    public SkullItem(String data) {
        super(Material.PLAYER_HEAD);
        this.data = data;
    }

    @Override
    public ItemStack build(@Nullable Player player) {
        return SkullUtils.getSkullFromURL(getData());
    }

    public String getData() {
        return data;
    }
}
