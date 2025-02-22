package cn.encmys.ykdz.forest.hyphashop.utils;

import cn.encmys.ykdz.forest.hyphashop.config.record.misc.SoundRecord;
import net.kyori.adventure.key.Key;
import org.bukkit.Registry;
import org.bukkit.Sound;
import org.jetbrains.annotations.NotNull;

public class RecordUtils {
    public static @NotNull SoundRecord fromSoundData(@NotNull String soundData) {
        if (soundData.isBlank()) return new SoundRecord(Sound.BLOCK_ANVIL_FALL, 1, 1, true);

        String[] data = soundData.split(":");
        Registry<@NotNull Sound> soundRegistry = Registry.SOUNDS;
        Sound sound = Sound.ENTITY_VILLAGER_YES;
        float volume = 1;
        float pitch = 1;
        if (data.length >= 1) {
            try {
                sound = soundRegistry.get(Key.key(data[0]));
            } catch (Exception ignored) {}
            if (sound == null) {
                return new SoundRecord(Sound.BLOCK_ANVIL_FALL, volume, pitch, true);
            }
        }
        if (data.length == 2) {
            volume = Float.parseFloat(data[1]);
        }
        if (data.length == 3) {
            pitch = Float.parseFloat(data[2]);
        }
        return new SoundRecord(sound, volume, pitch, false);
    }
}
