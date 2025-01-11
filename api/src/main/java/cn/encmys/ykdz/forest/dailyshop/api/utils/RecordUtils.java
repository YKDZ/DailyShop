package cn.encmys.ykdz.forest.dailyshop.api.utils;

import cn.encmys.ykdz.forest.dailyshop.api.config.record.misc.SoundRecord;
import net.kyori.adventure.key.Key;
import org.bukkit.Registry;
import org.bukkit.Sound;

public class RecordUtils {
    public static SoundRecord fromSoundData(String soundData) {
        if (soundData == null) {
            return null;
        }
        String[] data = soundData.split(":");
        Registry<Sound> soundRegistry = Registry.SOUNDS;
        Sound sound = Sound.ENTITY_VILLAGER_YES;
        float volume = 1;
        float pitch = 1;
        if (data.length >= 1) {
            sound = soundRegistry.get(Key.key(data[0]));
            if (sound == null) {
                return null;
            }
        }
        if (data.length == 2) {
            volume = Float.parseFloat(data[1]);
        }
        if (data.length == 3) {
            pitch = Float.parseFloat(data[2]);
        }
        return new SoundRecord(sound, volume, pitch);
    }
}
