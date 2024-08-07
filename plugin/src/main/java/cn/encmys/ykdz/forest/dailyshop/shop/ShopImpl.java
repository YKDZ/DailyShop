package cn.encmys.ykdz.forest.dailyshop.shop;

import cn.encmys.ykdz.forest.dailyshop.api.product.Product;
import cn.encmys.ykdz.forest.dailyshop.api.shop.Shop;
import cn.encmys.ykdz.forest.dailyshop.api.shop.cashier.ShopCashier;
import cn.encmys.ykdz.forest.dailyshop.api.shop.cashier.record.MerchantRecord;
import cn.encmys.ykdz.forest.dailyshop.api.shop.pricer.ShopPricer;
import cn.encmys.ykdz.forest.dailyshop.api.shop.stocker.ShopStocker;
import cn.encmys.ykdz.forest.dailyshop.gui.ShopGUI;
import cn.encmys.ykdz.forest.dailyshop.item.builder.ProductItemBuilder;
import cn.encmys.ykdz.forest.dailyshop.shop.cashier.ShopCashierImpl;
import cn.encmys.ykdz.forest.dailyshop.shop.pricer.ShopPricerImpl;
import cn.encmys.ykdz.forest.dailyshop.shop.stocker.ShopStockerImpl;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

public class ShopImpl implements Shop {
    private final String id;
    private final String name;
    private final int size;
    private final ShopGUI shopGUI;
    private final ShopPricer shopPricer;
    private final ShopCashier shopCashier;
    private final ShopStocker shopStocker;
    private final Map<String, ItemStack> cachedProduct = new HashMap<>();

    /**
     * @param id            Shop id
     * @param restockPeriod Shop restock period in ticks
     * @param allProductsId ID of all possible products
     * @param size          Maximum number of items in the shop at the same time
     */
    public ShopImpl(String id, String name, boolean restockEnabled, long restockPeriod, List<String> allProductsId, int size, MerchantRecord merchant) {
        this.id = id;
        this.name = name;
        this.size = size;
        shopGUI = new ShopGUI(this);
        shopPricer = new ShopPricerImpl(this);
        shopCashier = new ShopCashierImpl(this, merchant);
        shopStocker = new ShopStockerImpl(this, restockEnabled, restockPeriod, allProductsId);
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public String getId() {
        return id;
    }

    @Override
    public ShopGUI getShopGUI() {
        return shopGUI;
    }

    @Override
    public boolean isCached(String productId) {
        return getCachedProductItems().containsKey(productId);
    }

    @Override
    public void cacheProductItem(@NotNull Product product) {
        if (product.getItemDecorator() == null) {
            throw new RuntimeException("Check Product#isCacheable before Shop#cacheProductItem");
        }
        if (product.isProductItemCacheable()) {
            getCachedProductItems().put(product.getId(), ProductItemBuilder.build(product.getItemDecorator(), this, null));
        }
    }

    @Override
    @Nullable
    public ItemStack getCachedProductItem(@NotNull Product product) {
        String id = product.getId();
        if (product.isProductItemCacheable() && !isCached(id)) {
            cacheProductItem(product);
        }
        return getCachedProductItems().get(id);
    }

    @Override
    @NotNull
    public ItemStack getCachedProductItemOrCreateOne(@NotNull Product product, Player player) {
        if (product.getItemDecorator() == null) {
            throw new RuntimeException("Check Product#isCacheable before Shop#getCachedProductItemOrCreateOne");
        }
        return Optional.ofNullable(getCachedProductItem(product))
                .orElse(ProductItemBuilder.build(product.getItemDecorator(), this, player));
    }

    @Override
    public ShopPricer getShopPricer() {
        return shopPricer;
    }

    @Override
    public ShopCashier getShopCashier() {
        return shopCashier;
    }

    @Override
    public int getSize() {
        return size;
    }

    @Override
    public ShopStocker getShopStocker() {
        return shopStocker;
    }

    @Override
    public Map<String, ItemStack> getCachedProductItems() {
        return cachedProduct;
    }
}
