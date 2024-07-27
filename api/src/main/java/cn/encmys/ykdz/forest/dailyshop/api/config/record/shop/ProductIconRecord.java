package cn.encmys.ykdz.forest.dailyshop.api.config.record.shop;

import org.jetbrains.annotations.NotNull;

import java.util.List;

public record ProductIconRecord(@NotNull String formatName, @NotNull List<String> formatLore,
                                @NotNull String formatBundleContentsLine, @NotNull String miscDisabledPrice) {
}
