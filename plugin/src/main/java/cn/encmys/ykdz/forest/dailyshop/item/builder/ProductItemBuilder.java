package cn.encmys.ykdz.forest.dailyshop.item.builder;

import cn.encmys.ykdz.forest.dailyshop.api.item.decorator.BaseItemDecorator;
import cn.encmys.ykdz.forest.dailyshop.api.item.decorator.enums.PropertyType;
import cn.encmys.ykdz.forest.dailyshop.api.shop.Shop;
import cn.encmys.ykdz.forest.dailyshop.api.utils.TextUtils;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

import java.util.HashMap;
import java.util.Map;

public class ProductItemBuilder {
    @NotNull
    public static ItemStack build(@NotNull String productId, @NotNull BaseItemDecorator decorator, @NotNull Shop shop, Player player) {
        Map<String, String> vars = new HashMap<>() {{
            if (player != null) {
                put("player-name", player.getName());
                put("player-uuid", player.getUniqueId().toString());
            }
            put("shop-name", shop.getName());
            put("shop-id", shop.getId());
        }};

        return new cn.encmys.ykdz.forest.dailyshop.api.utils.ItemBuilder(decorator.getBaseItem().build(player))
                .setDisplayName(TextUtils.decorateTextToComponent(decorator.getProperty(PropertyType.NAME), player, vars))
                .setLore(TextUtils.decorateTextToComponent(decorator.getProperty(PropertyType.LORE), player, vars, null))
                .setItemFlags(decorator.getProperty(PropertyType.ITEM_FLAGS))
                .setCustomModelData(decorator.getProperty(PropertyType.CUSTOM_MODEL_DATA))
                .setBannerPatterns(decorator.getProperty(PropertyType.BANNER_PATTERNS))
                .setFireworkEffects(decorator.getProperty(PropertyType.FIREWORK_EFFECTS))
                .setEnchantments(decorator.getProperty(PropertyType.ENCHANTMENTS))
                .build(shop.getShopCounter().getAmount(productId));
    }
}
