package cn.encmys.ykdz.forest.dailyshop.shop.order;

import cn.encmys.ykdz.forest.dailyshop.api.DailyShop;
import cn.encmys.ykdz.forest.dailyshop.api.product.Product;
import cn.encmys.ykdz.forest.dailyshop.api.product.stock.ProductStock;
import cn.encmys.ykdz.forest.dailyshop.api.shop.Shop;
import cn.encmys.ykdz.forest.dailyshop.api.shop.order.ShopOrder;
import cn.encmys.ykdz.forest.dailyshop.api.shop.order.enums.OrderType;
import cn.encmys.ykdz.forest.dailyshop.api.utils.LogUtils;
import com.google.gson.annotations.Expose;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.*;

public class ShopOrderImpl implements ShopOrder {
    @Expose
    private final UUID customerUUID;
    @Expose
    private final Map<String, Integer> orderedProducts = new HashMap<>();
    @Expose
    private OrderType orderType;
    @Expose
    private Map<String, Double> bill = new HashMap<>();
    @Expose
    private boolean isSettled = false;
    @Expose
    private boolean isBilled = false;

    public ShopOrderImpl(Player customer) {
        this.customerUUID = customer.getUniqueId();
    }

    @Override
    public @NotNull ShopOrder combineOrder(ShopOrder order) {
        if (!customerUUID.equals(order.getCustomerUUID())) {
            LogUtils.warn("Try to combine orders with different customer.");
            return this;
        }
        if (orderType != order.getOrderType()) {
            LogUtils.warn("Try to combine orders with different order types.");
            return this;
        }
        if (isSettled || order.isSettled()) {
            LogUtils.warn("Try to combine orders that has already been settled.");
            return this;
        }
        for (Map.Entry<String, Integer> entry : order.getOrderedProducts().entrySet()) {
            modifyStack(entry.getKey(), entry.getValue());
        }
        setBilled(false);
        return this;
    }

    @Override
    public @NotNull ShopOrder setOrderType(@NotNull OrderType orderType) {
        if (isSettled) {
            return this;
        }
        this.orderType = orderType;
        setBilled(false);
        return this;
    }

    @Override
    public @NotNull ShopOrder modifyStack(@NotNull Product product, int amount) {
        return modifyStack(product.getId(), amount);
    }

    @Override
    public @NotNull ShopOrder modifyStack(@NotNull String productId, int amount) {
        if (isSettled) {
            return this;
        }
        int newValue = orderedProducts.getOrDefault(productId, 0) + amount;
        return setStack(productId, newValue);
    }

    @Override
    public @NotNull ShopOrder setStack(@NotNull Product product, int amount) {
        return setStack(product.getId(), amount);
    }

    @Override
    public @NotNull ShopOrder setStack(@NotNull String productId, int amount) {
        if (isSettled) {
            return this;
        }
        if (amount <= 0) {
            orderedProducts.remove(productId);
        } else {
            orderedProducts.put(productId, amount);
        }
        setBilled(false);
        return this;
    }

    @Override
    public boolean isSettled() {
        return isSettled;
    }

    @Override
    public void setSettled(boolean settled) {
        isSettled = settled;
    }

    @Override
    public @NotNull OrderType getOrderType() {
        return orderType;
    }

    @Override
    public UUID getCustomerUUID() {
        return customerUUID;
    }

    @Override
    public Map<String, Integer> getOrderedProducts() {
        return Collections.unmodifiableMap(orderedProducts);
    }

    @Override
    public double getBilledPrice(Product product) {
        return bill.getOrDefault(product.getId(), -1d);
    }

    @Override
    public @NotNull ShopOrder setBill(Map<String, Double> bill) {
        if (isSettled) {
            return this;
        }
        this.bill = bill;
        setBilled(false);
        return this;
    }

    @Override
    public double getTotalPrice() {
        return bill.values().stream().mapToDouble(Double::doubleValue).sum();
    }

    @Override
    public boolean isBilled() {
        return isBilled;
    }

    @Override
    public @NotNull ShopOrder setBilled(boolean billed) {
        if (isSettled) {
            return this;
        }
        isBilled = billed;
        return this;
    }

    @Override
    public void clear() {
        if (isSettled) {
            return;
        }
        isBilled = false;
        orderedProducts.clear();
    }

    @Override
    public void clean(@NotNull Shop shop) {
        Iterator<Map.Entry<String, Integer>> iterator = orderedProducts.entrySet().iterator();
        while (iterator.hasNext()) {
            Map.Entry<String, Integer> entry = iterator.next();

            int stack = entry.getValue();
            String productId = entry.getKey();
            Product product = DailyShop.PRODUCT_FACTORY.getProduct(productId);

            // 商品不存在
            if (product == null) {
                iterator.remove();
            }
            // 商品未上架
            else if (!shop.getShopStocker().isListedProduct(productId)) {
                iterator.remove();
            }
            // 商品库存不足
            else if (product.getProductStock().isStock()) {
                ProductStock stock = product.getProductStock();
                // 公共库存
                if (stock.isGlobalStock() && stock.getCurrentGlobalAmount() < stack) {
                    iterator.remove();
                }
                // 玩家库存
                else if (stock.isPlayerStock() && stock.getCurrentPlayerAmount(customerUUID) < stack) {
                    iterator.remove();
                }
            }
        }
    }
}
