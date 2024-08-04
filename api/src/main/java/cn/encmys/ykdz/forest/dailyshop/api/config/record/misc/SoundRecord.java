package cn.encmys.ykdz.forest.dailyshop.api.config.record.misc;

import org.bukkit.Sound;
import org.jetbrains.annotations.NotNull;

public record SoundRecord(@NotNull Sound sound, float volume,
                          float pitch) {
}
