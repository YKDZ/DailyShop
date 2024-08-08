package cn.encmys.ykdz.forest.dailyshop.api.config.record.gui;

import org.bukkit.event.inventory.ClickType;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public record CartProductIconRecord(@NotNull String formatName, @NotNull List<String> formatLore,
                                    long updatePeriod, @Nullable ClickType featuresAdd1Stack,
                                    @Nullable ClickType featuresRemove1Stack, @Nullable ClickType featuresRemoveAll,
                                    @Nullable ClickType featuresInputInAnvil) {
}
