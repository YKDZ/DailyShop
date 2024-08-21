package cn.encmys.ykdz.forest.dailyshop.api.config.record.gui;

import cn.encmys.ykdz.forest.dailyshop.api.config.record.misc.IconRecord;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import xyz.xenondevs.invui.gui.structure.Marker;

import java.util.List;

public record ShopGUIRecord(@NotNull String title, @Nullable Marker scrollMode, @Nullable Marker pageMode,
                            @NotNull List<String> layout, @Nullable List<IconRecord> icons,
                            @NotNull ProductIconRecord productIconRecord) {
}
