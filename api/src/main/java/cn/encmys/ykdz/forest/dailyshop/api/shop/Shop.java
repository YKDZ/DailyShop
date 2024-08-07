package cn.encmys.ykdz.forest.dailyshop.api.shop;

import cn.encmys.ykdz.forest.dailyshop.api.gui.ShopRelatedGUI;
import cn.encmys.ykdz.forest.dailyshop.api.product.Product;
import cn.encmys.ykdz.forest.dailyshop.api.shop.cashier.ShopCashier;
import cn.encmys.ykdz.forest.dailyshop.api.shop.pricer.ShopPricer;
import cn.encmys.ykdz.forest.dailyshop.api.shop.stocker.ShopStocker;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Map;

public interface Shop {
    String getName();

    String getId();

    ShopRelatedGUI getShopGUI();

    Map<String, ItemStack> getCachedProductItems();

    boolean isCached(String productId);

    void cacheProductItem(@NotNull Product product);

    @Nullable
    ItemStack getCachedProductItem(@NotNull Product product);

    @NotNull
    ItemStack getCachedProductItemOrCreateOne(@NotNull Product product, Player player);

    ShopPricer getShopPricer();

    ShopCashier getShopCashier();

    ShopRelatedGUI getHistoryGUI();

    ShopStocker getShopStocker();

    int getSize();
}
