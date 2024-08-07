package cn.encmys.ykdz.forest.dailyshop.item;

import org.bukkit.Material;
import org.bukkit.entity.Axolotl;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.AxolotlBucketMeta;
import org.jetbrains.annotations.NotNull;

public class AxolotlBucketItem extends VanillaItem {
    private final Axolotl.Variant variant;

    public AxolotlBucketItem(@NotNull Axolotl.Variant variant) {
        super(Material.AXOLOTL_BUCKET);
        this.variant = variant;
    }

    @Override
    public ItemStack build(Player player) {
        ItemStack item = new ItemStack(Material.AXOLOTL_BUCKET);
        AxolotlBucketMeta meta = (AxolotlBucketMeta) item.getItemMeta();

        if (meta == null) {
            return item;
        }

        meta.setVariant(variant);
        item.setItemMeta(meta);
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

    public Axolotl.Variant getVariant() {
        return variant;
    }
}
