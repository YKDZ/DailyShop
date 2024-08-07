package cn.encmys.ykdz.forest.dailyshop.item;

import cn.encmys.ykdz.forest.dailyshop.api.item.BaseItem;
import org.bukkit.DyeColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.entity.TropicalFish;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.TropicalFishBucketMeta;

public class TropicalFishBucketItem extends VanillaItem implements BaseItem {
    private final TropicalFish.Pattern pattern;
    private final DyeColor patternColor;
    private final DyeColor bodyColor;

    public TropicalFishBucketItem(TropicalFish.Pattern pattern, DyeColor patternColor, DyeColor bodyColor) {
        super(Material.TROPICAL_FISH_BUCKET);
        this.pattern = pattern;
        this.patternColor = patternColor;
        this.bodyColor = bodyColor;
    }

    @Override
    public ItemStack build(Player player) {
        ItemStack bucket = new ItemStack(Material.TROPICAL_FISH_BUCKET);
        TropicalFishBucketMeta meta = (TropicalFishBucketMeta) bucket.getItemMeta();
        if (meta == null) {
            return bucket;
        }
        meta.setPattern(getPattern());
        meta.setBodyColor(getBodyColor());
        meta.setPatternColor(getPatternColor());
        bucket.setItemMeta(meta);
        return bucket;
    }

    public TropicalFish.Pattern getPattern() {
        return pattern;
    }

    public DyeColor getBodyColor() {
        return bodyColor;
    }

    public DyeColor getPatternColor() {
        return patternColor;
    }
}
