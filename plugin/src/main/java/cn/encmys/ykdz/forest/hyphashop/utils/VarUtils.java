package cn.encmys.ykdz.forest.hyphashop.utils;

import cn.encmys.ykdz.forest.hyphashop.api.HyphaShop;
import cn.encmys.ykdz.forest.hyphashop.api.product.Product;
import cn.encmys.ykdz.forest.hyphashop.api.profile.Profile;
import cn.encmys.ykdz.forest.hyphashop.api.shop.Shop;
import cn.encmys.ykdz.forest.hyphashop.api.shop.order.enums.OrderType;
import cn.encmys.ykdz.forest.hyphashop.config.Config;
import cn.encmys.ykdz.forest.hyphashop.config.MessageConfig;
import cn.encmys.ykdz.forest.hyphashop.gui.StackPickerGUI;
import net.kyori.adventure.text.serializer.plain.PlainTextComponentSerializer;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.HashMap;
import java.util.Map;

public class VarUtils {
    public static @NotNull Map<String, Object> extractVars(@Nullable Shop shop, @Nullable Product product) {
        return new HashMap<>() {{
           if (product != null) {
                put("__product", product);
                put("product_id", product.getId());
                put("product_name", product.getIconDecorator().getNameOrUseBaseItemName());
                put("product_rarity_id", product.getRarity().id());
                if (shop != null) {
                    put("__shop", shop);
                    put("shop_name", shop.getName());
                    put("shop_id", shop.getId());
                    put("is_merchant", shop.getShopCashier().isMerchant());
                    if (shop.getShopCashier().isMerchant()) {
                        put("merchant_balance", shop.getShopCashier().getBalance());
                    }
                    put("product_amount", shop.getShopCounter().getAmount(product.getId()));
                    put("total_history_bought_amount", SettlementLogUtils.getHistoryAmountFromLogs(shop.getId(), product.getId(), Config.logUsageLimit_timeRange, Config.logUsageLimit_entryAmount, OrderType.SELL_TO));
                    put("total_history_sold_amount", SettlementLogUtils.getHistoryAmountFromLogs(shop.getId(), product.getId(), Config.logUsageLimit_timeRange, Config.logUsageLimit_entryAmount, OrderType.BUY_FROM, OrderType.BUY_ALL_FROM));
                    put("total_history_bought_stack", SettlementLogUtils.getHistoryStackAmountFromLogs(shop.getId(), product.getId(), Config.logUsageLimit_timeRange, Config.logUsageLimit_entryAmount, OrderType.SELL_TO));
                    put("total_history_sold_stack", SettlementLogUtils.getHistoryStackAmountFromLogs(shop.getId(), product.getId(), Config.logUsageLimit_timeRange, Config.logUsageLimit_entryAmount, OrderType.BUY_FROM, OrderType.BUY_ALL_FROM));
                }
           }
        }};
    }

    public static @NotNull Map<String, Object> extractVars(@Nullable Player player,
                                                           @Nullable Shop shop) {
        return new HashMap<>() {{
            {
                if (player != null) {
                    put("__player", player);
                    put("player_name", PlainTextComponentSerializer.plainText().serialize(player.displayName()));
                    put("player_uuid", player.getUniqueId().toString());
                    Profile profile = HyphaShop.PROFILE_FACTORY.getProfile(player);
                    put("cart_total_price", profile.getCart().getTotalPrice());
                    if (profile.getCurrentStackPickerGUI() != null) {
                        put("stack", ((StackPickerGUI) profile.getCurrentStackPickerGUI()).getStack());
                    }
                }
            }
            {
                if (shop != null) {
                    put("__shop", shop);
                    put("shop_name", shop.getName());
                    put("shop_id", shop.getId());
                    put("is_merchant", shop.getShopCashier().isMerchant());
                    if (shop.getShopCashier().isMerchant()) {
                        put("merchant_balance", shop.getShopCashier().getBalance());
                    }
                    if (player != null) {
                        Profile profile = HyphaShop.PROFILE_FACTORY.getProfile(player);
                        put("cart_mode_id", profile.getCart().getMode().name());
                        put("cart_mode_name", MessageConfig.getTerm(profile.getCart().getMode()));
                        put("shopping_mode_id", profile.getShoppingMode(shop.getId()).name());
                        put("shopping_mode_name", MessageConfig.getTerm(profile.getShoppingMode(shop.getId())));
                    }
                }
            }
        }};
    }

    public static @NotNull Map<String, Object> extractVars(@Nullable CommandSender sender,
                                                           @Nullable Shop shop) {
        if (sender instanceof Player) return extractVars((Player) sender, shop);

        return new HashMap<>() {{
            {
                if (sender != null) {
                    put("__sender", sender);
                    put("sender_name", PlainTextComponentSerializer.plainText().serialize(sender.name()));
                }
            }
            {
                if (shop != null) {
                    put("__shop", shop);
                    put("shop_name", shop.getName());
                    put("shop_id", shop.getId());
                    put("is_merchant", shop.getShopCashier().isMerchant());
                    if (shop.getShopCashier().isMerchant()) {
                        put("merchant_balance", shop.getShopCashier().getBalance());
                    }
                }
            }
        }};
    }
}
