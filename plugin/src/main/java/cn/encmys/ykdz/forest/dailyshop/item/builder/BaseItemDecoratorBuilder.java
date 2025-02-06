package cn.encmys.ykdz.forest.dailyshop.item.builder;

import cn.encmys.ykdz.forest.dailyshop.api.config.record.misc.IconRecord;
import cn.encmys.ykdz.forest.dailyshop.api.item.BaseItem;
import cn.encmys.ykdz.forest.dailyshop.api.item.decorator.BaseItemDecorator;
import cn.encmys.ykdz.forest.dailyshop.api.item.decorator.enums.PropertyType;
import cn.encmys.ykdz.forest.dailyshop.api.utils.EnumUtils;
import org.bukkit.event.inventory.ClickType;
import org.jetbrains.annotations.NotNull;

import java.util.HashMap;

public class BaseItemDecoratorBuilder {
    public static @NotNull BaseItemDecorator get(@NotNull IconRecord record) {
        BaseItem item = BaseItemBuilder.get(record.base());
        if (item == null) {
            throw new IllegalArgumentException("Icon " + record.key() + " has invalid base.");
        }
        BaseItemDecorator decorator = new BaseItemDecorator(item)
                .setProperty(PropertyType.NAME, record.name())
                .setProperty(PropertyType.LORE, record.lore())
                .setProperty(PropertyType.AMOUNT, record.amount())
                .setProperty(PropertyType.UPDATE_PERIOD, record.updatePeriod())
                .setProperty(PropertyType.ITEM_FLAGS, record.itemFlagsData())
                .setProperty(PropertyType.CUSTOM_MODEL_DATA, record.customModalData())
                .setProperty(PropertyType.BANNER_PATTERNS, record.bannerPatternsData())
                .setProperty(PropertyType.ENCHANTMENTS, record.enchantmentsData());

        if (record.commands() != null) {
            decorator.setProperty(PropertyType.COMMANDS_DATA, new HashMap<>() {{
                put(ClickType.LEFT, record.commands().getStringList("left"));
                put(ClickType.RIGHT, record.commands().getStringList("right"));
                put(ClickType.SHIFT_LEFT, record.commands().getStringList("shift-left"));
                put(ClickType.SHIFT_RIGHT, record.commands().getStringList("shift-right"));
                put(ClickType.DOUBLE_CLICK, record.commands().getStringList("double-click"));
                put(ClickType.DROP, record.commands().getStringList("drop"));
                put(ClickType.CONTROL_DROP, record.commands().getStringList("control-drop"));
                put(ClickType.MIDDLE, record.commands().getStringList("middle"));
                put(ClickType.SWAP_OFFHAND, record.commands().getStringList("swap-offhand"));
                put(ClickType.NUMBER_KEY, record.commands().getStringList("number-key"));
                put(ClickType.WINDOW_BORDER_LEFT, record.commands().getStringList("window-border-left"));
                put(ClickType.WINDOW_BORDER_RIGHT, record.commands().getStringList("window-border-right"));
            }});
        }
        if (record.features() != null) {
            decorator.setProperty(PropertyType.FEATURE_SCROLL, EnumUtils.getEnumFromName(ClickType.class, record.features().getString("scroll")))
                    .setProperty(PropertyType.FEATURE_SCROLL_AMOUNT, record.features().getInt("scroll-amount", 0))
                    .setProperty(PropertyType.FEATURE_PAGE_CHANGE, EnumUtils.getEnumFromName(ClickType.class, record.features().getString("page-change")))
                    .setProperty(PropertyType.FEATURE_PAGE_CHANGE_AMOUNT, record.features().getInt("page-change-amount", 0))
                    .setProperty(PropertyType.FEATURE_BACK_TO_SHOP, EnumUtils.getEnumFromName(ClickType.class, record.features().getString("back-to-shop")))
                    .setProperty(PropertyType.FEATURE_SETTLE_CART, EnumUtils.getEnumFromName(ClickType.class, record.features().getString("settle-cart")))
                    .setProperty(PropertyType.FEATURE_OPEN_CART, EnumUtils.getEnumFromName(ClickType.class, record.features().getString("open-cart")))
                    .setProperty(PropertyType.FEATURE_SWITCH_SHOPPING_MODE, EnumUtils.getEnumFromName(ClickType.class, record.features().getString("switch-shopping-mode")))
                    .setProperty(PropertyType.FEATURE_SWITCH_CART_MODE, EnumUtils.getEnumFromName(ClickType.class, record.features().getString("switch-cart-mode")))
                    .setProperty(PropertyType.FEATURE_CLEAN_CART, EnumUtils.getEnumFromName(ClickType.class, record.features().getString("clean-cart")))
                    .setProperty(PropertyType.FEATURE_CLEAR_CART, EnumUtils.getEnumFromName(ClickType.class, record.features().getString("clear-cart")))
                    .setProperty(PropertyType.FEATURE_LOAD_MORE_LOG, EnumUtils.getEnumFromName(ClickType.class, record.features().getString("load-more-log")))
                    .setProperty(PropertyType.FEATURE_OPEN_SHOP, EnumUtils.getEnumFromName(ClickType.class, record.features().getString("open-shop")))
                    .setProperty(PropertyType.FEATURE_OPEN_SHOP_TARGET, record.features().getString("open-shop-target"))
                    .setProperty(PropertyType.FEATURE_OPEN_ORDER_HISTORY, EnumUtils.getEnumFromName(ClickType.class, record.features().getString("open-order-history")));

        }
        return decorator;
    }
}
