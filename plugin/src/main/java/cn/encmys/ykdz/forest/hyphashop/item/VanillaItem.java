package cn.encmys.ykdz.forest.hyphashop.item;

import cn.encmys.ykdz.forest.hyphashop.api.item.BaseItem;
import cn.encmys.ykdz.forest.hyphashop.api.item.decorator.BaseItemDecorator;
import cn.encmys.ykdz.forest.hyphashop.api.item.enums.BaseItemType;
import cn.encmys.ykdz.forest.hyphashop.config.MinecraftLangConfig;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class VanillaItem implements BaseItem {
    protected final Material material;

    public VanillaItem(@NotNull Material material) {
        this.material = material;
    }

    @Override
    public @NotNull ItemStack build(@Nullable Player player) {
        return new ItemStack(getMaterial());
    }

    @Override
    public @Nullable String getDisplayName(@NotNull BaseItemDecorator decorator) {
        return MinecraftLangConfig.translate(material, "");
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
    public @NotNull BaseItemType getItemType() {
        return BaseItemType.VANILLA;
    }

    public Material getMaterial() {
        return material;
    }
}
