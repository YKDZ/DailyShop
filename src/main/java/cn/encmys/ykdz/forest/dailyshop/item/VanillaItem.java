package cn.encmys.ykdz.forest.dailyshop.item;

import cn.encmys.ykdz.forest.dailyshop.DailyShop;
import cn.encmys.ykdz.forest.dailyshop.api.item.ProductItem;
import cn.encmys.ykdz.forest.dailyshop.config.Config;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class VanillaItem implements ProductItem {
    private final Material material;

    public VanillaItem(@NotNull Material material) {
        this.material = material;
    }

    @Override
    public ItemStack build(@Nullable Player player) {
        return new ItemStack(material);
    }

    @Override
    public String getDisplayName() {
        return DailyShop.getItemsLangAPI().translate(material, Config.language);
    }

    @Override
    public boolean isSimilar(@NotNull ItemStack item) {
        return item.isSimilar(build(null));
    }

    public Material getMaterial() {
        return material;
    }
}
