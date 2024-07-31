package cn.encmys.ykdz.forest.dailyshop.item.builder;

import cn.encmys.ykdz.forest.dailyshop.api.item.decorator.BaseItemDecorator;
import cn.encmys.ykdz.forest.dailyshop.api.shop.Shop;
import cn.encmys.ykdz.forest.dailyshop.api.utils.TextUtils;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class ProductItemBuilder {
    public static ItemStack build(@NotNull BaseItemDecorator decorator, @NotNull Shop shop, @Nullable Player player) {
        return new cn.encmys.ykdz.forest.dailyshop.api.utils.ItemBuilder(decorator.getBaseItem().build(player))
                .setDisplayName(TextUtils.decorateText(decorator.getName(), player, null))
                .setLore(TextUtils.decorateText(decorator.getLore(), player, null, null))
                .setItemFlags(decorator.getItemFlags())
                .setCustomModelData(decorator.getCustomModelData())
                .setBannerPatterns(decorator.getPatternsData())
                .setFireworkEffects(decorator.getFireworkEffectData())
                .build(decorator.getAmount());
    }
}
