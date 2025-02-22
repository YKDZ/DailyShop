package cn.encmys.ykdz.forest.hyphashop.config.record.gui;

import cn.encmys.ykdz.forest.hyphashop.api.item.decorator.BaseItemDecorator;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import xyz.xenondevs.invui.gui.Marker;

import java.util.List;
import java.util.Map;

public record ShopGUIRecord(@NotNull String title, long timeUpdatePeriod, @Nullable Marker scrollMode, @Nullable Marker pageMode,
                            @NotNull List<String> layout, @Nullable Map<Character, BaseItemDecorator> icons,
                            @NotNull ProductIconRecord productIconRecord) {
}
