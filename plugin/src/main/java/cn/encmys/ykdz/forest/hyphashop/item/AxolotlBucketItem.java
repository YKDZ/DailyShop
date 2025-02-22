package cn.encmys.ykdz.forest.hyphashop.item;

import org.bukkit.Material;
import org.bukkit.entity.Axolotl;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.AxolotlBucketMeta;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class AxolotlBucketItem extends VanillaItem {
    private final @NotNull Axolotl.Variant variant;

    public AxolotlBucketItem(@NotNull Axolotl.Variant variant) {
        super(Material.AXOLOTL_BUCKET);
        this.variant = variant;
    }

    @Override
    public @NotNull ItemStack build(@Nullable Player player) {
        ItemStack item = new ItemStack(Material.AXOLOTL_BUCKET);

        item.editMeta(AxolotlBucketMeta.class, meta -> meta.setVariant(variant));

        return item;
    }

    @Override
    public boolean isSimilar(@NotNull ItemStack item) {
        AxolotlBucketMeta meta = (AxolotlBucketMeta) item.getItemMeta();
        if (meta == null || !meta.hasVariant()) {
            return false;
        }
        return meta.getVariant() == variant;
    }

    public @NotNull Axolotl.Variant getVariant() {
        return variant;
    }
}
