package cn.encmys.ykdz.forest.dailyshop.shop;

import cn.encmys.ykdz.forest.dailyshop.api.config.ShopConfig;
import cn.encmys.ykdz.forest.dailyshop.api.config.record.shop.ShopSettingsRecord;
import cn.encmys.ykdz.forest.dailyshop.api.product.Product;
import cn.encmys.ykdz.forest.dailyshop.api.shop.Shop;
import cn.encmys.ykdz.forest.dailyshop.api.shop.cashier.ShopCashier;
import cn.encmys.ykdz.forest.dailyshop.api.shop.counter.ShopCounter;
import cn.encmys.ykdz.forest.dailyshop.api.shop.pricer.ShopPricer;
import cn.encmys.ykdz.forest.dailyshop.api.shop.stocker.ShopStocker;
import cn.encmys.ykdz.forest.dailyshop.gui.ShopGUI;
import cn.encmys.ykdz.forest.dailyshop.item.builder.ProductItemBuilder;
import cn.encmys.ykdz.forest.dailyshop.shop.cashier.ShopCashierImpl;
import cn.encmys.ykdz.forest.dailyshop.shop.counter.ShopCounterImpl;
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
    private final ShopGUI shopGUI;
    private final ShopPricer shopPricer;
    private final ShopCashier shopCashier;
    private final ShopStocker shopStocker;
    private final ShopCounter shopCounter;
    private final Map<String, ItemStack> cachedProduct = new HashMap<>();

    public ShopImpl(String id, ShopSettingsRecord settings, List<String> allProductsId) {
        this.id = id;
        this.name = settings.name();
        shopGUI = new ShopGUI(this, ShopConfig.getShopGUIRecord(id));
        shopPricer = new ShopPricerImpl(this);
        shopCashier = new ShopCashierImpl(this, settings.merchant());
        shopStocker = new ShopStockerImpl(this, settings.size(), settings.autoRestockEnabled(), settings.autoRestockPeriod(), allProductsId);
        shopCounter = new ShopCounterImpl(this);
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
    public boolean isProductItemCached(String productId) {
        return getCachedProductItems().containsKey(productId);
    }

    @Override
    public void cacheProductItem(@NotNull Product product) {
        if (product.getProductItemDecorator() == null) {
            throw new RuntimeException("Check Product#isCacheable before Shop#cacheProductItem");
        }
        if (product.isProductItemCacheable()) {
            getCachedProductItems().put(product.getId(), ProductItemBuilder.build(product.getId(), product.getProductItemDecorator(), this, null));
        }
    }

    @Override
    @Nullable
    public ItemStack getCachedProductItem(@NotNull Product product) {
        String id = product.getId();
        if (product.isProductItemCacheable() && !isProductItemCached(id)) {
            cacheProductItem(product);
        }
        return getCachedProductItems().get(id);
    }

    @Override
    @NotNull
    public ItemStack getCachedProductItemOrBuildOne(@NotNull Product product, Player player) {
        if (product.getProductItemDecorator() == null) {
            throw new RuntimeException("Check Product#isCacheable before Shop#getCachedProductItemOrCreateOne");
        }
        return Optional.ofNullable(getCachedProductItem(product))
                .orElse(ProductItemBuilder.build(product.getId(), product.getProductItemDecorator(), this, player));
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
    public ShopStocker getShopStocker() {
        return shopStocker;
    }

    @Override
    public Map<String, ItemStack> getCachedProductItems() {
        return cachedProduct;
    }

    @Override
    public ShopCounter getShopCounter() {
        return shopCounter;
    }
}
