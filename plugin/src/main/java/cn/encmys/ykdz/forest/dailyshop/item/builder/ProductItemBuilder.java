package cn.encmys.ykdz.forest.dailyshop.item.builder;

import cn.encmys.ykdz.forest.dailyshop.api.item.decorator.BaseItemDecorator;
import cn.encmys.ykdz.forest.dailyshop.api.shop.Shop;
import cn.encmys.ykdz.forest.dailyshop.api.utils.TextUtils;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.HashMap;
import java.util.Map;

public class ProductItemBuilder {
    public static ItemStack build(@NotNull BaseItemDecorator decorator, @NotNull Shop shop, @Nullable Player player) {
        Map<String, String> vars = new HashMap<>() {{
            if (player != null) {
                put("player-name", player.getName());
                put("player-uuid", player.getUniqueId().toString());
            }
            put("shop-name", shop.getName());
            put("shop-id", shop.getId());
        }};
        return new cn.encmys.ykdz.forest.dailyshop.api.utils.ItemBuilder(decorator.getBaseItem().build(player))
                .setDisplayName(TextUtils.decorateText(decorator.getName(), player, vars))
                .setLore(TextUtils.decorateText(decorator.getLore(), player, vars, null))
                .setItemFlags(decorator.getItemFlags())
                .setCustomModelData(decorator.getCustomModelData())
                .setBannerPatterns(decorator.getPatternsData())
                .setFireworkEffects(decorator.getFireworkEffectData())
                .build(decorator.getAmount());
    }
}
