package cn.encmys.ykdz.forest.dailyshop.command.sub;

import cn.encmys.ykdz.forest.dailyshop.api.DailyShop;
import cn.encmys.ykdz.forest.dailyshop.api.config.MessageConfig;
import cn.encmys.ykdz.forest.dailyshop.util.ColorUtils;
import cn.encmys.ykdz.forest.dailyshop.util.PlayerUtils;
import cn.encmys.ykdz.forest.dailyshop.util.TextUtils;
import dev.jorel.commandapi.CommandAPICommand;
import org.bukkit.Color;
import org.bukkit.FireworkEffect;
import org.bukkit.Material;
import org.bukkit.block.banner.Pattern;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.BannerMeta;
import org.bukkit.inventory.meta.FireworkMeta;
import org.bukkit.inventory.meta.PotionMeta;
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
                    // Send Config key value list
                    for (String out : keyValue) {
                        DailyShop.ADVENTURE_MANAGER.sendConsoleMessage(out);
                    }
                    if (!keyValue.isEmpty()) {
                        DailyShop.ADVENTURE_MANAGER.sendMessageWithPrefix(player, TextUtils.parseInternalVariables(MessageConfig.messages_command_product_check_success, vars));
                        return;
                    }
                    DailyShop.ADVENTURE_MANAGER.sendMessageWithPrefix(player, MessageConfig.messages_command_product_check_failure_nullMeta);
                });
    }

}
