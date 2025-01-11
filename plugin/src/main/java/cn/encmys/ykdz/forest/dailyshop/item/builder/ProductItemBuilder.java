package cn.encmys.ykdz.forest.dailyshop.item.builder;

import cn.encmys.ykdz.forest.dailyshop.api.item.decorator.BaseItemDecorator;
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
                .setDisplayName(TextUtils.decorateTextToComponent(decorator.getName(), player, vars))
                .setLore(TextUtils.decorateTextToComponent(decorator.getLore(), player, vars, null))
                .setItemFlags(decorator.getItemFlags())
                .setCustomModelData(decorator.getCustomModelData())
                .setBannerPatterns(decorator.getBannerPatterns())
                .setFireworkEffects(decorator.getFireworkEffects())
                .setEnchantments(decorator.getEnchantments())
                .build(shop.getShopCounter().getAmount(productId));
    }
}
