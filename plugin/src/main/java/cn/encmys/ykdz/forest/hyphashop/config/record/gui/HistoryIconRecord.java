package cn.encmys.ykdz.forest.hyphashop.config.record.gui;

import cn.encmys.ykdz.forest.hyphashop.api.item.decorator.BaseItemDecorator;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public record HistoryIconRecord(@NotNull String formatName, @NotNull List<String> formatLore,
                                @NotNull String formatOrderContentLine, @NotNull String formatInvalidOrderContentLine,
                                @Nullable BaseItemDecorator miscPlaceholderIcon) {
}
