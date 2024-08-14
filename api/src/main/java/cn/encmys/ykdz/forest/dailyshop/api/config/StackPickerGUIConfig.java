package cn.encmys.ykdz.forest.dailyshop.api.config;

import cn.encmys.ykdz.forest.dailyshop.api.DailyShop;
import cn.encmys.ykdz.forest.dailyshop.api.config.record.gui.StackPickerGUIRecord;
import cn.encmys.ykdz.forest.dailyshop.api.config.record.misc.SoundRecord;
import cn.encmys.ykdz.forest.dailyshop.api.utils.ConfigUtils;
import cn.encmys.ykdz.forest.dailyshop.api.utils.LogUtils;
import cn.encmys.ykdz.forest.dailyshop.api.utils.RecordUtils;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.configuration.file.YamlConfiguration;
import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.io.IOException;

public class StackPickerGUIConfig {
    private static final String stackPickerGUIPath = DailyShop.INSTANCE.getDataFolder() + "/gui/stack-picker.yml";
    private static final YamlConfiguration stackPickerGUIConfig = new YamlConfiguration();

    public static void load() {
        File file = new File(stackPickerGUIPath);

        if (!file.exists()) {
            DailyShop.INSTANCE.saveResource("gui/stack-picker.yml", false);
        }

        try {
            stackPickerGUIConfig.load(file);
        } catch (IOException | InvalidConfigurationException error) {
            LogUtils.error(error.getMessage());
        }
    }

    public static YamlConfiguration getConfig() {
        return stackPickerGUIConfig;
    }

    @NotNull
    public static StackPickerGUIRecord getGUIRecord() {
        ConfigurationSection mainSection = stackPickerGUIConfig.getConfigurationSection("stack-picker");
        if (mainSection == null) {
            throw new RuntimeException("Attempted to read gui information, but the configuration section is empty.");
        }
        return new StackPickerGUIRecord(
                mainSection.getString("title", " "),
                mainSection.getStringList("layout"),
                ConfigUtils.getIconRecords(mainSection.getConfigurationSection("icons"))
        );
    }

    public static SoundRecord getSoundRecord(String soundKey) {
        String soundData = stackPickerGUIConfig.getString("sounds." + soundKey);
        return RecordUtils.fromSoundData(soundData);
    }
}
