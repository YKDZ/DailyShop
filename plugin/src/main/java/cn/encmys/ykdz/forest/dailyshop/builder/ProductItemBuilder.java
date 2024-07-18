package cn.encmys.ykdz.forest.dailyshop.builder;

import cn.encmys.ykdz.forest.dailyshop.api.item.decorator.BaseItemDecorator;
import cn.encmys.ykdz.forest.dailyshop.api.utils.TextUtils;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class ProductItemBuilder {
    public static ItemStack build(@NotNull BaseItemDecorator decorator, @Nullable Player player) {
        return new cn.encmys.ykdz.forest.dailyshop.api.utils.ItemBuilder(decorator.getItem().build(player))
                .setDisplayName(TextUtils.decorateText(decorator.getName(), player))
                .setLore(TextUtils.decorateText(decorator.getLore(), player))
                .setItemFlags(decorator.getItemFlags())
                .setCustomModelData(decorator.getCustomModelData())
                .setBannerPatterns(decorator.getPatternsData())
                .setFireworkEffects(decorator.getFireworkEffectData())
                .build(decorator.getAmount());
    }
}
