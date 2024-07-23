package cn.encmys.ykdz.forest.dailyshop.api.shop.factory;

import cn.encmys.ykdz.forest.dailyshop.api.shop.Shop;

import java.util.HashMap;

public interface ShopFactory {
    void load();

    Shop buildShop(String id);

    Shop getShop(String id);

    HashMap<String, Shop> getShops();

    void unload();

    void save();
}
