package cn.encmys.ykdz.forest.dailyshop.api.product;

import cn.encmys.ykdz.forest.dailyshop.item.GUIProductItem;
import cn.encmys.ykdz.forest.dailyshop.price.PriceProvider;
import cn.encmys.ykdz.forest.dailyshop.rarity.Rarity;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.Nullable;
import xyz.xenondevs.invui.item.impl.AbstractItem;

import java.util.List;

public interface Product {
    String getId();

    List<String> getDescLore();

    AbstractItem getGUIItem(String shopId);

    GUIProductItem buildGUIProductItem(String shopId);

    /**
     * @param player Buyer
     */
    void sellTo(@Nullable String shopId, Player player);

    /**
     * @param player Seller
     */
    void buyFrom(@Nullable String shopId, Player player);

    String getDisplayName();

    Material getMaterial();

    int getAmount();

    boolean canBuyFrom(@Nullable String shopId, Player player);

    Rarity getRarity();

    PriceProvider getBuyPriceProvider();

    PriceProvider getSellPriceProvider();

    void updatePrice(String shopId);
}
