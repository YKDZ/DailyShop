package cn.encmys.ykdz.forest.dailyshop.api.gui;

import cn.encmys.ykdz.forest.dailyshop.api.shop.Shop;
import org.bukkit.entity.Player;

public abstract class ShopRelatedGUI extends GUI {
    protected final Shop shop;

    public ShopRelatedGUI(Shop shop) {
        this.shop = shop;
    }

    public Shop getShop() {
        return shop;
    }

    public abstract void open(Player player);
}
