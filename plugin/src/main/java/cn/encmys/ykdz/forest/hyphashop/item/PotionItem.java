package cn.encmys.ykdz.forest.hyphashop.item;

import cn.encmys.ykdz.forest.hyphashop.api.item.decorator.BaseItemDecorator;
import cn.encmys.ykdz.forest.hyphashop.api.item.decorator.enums.PropertyType;
import cn.encmys.ykdz.forest.hyphashop.config.MinecraftLangConfig;
import org.bukkit.Material;
import org.bukkit.potion.PotionType;
import org.jetbrains.annotations.NotNull;

public class PotionItem extends VanillaItem {

    public PotionItem(@NotNull Material material) {
        super(material);
    }

    @Override
    public String getDisplayName(@NotNull BaseItemDecorator decorator) {
        PotionType type = decorator.getProperty(PropertyType.POTION_TYPE);
        return MinecraftLangConfig.translate(material, type == null ? "" : ".effect." + type.name().toLowerCase().replaceAll("long_|strong_", ""));
    }
}
