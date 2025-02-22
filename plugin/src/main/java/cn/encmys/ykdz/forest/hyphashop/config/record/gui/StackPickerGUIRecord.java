package cn.encmys.ykdz.forest.hyphashop.config.record.gui;

import cn.encmys.ykdz.forest.hyphashop.api.item.decorator.BaseItemDecorator;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.Map;

public record StackPickerGUIRecord(@NotNull String title, long timeUpdatePeriod, @NotNull List<String> layout,
                                   @Nullable Map<Character, BaseItemDecorator> icons) {
}
