package cn.encmys.ykdz.forest.hyphashop.api.shop.factory;

import cn.encmys.ykdz.forest.hyphashop.api.shop.Shop;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.jetbrains.annotations.Unmodifiable;

import java.util.Map;

public interface ShopFactory {
    void load();

    Shop buildShop(String id);

    @Nullable
    Shop getShop(String id);

    @NotNull
    @Unmodifiable Map<String, Shop> getShops();

    void unload();

    void save();
}
