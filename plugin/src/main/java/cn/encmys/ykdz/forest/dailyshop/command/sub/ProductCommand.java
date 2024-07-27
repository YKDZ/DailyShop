package cn.encmys.ykdz.forest.dailyshop.command.sub;

import cn.encmys.ykdz.forest.dailyshop.api.DailyShop;
import cn.encmys.ykdz.forest.dailyshop.api.config.MessageConfig;
import cn.encmys.ykdz.forest.dailyshop.api.utils.ColorUtils;
import cn.encmys.ykdz.forest.dailyshop.api.utils.PlayerUtils;
import cn.encmys.ykdz.forest.dailyshop.api.utils.TextUtils;
import cn.encmys.ykdz.forest.dailyshop.hook.MMOItemsHook;
import cn.encmys.ykdz.forest.dailyshop.hook.MythicMobsHook;
import dev.jorel.commandapi.CommandAPICommand;
import io.lumine.mythic.bukkit.MythicBukkit;
import net.Indyuce.mmoitems.MMOItems;
import net.Indyuce.mmoitems.api.Type;
import org.bukkit.Color;
import org.bukkit.FireworkEffect;
import org.bukkit.Material;
import org.bukkit.block.banner.Pattern;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.*;
import org.bukkit.potion.PotionData;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ProductCommand {
    public static ProductCommand INSTANCE = new ProductCommand();

    public CommandAPICommand getShopCommand() {
        return new CommandAPICommand("product")
                .withSubcommands(
                        getProductCheckCommand()
                );
    }

    private CommandAPICommand getProductCheckCommand() {
        return new CommandAPICommand("check")
                .withPermission("dailyshop.command.product.check")
                .executesPlayer((player, args) -> {
                    Map<String, String> vars = new HashMap<>();
                    ItemStack item = PlayerUtils.getItemInMainHand(player);
                    List<String> keyValue = new ArrayList<>() {{
                        add("<yellow>The following are possible configuration key value obtained from the item.");
                        add("<yellow>Please pay attention to the <red>yml format <yellow>when copying to the configuration file:");
                    }};
                    // Banner
                    if (item.getType().toString().contains("BANNER")) {
                        vars.put("keys", "base, banner-patterns");
                        BannerMeta meta = (BannerMeta) item.getItemMeta();
                        if (meta == null) {
                            DailyShop.ADVENTURE_MANAGER.sendMessageWithPrefix(player, MessageConfig.messages_command_product_check_failure_nullMeta);
                            return;
                        }
                        List<Pattern> patterns = meta.getPatterns();
                        keyValue.add("<#C28456>base: <#346659>" + item.getType());
                        keyValue.add("<#C28456>banner-patterns:");
                        for (Pattern pattern : patterns) {
                            keyValue.add("  <#346659>-\"" + pattern.getColor() + ":" + pattern.getPattern() + "\"");
                        }
                    }
                    // Potion
                    else if (item.getType().toString().contains("POTION")) {
                        vars.put("keys", "base");
                        String type = item.getType().toString().replace("_POTION", "");
                        if (type.equals("POTION")) {
                            type = "DRINK";
                        }
                        PotionMeta meta = (PotionMeta) item.getItemMeta();
                        if (meta == null) {
                            DailyShop.ADVENTURE_MANAGER.sendMessageWithPrefix(player, MessageConfig.messages_command_product_check_failure_nullMeta);
                            return;
                        }
                        PotionData data = meta.getBasePotionData();
                        String finalType = type;
                        keyValue.add("<#C28456>base: <#346659>POTION:" + finalType + ":" + data.getType() + ":" + data.isUpgraded() + ":" + data.isExtended());
                    }
                    // Firework
                    else if (item.getType() == Material.FIREWORK_ROCKET) {
                        vars.put("keys", "base, firework-effects");
                        FireworkMeta meta = (FireworkMeta) item.getItemMeta();
                        if (meta == null) {
                            DailyShop.ADVENTURE_MANAGER.sendMessageWithPrefix(player, MessageConfig.messages_command_product_check_failure_nullMeta);
                            return;
                        }
                        keyValue.add("<#C28456>base: <#346659>FIREWORK:" + meta.getPower());
                        keyValue.add("<#C28456>firework-effects:");
                        for (FireworkEffect effect : meta.getEffects()) {
                            List<Color> colors = effect.getColors();
                            List<Color> fadeColors = effect.getFadeColors();
                            boolean trail = effect.hasTrail();
                            boolean flicker = effect.hasFlicker();
                            FireworkEffect.Type type = effect.getType();
                            StringBuilder builder = new StringBuilder("  <#346659>- \"-t:" + type);

                            // 处理 -c 和 -fc 表格样式
                            List<String> hexColors = colors
                                    .stream()
                                    .map(ColorUtils::getHex)
                                    .toList();

                            List<String> hexFadeColors = fadeColors
                                    .stream()
                                    .map(ColorUtils::getHex)
                                    .toList();

                            if (!hexColors.isEmpty()) {
                                builder.append(" -c:").append(hexColors);
                            }

                            if (!fadeColors.isEmpty()) {
                                builder.append(" -cf:").append(hexFadeColors);
                            }

                            if (trail) {
                                builder.append(" -trail:true");
                            }
                            if (flicker) {
                                builder.append(" -flicker:true");
                            }
                            keyValue.add(builder.append("\"").toString());
                        }
                    }
                    // Axolotl Bucket
                    else if (item.getType() == Material.AXOLOTL_BUCKET) {
                        AxolotlBucketMeta meta = (AxolotlBucketMeta) item.getItemMeta();
                        if (meta != null) {
                            vars.put("keys", "base");
                            keyValue.add("<#C28456>base: <#346659>AXOLOTL_BUCKET:" + meta.getVariant().name());
                        }
                    }
                    // Tropical Fish Bucket
                    else if (item.getType() == Material.TROPICAL_FISH_BUCKET) {
                        TropicalFishBucketMeta meta = (TropicalFishBucketMeta) item.getItemMeta();
                        if (meta != null && meta.hasVariant()) {
                            vars.put("keys", "base");
                            keyValue.add("<#C28456>base: <#346659>TROPICAL_FISH_BUCKET:" + meta.getPattern().name() + ":" + meta.getBodyColor().name() + ":" + meta.getPatternColor().name());
                        }
                    }
                    // MMOItems
                    else if (MMOItemsHook.isHooked() && MMOItems.getID(item) != null) {
                        Type itemType = Type.get(MMOItems.getTypeName(item));
                        String itemId = MMOItems.getID(item);
                        if (itemType != null && itemId != null) {
                            vars.put("keys", "base");
                            keyValue.add("<#C28456>base: <#346659>MI:" + itemType.getId() + ":" + itemId);
                        }
                    }
                    // MythicMobs
                    else if (MythicMobsHook.isHooked() && MythicBukkit.inst().getItemManager().isMythicItem(item)) {
                        String id = MythicBukkit.inst().getItemManager().getMythicTypeFromItem(item);
                        vars.put("keys", "base");
                        keyValue.add("<#C28456>base: <#346659>MM:" + id);
                    }
                    // 原版物品 base
                    else {
                        vars.put("keys", "base");
                        keyValue.add("<#C28456>base: <#346659>" + item.getType());
                    }
                    for (String out : keyValue) {
                        DailyShop.ADVENTURE_MANAGER.sendConsoleMessage(out);
                    }
                    if (!keyValue.isEmpty()) {
                        DailyShop.ADVENTURE_MANAGER.sendMessageWithPrefix(player, TextUtils.parseInternalVar(MessageConfig.messages_command_product_check_success, vars));
                        return;
                    }
                    DailyShop.ADVENTURE_MANAGER.sendMessageWithPrefix(player, MessageConfig.messages_command_product_check_failure_nullMeta);
                });
    }
}
