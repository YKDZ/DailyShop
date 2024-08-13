package cn.encmys.ykdz.forest.dailyshop.api.config.record.gui;

import org.bukkit.event.inventory.ClickType;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public record ProductIconRecord(@NotNull String formatName, @NotNull List<String> formatLore,
                                @NotNull String formatBundleContentsLine, @NotNull String miscDisabledPrice,
                                long updatePeriod, @Nullable ClickType featuresSellTo,
                                @Nullable ClickType featuresBuyFrom, @Nullable ClickType featuresBuyAllFrom,
                                @Nullable ClickType featuresAdd1ToCart, @Nullable ClickType featuresRemove1FromCart,
                                @Nullable ClickType featuresRemoveAllFromCart) {
}
