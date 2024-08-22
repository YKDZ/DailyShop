package cn.encmys.ykdz.forest.dailyshop.api.config.record.gui;

import org.bukkit.Material;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public record HistoryIconRecord(@NotNull String formatName, @NotNull List<String> formatLore,
                                @NotNull String formatOrderContentsLine, @NotNull Material miscPlaceholderMaterial) {
}
