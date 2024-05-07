package cn.encmys.ykdz.forest.dailyshop.item;

import cn.encmys.ykdz.forest.dailyshop.api.item.BaseItem;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.PotionMeta;
import org.bukkit.potion.PotionData;
import org.bukkit.potion.PotionType;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class PotionItem extends VanillaItem implements BaseItem {
    private final Material potionType;
    private final String effectType;
    private final boolean upgradeable;
    private final boolean extendable;

    public PotionItem(@NotNull Material potionType, @NotNull String effectType, boolean upgradeable, boolean extendable) {
        super(potionType);
        this.potionType = potionType;
        this.effectType = effectType;
        this.upgradeable = upgradeable;
        this.extendable = extendable;
    }

    @Override
    public ItemStack build(@Nullable Player player) {
        ItemStack potion = new ItemStack(getPotionType());
        PotionMeta meta = (PotionMeta) potion.getItemMeta();

        if (meta == null) {
            return potion;
        }

        meta.setBasePotionData(new PotionData(PotionType.valueOf(effectType), isUpgradeable(), isExtendable()));
        potion.setItemMeta(meta);
        return potion;
    }

    @Override
    public boolean isExist() {
        return false;
    }

    public Material getPotionType() {
        return potionType;
    }

    public String getEffectType() {
        return effectType;
    }

    public boolean isUpgradeable() {
        return upgradeable;
    }

    public boolean isExtendable() {
        return extendable;
    }
}
