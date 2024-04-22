package cn.encmys.ykdz.forest.dailyshop.shop.cashier;

import cn.encmys.ykdz.forest.dailyshop.api.product.Product;
import cn.encmys.ykdz.forest.dailyshop.price.enums.PriceMode;
import cn.encmys.ykdz.forest.dailyshop.shop.Shop;
import cn.encmys.ykdz.forest.dailyshop.shop.enums.TransitionResult;
import cn.encmys.ykdz.forest.dailyshop.util.BalanceUtils;
import org.bukkit.entity.Player;

import java.util.HashMap;
import java.util.Map;

public class ShopCashier {
    private final Shop shop;
    private final Player player;
    private final Map<Product, Integer> handledProducts = new HashMap<>();
    private int totalBuyStack = 0;
    private int totalSellStack = 0;

    public ShopCashier(Shop shop, Player player) {
        this.shop = shop;
        this.player = player;
    }

    public ShopCashier addProduct(Product product, int amount) {
        handledProducts.put(product, handledProducts.getOrDefault(product, 0) + amount);
        return this;
    }

    public ShopCashier removeProduct(Product product) {
        handledProducts.remove(product);
        return this;
    }

    public TransitionResult sellTo() {
        TransitionResult result = canSellTo();
        if (result == TransitionResult.SUCCESS) {
            for (Map.Entry<Product, Integer> entry : handledProducts.entrySet()) {
                Product product = entry.getKey();
                int stack = entry.getValue();

                BalanceUtils.removeBalance(player, shop.getShopPricer().getBuyPrice(product.getId()));
                product.give(shop, player, stack);
            }
            totalSellStack++;
        }
        return result;
    }

    public TransitionResult buyFrom() {
        TransitionResult result = canBuyFrom();
        if (result == TransitionResult.SUCCESS) {
            for (Map.Entry<Product, Integer> entry : handledProducts.entrySet()) {
                Product product = entry.getKey();
                int stack = entry.getValue();

                BalanceUtils.addBalance(player, shop.getShopPricer().getBuyPrice(product.getId()));
                product.take(shop, player, stack);
            }
            totalBuyStack++;
        }
        return result;
    }

    public TransitionResult buyAllFrom() {
        TransitionResult result = canBuyFrom();
        if (result != TransitionResult.SUCCESS) {
            return result;
        }

        while (buyFrom() == TransitionResult.SUCCESS);
        return TransitionResult.SUCCESS;
    }

    public TransitionResult canSellTo() {
        for (Map.Entry<Product, Integer> entry : handledProducts.entrySet()) {
            Product product = entry.getKey();
            int stack = entry.getValue();

            if (BalanceUtils.checkBalance(player) < shop.getShopPricer().getBuyPrice(product.getId()) * stack) {
                return TransitionResult.NOT_ENOUGH_MONEY;
            } else if (product.getBuyPrice().getPriceMode() == PriceMode.DISABLE) {
                return TransitionResult.TRANSITION_DISABLED;
            } else if (!canHold()) {
                return TransitionResult.NOT_ENOUGH_MONEY;
            }
        }
        return TransitionResult.SUCCESS;
    }

    public TransitionResult canBuyFrom() {
        for (Map.Entry<Product, Integer> entry : handledProducts.entrySet()) {
            Product product = entry.getKey();
            int stack = entry.getValue();

            if (product.has(shop, player, stack) == 0) {
                return TransitionResult.NOT_ENOUGH_PRODUCT;
            } else if (product.getBuyPrice().getPriceMode() == PriceMode.DISABLE) {
                return TransitionResult.TRANSITION_DISABLED;
            }
        }
        return TransitionResult.SUCCESS;
    }

    public boolean canHold() {
        for (Map.Entry<Product, Integer> entry : handledProducts.entrySet()) {
            Product product = entry.getKey();
            int stack = entry.getValue();

            if (!product.canHold(shop, player, stack)) {
                return false;
            }
        }
        return true;
    }

    public int getTotalBuyStack() {
        return totalBuyStack;
    }

    public int getTotalSellStack() {
        return totalSellStack;
    }
}
