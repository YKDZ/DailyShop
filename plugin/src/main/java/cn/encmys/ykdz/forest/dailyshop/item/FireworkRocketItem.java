package cn.encmys.ykdz.forest.dailyshop.item;

import cn.encmys.ykdz.forest.dailyshop.api.item.BaseItem;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.FireworkMeta;

public class FireworkRocketItem extends VanillaItem implements BaseItem {
    private final int power;

    public FireworkRocketItem(int power) {
        super(Material.FIREWORK_ROCKET);
        this.power = power;
    }

    @Override
    public ItemStack build(Player player) {
        ItemStack firework = new ItemStack(Material.FIREWORK_ROCKET);
        FireworkMeta meta = (FireworkMeta) firework.getItemMeta();
        if (meta == null) {
            return firework;
        }
        meta.setPower(getPower());
        firework.setItemMeta(meta);
        return firework;
    }

    public int getPower() {
        return power;
    }
}
