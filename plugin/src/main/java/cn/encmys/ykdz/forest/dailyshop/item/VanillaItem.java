package cn.encmys.ykdz.forest.dailyshop.item;

import cn.encmys.ykdz.forest.dailyshop.api.config.MinecraftLangConfig;
import cn.encmys.ykdz.forest.dailyshop.api.item.BaseItem;
import cn.encmys.ykdz.forest.dailyshop.api.item.enums.BaseItemType;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

public class VanillaItem implements BaseItem {
    private final Material material;

    public VanillaItem(@NotNull Material material) {
        this.material = material;
    }

    @Override
    public ItemStack build(Player player) {
        return new ItemStack(getMaterial());
    }

    @Override
    public String getDisplayName() {
        return MinecraftLangConfig.translate(material);
    }

    @Override
    public boolean isSimilar(@NotNull ItemStack item) {
        return getMaterial() == item.getType();
    }

    @Override
    public boolean isExist() {
        return true;
    }

    @Override
    public BaseItemType getItemType() {
        return BaseItemType.VANILLA;
    }

    public Material getMaterial() {
        return material;
    }
}
