package cn.encmys.ykdz.forest.dailyshop.api.config.record.misc;

import org.bukkit.configuration.ConfigurationSection;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.Map;

public record IconRecord(char key, @NotNull String base,
                         @Nullable String name, @Nullable List<String> lore,
                         int amount, long updatePeriod,
                         int customModalData, @Nullable ConfigurationSection commands,
                         @Nullable List<String> itemFlags, @Nullable List<String> bannerPatterns,
                         @Nullable List<String> fireworkEffects, @Nullable List<String> potionEffects,
                         @Nullable ConfigurationSection features,
                         // Inspired from TrMenu
                         @NotNull Map<String, IconRecord> conditionIcons) {
}
