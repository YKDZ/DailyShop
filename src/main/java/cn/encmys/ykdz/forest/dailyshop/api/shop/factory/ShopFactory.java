package cn.encmys.ykdz.forest.dailyshop.api.shop.factory;

import cn.encmys.ykdz.forest.dailyshop.shop.ShopImpl;

import java.util.HashMap;

public interface ShopFactory {
    void load();

    ShopImpl buildShop(String id);

    ShopImpl getShop(String id);

    HashMap<String, ShopImpl> getAllShops();

    void unload();

    void save();
}
