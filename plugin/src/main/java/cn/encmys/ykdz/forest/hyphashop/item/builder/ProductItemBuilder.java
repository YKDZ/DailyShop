package cn.encmys.ykdz.forest.hyphashop.item.builder;

import cn.encmys.ykdz.forest.hyphashop.api.item.decorator.BaseItemDecorator;
import cn.encmys.ykdz.forest.hyphashop.api.item.decorator.enums.PropertyType;
import cn.encmys.ykdz.forest.hyphashop.api.shop.Shop;
import cn.encmys.ykdz.forest.hyphashop.utils.ItemBuilder;
import cn.encmys.ykdz.forest.hyphashop.utils.TextUtils;
import cn.encmys.ykdz.forest.hyphashop.utils.VarUtils;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Map;

public class ProductItemBuilder {
    @NotNull
    public static ItemStack build(@NotNull BaseItemDecorator decorator, @NotNull Shop shop, @Nullable Player player) {
        Map<String, Object> vars = VarUtils.extractVars(player, shop);

        return new ItemBuilder(decorator.getBaseItem().build(player))
                // 若没有指定 Name，则不需要为物品设置名称
                // 故不需要使用 BaseItemDecorator#getNameOrUseBaseItemName 方法
                .setDisplayName(TextUtils.decorateTextToComponent(decorator.getProperty(PropertyType.NAME), player, vars))
                .setLore(TextUtils.decorateTextToComponent(decorator.getProperty(PropertyType.LORE), player, vars, null))
                .setItemFlags(decorator.getProperty(PropertyType.ITEM_FLAGS))
                .setCustomModelData(decorator.getProperty(PropertyType.CUSTOM_MODEL_DATA))
                .setBannerPatterns(decorator.getProperty(PropertyType.BANNER_PATTERNS))
                .setFireworkEffects(decorator.getProperty(PropertyType.FIREWORK_EFFECTS))
                .setEnchantments(decorator.getProperty(PropertyType.ENCHANTMENTS))
                .setPotionEffects(decorator.getProperty(PropertyType.POTION_EFFECTS))
                .setArmorTrim(decorator.getProperty(PropertyType.ARMOR_TRIM))
                .setGlider(decorator.getProperty(PropertyType.GLIDER))
                .setFlightDuration(decorator.getProperty(PropertyType.FLIGHT_DURATION))
                .setEnchantGlint(decorator.getProperty(PropertyType.ENCHANT_GLINT))
                .setEnchantable(decorator.getProperty(PropertyType.ENCHANTABLE))
                .setPotionCustomColor(decorator.getProperty(PropertyType.POTION_COLOR))
                .setPotionType(decorator.getProperty(PropertyType.POTION_TYPE))
                .setPotionCustomName(decorator.getProperty(PropertyType.POTION_CUSTOM_NAME))
                // 构建阶段（缓存和实时）只提供一个物品
                // 因为若物品采取了动态数量
                // 则 restock 后会导致缓存物品数量和实际数量不一致
                .build(1);
    }
}
