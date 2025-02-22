package cn.encmys.ykdz.forest.hyphashop.item;

import cn.encmys.ykdz.forest.hyphashop.api.item.BaseItem;
import org.bukkit.DyeColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.entity.TropicalFish;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.TropicalFishBucketMeta;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class TropicalFishBucketItem extends VanillaItem implements BaseItem {
    private final @NotNull TropicalFish.Pattern pattern;
    private final @NotNull DyeColor patternColor;
    private final @NotNull DyeColor bodyColor;

    public TropicalFishBucketItem(@NotNull TropicalFish.Pattern pattern, @NotNull DyeColor patternColor, @NotNull DyeColor bodyColor) {
        super(Material.TROPICAL_FISH_BUCKET);
        this.pattern = pattern;
        this.patternColor = patternColor;
        this.bodyColor = bodyColor;
    }

    @Override
    public @NotNull ItemStack build(@Nullable Player player) {
        ItemStack bucket = new ItemStack(Material.TROPICAL_FISH_BUCKET);

        bucket.editMeta(TropicalFishBucketMeta.class, meta -> {
            meta.setPattern(getPattern());
            meta.setBodyColor(getBodyColor());
            meta.setPatternColor(getPatternColor());
        });

        return bucket;
    }

    public @NotNull TropicalFish.Pattern getPattern() {
        return pattern;
    }

    public @NotNull DyeColor getBodyColor() {
        return bodyColor;
    }

    public @NotNull DyeColor getPatternColor() {
        return patternColor;
    }
}
