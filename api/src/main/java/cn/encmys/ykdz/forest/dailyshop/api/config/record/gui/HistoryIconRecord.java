package cn.encmys.ykdz.forest.dailyshop.api.config.record.gui;

import cn.encmys.ykdz.forest.dailyshop.api.config.record.misc.IconRecord;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public record HistoryIconRecord(@NotNull String formatName, @NotNull List<String> formatLore,
                                @NotNull String formatOrderContentsLine, @Nullable IconRecord miscPlaceholderIcon) {
}
