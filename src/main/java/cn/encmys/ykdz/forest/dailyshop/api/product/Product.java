package cn.encmys.ykdz.forest.dailyshop.api.product;

import cn.encmys.ykdz.forest.dailyshop.price.PriceProvider;
import cn.encmys.ykdz.forest.dailyshop.rarity.Rarity;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import xyz.xenondevs.invui.item.impl.AbstractItem;

import java.util.List;

public interface Product {
    String getId();

    List<String> getDescLore();

    AbstractItem getGUIItem();

    /**
     * @param player Buyer
     */
    void sellTo(Player player);

    /**
     * @param player Seller
     */
    void buyFrom(Player player);

    String getDisplayName();

    Material getMaterial();

    int getAmount();

    Rarity getRarity();

    PriceProvider getBuyPriceProvider();

    PriceProvider getSellPriceProvider();
}
