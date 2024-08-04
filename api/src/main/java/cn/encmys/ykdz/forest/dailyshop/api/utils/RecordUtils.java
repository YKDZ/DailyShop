package cn.encmys.ykdz.forest.dailyshop.api.utils;

import cn.encmys.ykdz.forest.dailyshop.api.config.record.misc.SoundRecord;
import org.bukkit.Sound;

public class RecordUtils {
    public static SoundRecord fromSoundData(String soundData) {
        if (soundData == null) {
            return null;
        }
        String[] data = soundData.split(":");
        Sound sound = Sound.ENTITY_VILLAGER_YES;
        float volume = 1;
        float pitch = 1;
        if (data.length >= 1) {
            sound = EnumUtils.getEnumFromName(Sound.class, data[0]);
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
