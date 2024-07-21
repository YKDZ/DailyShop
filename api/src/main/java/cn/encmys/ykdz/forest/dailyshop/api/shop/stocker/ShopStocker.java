package cn.encmys.ykdz.forest.dailyshop.api.shop.stocker;

import cn.encmys.ykdz.forest.dailyshop.api.product.Product;

import java.util.List;

public interface ShopStocker {
    boolean needRestock();

    void restock();

    void listProduct(Product product);

    long getLastRestocking();

    /**
     * @return Period in tick
     */
    long getRestockPeriod();

    List<String> getListedProducts();

    List<String> getAllProductsId();

    boolean isListedProduct(String id);

    void setLastRestocking(long lastRestocking);

    void addListedProducts(List<String> listedProducts);
}
