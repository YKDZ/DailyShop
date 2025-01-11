package cn.encmys.ykdz.forest.dailyshop.api.config.record.misc;

import org.bukkit.configuration.ConfigurationSection;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.Map;

public record IconRecord(char key, @NotNull String base,
                         @Nullable String name, @Nullable List<String> lore,
                         String amount, long updatePeriod,
                         int customModalData, @Nullable ConfigurationSection commands,
                         @Nullable List<String> itemFlagsData, @Nullable List<String> bannerPatternsData,
                         @Nullable List<String> fireworkEffectsData, @Nullable List<String> potionEffectsData,
                         @Nullable List<String> enchantmentsData,
                         @Nullable ConfigurationSection features,
                         // Inspired from TrMenu
                         @NotNull Map<String, IconRecord> conditionIcons) {
}
