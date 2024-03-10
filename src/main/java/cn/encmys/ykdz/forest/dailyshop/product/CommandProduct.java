package cn.encmys.ykdz.forest.dailyshop.product;

import cn.encmys.ykdz.forest.dailyshop.api.product.Product;
import cn.encmys.ykdz.forest.dailyshop.enums.ProductType;
import cn.encmys.ykdz.forest.dailyshop.item.GUIProductItem;
import cn.encmys.ykdz.forest.dailyshop.price.PriceProvider;
import cn.encmys.ykdz.forest.dailyshop.rarity.Rarity;
import me.clip.placeholderapi.PlaceholderAPI;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.Nullable;
import xyz.xenondevs.invui.item.impl.AbstractItem;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class CommandProduct implements Product {
    private final String id;
    private final PriceProvider buyPriceProvider;
    private final PriceProvider sellPriceProvider;
    private final Rarity rarity;
    private final Material material;
    private final int amount;
    private final String displayName;
    private final List<String> descLore;
    private final List<String> commands;
    private final Map<String, GUIProductItem> guiProductItems = new HashMap<>();

    public CommandProduct(
            String id,
            PriceProvider buyPriceProvider,
            PriceProvider sellPriceProvider,
            Rarity rarity,
            Material material,
            int amount,
            @Nullable String displayName,
            @Nullable List<String> descLore, List<String> commands) {
        this.id = id;
        this.buyPriceProvider = buyPriceProvider;
        this.sellPriceProvider = sellPriceProvider;
        this.rarity = rarity;
        this.material = material;
        this.amount = amount;
        this.displayName = displayName;
        this.descLore = descLore;
        this.commands = commands;
    }

    @Override
    public String getId() {
        return id;
    }

    @Override
    public List<String> getDescLore() {
        return descLore;
    }

    @Override
    public AbstractItem getGUIItem(String shopId) {
        return guiProductItems.get(shopId) == null ?
                buildGUIProductItem(shopId) :
                guiProductItems.get(shopId);
    }

    @Override
    public GUIProductItem buildGUIProductItem(String shopId) {
        guiProductItems.put(shopId, new GUIProductItem(shopId, this));
        return guiProductItems.get(shopId);
    }

    @Override
    public void sellTo(@Nullable String shopId, Player player) {
        for (int i = 0; i < amount; i++) {
            for (String command : commands) {
                Bukkit.getServer().dispatchCommand(Bukkit.getConsoleSender(), PlaceholderAPI.setPlaceholders(player, command));
            }
        }
    }

    @Override
    public void buyFrom(@Nullable String shopId, Player player) {
        return;
    }

    @Override
    public void buyAllFrom(@Nullable String shopId, Player player) {
        return;
    }

    @Override
    public String getDisplayName() {
        return displayName;
    }

    @Override
    public Material getMaterial() {
        return material;
    }

    @Override
    public int getAmount() {
        return amount;
    }

    @Override
    public boolean canBuyFrom(@Nullable String shopId, Player player) {
        return false;
    }

    @Override
    public Rarity getRarity() {
        return rarity;
    }

    @Override
    public PriceProvider getBuyPriceProvider() {
        return buyPriceProvider;
    }

    @Override
    public PriceProvider getSellPriceProvider() {
        return sellPriceProvider;
    }

    @Override
    public void updatePrice(String shopId) {
        getBuyPriceProvider().update(shopId);
        getSellPriceProvider().update(shopId);
    }

    @Override
    public ProductType getType() {
        return ProductType.COMMAND;
    }

    @Override
    public List<String> getBundleContents() {
        return new ArrayList<>();
    }
}
