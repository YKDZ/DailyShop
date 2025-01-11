package cn.encmys.ykdz.forest.dailyshop.item;

import cn.encmys.ykdz.forest.dailyshop.api.item.BaseItem;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.PotionMeta;
import org.bukkit.potion.PotionType;
import org.jetbrains.annotations.NotNull;

public class PotionItem extends VanillaItem implements BaseItem {
    private final Material potionType;
    private final String effectType;

    public PotionItem(@NotNull Material potionType, @NotNull String effectType) {
        super(potionType);
        this.potionType = potionType;
        this.effectType = effectType;
    }

    @Override
    public ItemStack build(Player player) {
        ItemStack potion = new ItemStack(getPotionType());
        PotionMeta meta = (PotionMeta) potion.getItemMeta();

        if (meta == null) {
            return potion;
        }

        meta.setBasePotionType(PotionType.valueOf(effectType));
        potion.setItemMeta(meta);
        return potion;
    }

    public Material getPotionType() {
        return potionType;
    }

    public String getEffectType() {
        return effectType;
    }
}
