package cn.encmys.ykdz.forest.dailyshop.builder;

import cn.encmys.ykdz.forest.dailyshop.api.gui.ShopRelatedGUI;
import cn.encmys.ykdz.forest.dailyshop.api.gui.icon.Icon;
import cn.encmys.ykdz.forest.dailyshop.api.item.decorator.BaseItemDecorator;
import cn.encmys.ykdz.forest.dailyshop.api.utils.CommandUtils;
import cn.encmys.ykdz.forest.dailyshop.api.utils.TextUtils;
import cn.encmys.ykdz.forest.dailyshop.gui.icon.NormalIcon;
import cn.encmys.ykdz.forest.dailyshop.gui.icon.ScrollIcon;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.jetbrains.annotations.NotNull;
import xyz.xenondevs.invui.gui.ScrollGui;
import xyz.xenondevs.invui.item.Item;
import xyz.xenondevs.invui.item.ItemProvider;
import xyz.xenondevs.invui.item.builder.ItemBuilder;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class NormalIconBuilder {
    public static Item build(@NotNull BaseItemDecorator decorator, @NotNull ShopRelatedGUI gui) {
        Item icon;
        if (decorator.getScroll() == 0) {
            icon = new NormalIcon() {
                @Override
                public ItemProvider getItemProvider() {
                    return new ItemBuilder(
                            new cn.encmys.ykdz.forest.dailyshop.api.utils.ItemBuilder(decorator.getItem().build(null))
                                    .setCustomModelData(decorator.getCustomModelData())
                                    .setItemFlags(decorator.getItemFlags())
                                    .setLore(TextUtils.decorateText(decorator.getLore(), null, null, null))
                                    .setDisplayName(TextUtils.decorateText(decorator.getName(), null, null))
                                    .setBannerPatterns(decorator.getPatternsData())
                                    .setFireworkEffects(decorator.getFireworkEffectData())
                                    .build(decorator.getAmount()));
                }

                @Override
                public void handleClick(@NotNull ClickType clickType, @NotNull Player player, @NotNull InventoryClickEvent event) {
                    Map<String, String> vars = new HashMap<>() {{
                        put("player", player.getName());
                        put("player-uuid", player.getUniqueId().toString());
                        if (gui.getShop() != null) {
                            put("shop", gui.getShop().getId());
                            put("shop-name", gui.getShop().getName());
                        }
                        put("click-type", clickType.name());
                    }};
                    switch (clickType) {
                        case LEFT ->
                                CommandUtils.dispatchCommands(player, decorator.getCommands().getOrDefault(ClickType.LEFT, new ArrayList<>()), vars);
                        case RIGHT ->
                                CommandUtils.dispatchCommands(player, decorator.getCommands().getOrDefault(ClickType.RIGHT, new ArrayList<>()), vars);
                        case SHIFT_LEFT ->
                                CommandUtils.dispatchCommands(player, decorator.getCommands().getOrDefault(ClickType.SHIFT_LEFT, new ArrayList<>()), vars);
                        case SHIFT_RIGHT ->
                                CommandUtils.dispatchCommands(player, decorator.getCommands().getOrDefault(ClickType.SHIFT_RIGHT, new ArrayList<>()), vars);
                        case DROP ->
                                CommandUtils.dispatchCommands(player, decorator.getCommands().getOrDefault(ClickType.DROP, new ArrayList<>()), vars);
                        case DOUBLE_CLICK ->
                                CommandUtils.dispatchCommands(player, decorator.getCommands().getOrDefault(ClickType.DOUBLE_CLICK, new ArrayList<>()), vars);
                        case MIDDLE ->
                                CommandUtils.dispatchCommands(player, decorator.getCommands().getOrDefault(ClickType.MIDDLE, new ArrayList<>()), vars);
                        default -> {
                        }
                    }
                }
            };
        } else {
            icon = new ScrollIcon(decorator.getScroll()) {
                @Override
                public ItemProvider getItemProvider(ScrollGui<?> gui) {
                    HashMap<String, String> vars = new HashMap<>() {{
                        put("current-scroll", String.valueOf(gui.getCurrentLine() + 1));
                        put("max-scroll", String.valueOf(gui.getMaxLine() + 1 - decorator.getScrollShift() + 1));
                    }};
                    return new ItemBuilder(
                            new cn.encmys.ykdz.forest.dailyshop.api.utils.ItemBuilder(decorator.getItem().build(null))
                                    .setCustomModelData(decorator.getCustomModelData())
                                    .setItemFlags(decorator.getItemFlags())
                                    .setLore(TextUtils.decorateText(decorator.getLore(), null, vars, null))
                                    .setDisplayName(TextUtils.decorateText(decorator.getName(), null, vars))
                                    .setFireworkEffects(decorator.getFireworkEffectData())
                                    .build(decorator.getAmount())
                    );
                }
            };
        }

        // Auto Update
        if (decorator.getPeriod() > 0) {
            ((Icon) icon).startUpdater(decorator.getPeriod());
        }

        return icon;
    }
}
