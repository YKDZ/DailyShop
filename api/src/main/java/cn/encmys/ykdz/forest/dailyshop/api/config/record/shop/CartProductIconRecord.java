package cn.encmys.ykdz.forest.dailyshop.api.config.record.shop;

import org.jetbrains.annotations.NotNull;

import java.util.List;

public record CartProductIconRecord(@NotNull String formatName, @NotNull List<String> formatLore) {
}
