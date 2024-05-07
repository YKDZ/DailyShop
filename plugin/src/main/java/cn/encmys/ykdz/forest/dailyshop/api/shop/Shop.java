package cn.encmys.ykdz.forest.dailyshop.api.shop;

import cn.encmys.ykdz.forest.dailyshop.api.gui.ShopRelatedGUI;
import cn.encmys.ykdz.forest.dailyshop.api.product.Product;
import cn.encmys.ykdz.forest.dailyshop.api.shop.cashier.ShopCashier;
import cn.encmys.ykdz.forest.dailyshop.api.shop.pricer.ShopPricer;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.Map;

public interface Shop {
    void restock();

    void listProduct(Product product);

    long getLastRestocking();

    String getName();

    int getRestockTime();

    String getId();

    List<String> getListedProducts();

    List<String> getAllProductsId();

    boolean isListedProduct(String id);

    void setLastRestocking(long lastRestocking);

    void addListedProducts(List<String> listedProducts);

    ShopRelatedGUI getShopGUI();

    Map<String, ItemStack> getCachedProductItems();

    boolean hasCachedProductItem(String productId);

    void cacheProductItem(Product product);

    @Nullable
    ItemStack getCachedProductItem(@NotNull Product product);

    @NotNull
    ItemStack getCachedProductItemOrCreateOne(@NotNull Product product, @Nullable Player player);

    ShopPricer getShopPricer();

    ShopCashier getShopCashier();

    ShopRelatedGUI getHistoryGUI();
}
