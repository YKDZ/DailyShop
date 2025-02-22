package cn.encmys.ykdz.forest.hyphashop.shop;

import cn.encmys.ykdz.forest.hyphascript.context.Context;
import cn.encmys.ykdz.forest.hyphashop.api.product.Product;
import cn.encmys.ykdz.forest.hyphashop.api.shop.Shop;
import cn.encmys.ykdz.forest.hyphashop.api.shop.cashier.ShopCashier;
import cn.encmys.ykdz.forest.hyphashop.api.shop.counter.ShopCounter;
import cn.encmys.ykdz.forest.hyphashop.api.shop.pricer.ShopPricer;
import cn.encmys.ykdz.forest.hyphashop.api.shop.stocker.ShopStocker;
import cn.encmys.ykdz.forest.hyphashop.config.ShopConfig;
import cn.encmys.ykdz.forest.hyphashop.config.record.shop.ShopSettingsRecord;
import cn.encmys.ykdz.forest.hyphashop.gui.ShopGUI;
import cn.encmys.ykdz.forest.hyphashop.item.builder.ProductItemBuilder;
import cn.encmys.ykdz.forest.hyphashop.shop.cashier.ShopCashierImpl;
import cn.encmys.ykdz.forest.hyphashop.shop.counter.ShopCounterImpl;
import cn.encmys.ykdz.forest.hyphashop.shop.pricer.ShopPricerImpl;
import cn.encmys.ykdz.forest.hyphashop.shop.stocker.ShopStockerImpl;
import cn.encmys.ykdz.forest.hyphashop.utils.ScriptUtils;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

public class ShopImpl implements Shop {
    @NotNull
    private final String id;
    @NotNull
    private final String name;
    @NotNull
    private final ShopGUI shopGUI;
    @NotNull
    private final ShopPricer shopPricer;
    @NotNull
    private final ShopCashier shopCashier;
    @NotNull
    private final ShopStocker shopStocker;
    @NotNull
    private final ShopCounter shopCounter;
    @NotNull
    private final Map<String, ItemStack> cachedProduct = new HashMap<>();
    @NotNull
    private final Context scriptContext;

    public ShopImpl(@NotNull String id, @NotNull ShopSettingsRecord settings, @NotNull List<String> allProductsId) {
        this.id = id;
        this.name = settings.name();
        this.shopGUI = new ShopGUI(this, ShopConfig.getShopGUIRecord(id));
        this.shopPricer = new ShopPricerImpl(this);
        this.shopCashier = new ShopCashierImpl(this, settings.merchant());
        this.shopStocker = new ShopStockerImpl(this, settings.size(), settings.autoRestockEnabled(), settings.autoRestockPeriod(), allProductsId);
        this.shopCounter = new ShopCounterImpl(this);
        this.scriptContext = ScriptUtils.extractContext(settings.context());
    }

    @Override
    public @NotNull String getName() {
        return name;
    }

    @Override
    public @NotNull String getId() {
        return id;
    }

    @Override
    public @NotNull ShopGUI getShopGUI() {
        return shopGUI;
    }

    @Override
    public boolean isProductItemCached(@NotNull String productId) {
        return getCachedProductItems().containsKey(productId);
    }

    @Override
    public void cacheProductItem(@NotNull Product product) {
        if (product.getProductItemDecorator() == null) {
            throw new RuntimeException("Check Product#isCacheable before Shop#cacheProductItem");
        }
        if (product.isProductItemCacheable()) {
            getCachedProductItems().put(product.getId(), ProductItemBuilder.build(product.getProductItemDecorator(), this, null));
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
                .orElse(ProductItemBuilder.build(product.getProductItemDecorator(), this, player));
    }

    @Override
    public @NotNull ShopPricer getShopPricer() {
        return shopPricer;
    }

    @Override
    public @NotNull ShopCashier getShopCashier() {
        return shopCashier;
    }

    @Override
    public @NotNull ShopStocker getShopStocker() {
        return shopStocker;
    }

    @Override
    public @NotNull Map<String, ItemStack> getCachedProductItems() {
        return cachedProduct;
    }

    @Override
    public @NotNull ShopCounter getShopCounter() {
        return shopCounter;
    }

    @Override
    public @NotNull Context getScriptContext() {
        return scriptContext;
    }
}
