package cn.encmys.ykdz.forest.hyphashop.command.sub;

import com.mojang.brigadier.tree.CommandNode;
import io.papermc.paper.command.brigadier.CommandSourceStack;
import io.papermc.paper.command.brigadier.Commands;

public class ProductCommand {
    public static CommandNode<CommandSourceStack> getShopCommand() {
        return Commands.literal("product")
                .build();
    }

    // TODO 自动生成配置
//    private CommandAPICommand getProductCheckCommand() {
//        return new CommandAPICommand("check")
//                .withPermission("hyphashop.command.product.check")
//                .executesPlayer((player, args) -> {
//                    Map<String, Object> vars = new HashMap<>();
//                    ItemStack item = PlayerUtils.getItemInMainHand(player);
//
//                    Map<String, Object> configs = new LinkedHashMap<>();
//
//                    HyphaAdventureUtils.sendMessage(player, TextUtils.decorateText(
//                            "<yellow>The following are possible configuration key value obtained from the base.", player, null
//                    ));
//                    HyphaAdventureUtils.sendMessage(player, TextUtils.decorateText(
//                            "<yellow>Please pay attention to the <red>yml format <yellow>when copying to the configuration file:", player, null
//                    ));
//
//                    ItemMeta meta = item.getItemMeta();
//
//                    // 原版物品
//                    configs.put("base", item.getType());
//                    configs.put("amount", item.getAmount());
//
//                    // Banner
//                    if (item.getType().toString().contains("BANNER")) {
//                        if (meta == null) {
//                            HyphaAdventureUtils.sendMessage(player, MessageConfig.messages_prefix + MessageConfig.messages_command_product_check_failure_nullMeta);
//                            return;
//                        }
//                        configs.put("base", item.getType());
//                        List<Pattern> patterns = ((BannerMeta) meta).getPatterns();
//                        Map<DyeColor, PatternType> patternConfigs = new LinkedHashMap<>();
//                        for (Pattern pattern : patterns) {
//                            patternConfigs.put(pattern.getColor(), pattern.getPattern());
//                        }
//                        configs.put("banner-patterns",  patternConfigs);
//                    }
//                    // Potion
//                    if (item.getType().toString().contains("POTION")) {
//                        String potionMaterial = item.getType().toString().replace("_POTION", "");
//                        if (potionMaterial.equals("POTION")) {
//                            potionMaterial = "DRINK";
//                        }
//                        if (meta == null) {
//                            HyphaAdventureUtils.sendMessage(player, MessageConfig.messages_prefix + MessageConfig.messages_command_product_check_failure_nullMeta);
//                            return;
//                        }
//                        PotionType potionType = ((PotionMeta) meta).getBasePotionType();
//                        if (potionType == null) return;
//                        keyValue.add("base: <#346659>POTION:" + potionMaterial + ":" + potionType.name().toLowerCase());
//                    }
//                    // Firework
//                    if (item.getType() == Material.FIREWORK_ROCKET) {
//                        if (meta == null) {
//                            HyphaAdventureUtils.sendMessage(player, MessageConfig.messages_prefix + MessageConfig.messages_command_product_check_failure_nullMeta);
//                            return;
//                        }
//                        keyValue.add("base: <#346659>FIREWORK:" + ((FireworkMeta) meta).getPower());
//                        keyValue.add("firework-effects:");
//                        for (FireworkEffect effect : ((FireworkMeta) meta).getEffects()) {
//                            List<Color> colors = effect.getColors();
//                            List<Color> fadeColors = effect.getFadeColors();
//                            boolean trail = effect.hasTrail();
//                            boolean flicker = effect.hasFlicker();
//                            FireworkEffect.Type type = effect.getType();
//                            StringBuilder builder = new StringBuilder("  <#346659>- \"-t:" + type);
//
//                            // 处理 -c 和 -fc 表格样式
//                            List<String> hexColors = colors
//                                    .stream()
//                                    .map(ColorUtils::getHex)
//                                    .toList();
//
//                            List<String> hexFadeColors = fadeColors
//                                    .stream()
//                                    .map(ColorUtils::getHex)
//                                    .toList();
//
//                            if (!hexColors.isEmpty()) {
//                                builder.append(" -c:").append(hexColors);
//                            }
//
//                            if (!fadeColors.isEmpty()) {
//                                builder.append(" -cf:").append(hexFadeColors);
//                            }
//
//                            if (trail) {
//                                builder.append(" -trail:true");
//                            }
//                            if (flicker) {
//                                builder.append(" -flicker:true");
//                            }
//                            keyValue.add(builder.append("\"").toString());
//                        }
//                    }
//                    // Axolotl Bucket
//                    if (item.getType() == Material.AXOLOTL_BUCKET) {
//                        if (meta != null) {
//                            keyValue.add("base: <#346659>AXOLOTL_BUCKET:" + ((AxolotlBucketMeta) meta).getVariant().name());
//                        }
//                    }
//                    // Tropical Fish Bucket
//                    if (item.getType() == Material.TROPICAL_FISH_BUCKET) {
//                        if (meta != null && ((TropicalFishBucketMeta) meta).hasVariant()) {
//                            keyValue.add("base: <#346659>TROPICAL_FISH_BUCKET:" + ((TropicalFishBucketMeta) meta).getPattern().name() + ":" + meta.getBodyColor().name() + ":" + meta.getPatternColor().name());
//                        }
//                    }
//                    // Armor with Trim
//                    if (meta instanceof ArmorMeta) {
//                        keyValue.add("base: <#346659>" + item.getType().name().toLowerCase());
//                    }
//                    // MMOItems
//                    if (MMOItemsHook.isHooked() && MMOItems.getID(item) != null) {
//                        Type itemType = Type.get(MMOItems.getTypeName(item));
//                        String itemId = MMOItems.getID(item);
//                        if (itemType != null && itemId != null) {
//                            keyValue.add("base: <#346659>MI:" + itemType.getId() + ":" + itemId);
//                        }
//                    }
//                    // MythicMobs
//                    if (MythicMobsHook.isHooked()) {
//                        try (MythicBukkit mythicBukkit = MythicBukkit.inst()) {
//                            if (!mythicBukkit.getItemManager().isMythicItem(item)) {
//                                return;
//                            }
//                            String id = mythicBukkit.getItemManager().getMythicTypeFromItem(item);
//                            keyValue.add("base: <#346659>MM:" + id);
//                        }
//                    }
//
//                    for (String out : keyValue) {
//                        HyphaAdventureUtils.sendConsoleMessage(out);
//                    }
//                    HyphaAdventureUtils.sendMessage(player, TextUtils.decorateText(MessageConfig.messages_prefix + MessageConfig.messages_command_product_check_success, player, vars));
//                });
//    }
}
