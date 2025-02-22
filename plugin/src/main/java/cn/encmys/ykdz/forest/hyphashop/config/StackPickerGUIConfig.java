package cn.encmys.ykdz.forest.hyphashop.config;

import cn.encmys.ykdz.forest.hyphashop.api.HyphaShop;
import cn.encmys.ykdz.forest.hyphashop.config.record.gui.StackPickerGUIRecord;
import cn.encmys.ykdz.forest.hyphashop.config.record.misc.SoundRecord;
import cn.encmys.ykdz.forest.hyphashop.utils.ConfigUtils;
import cn.encmys.ykdz.forest.hyphashop.utils.LogUtils;
import cn.encmys.ykdz.forest.hyphashop.utils.RecordUtils;
import cn.encmys.ykdz.forest.hyphashop.utils.TextUtils;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.configuration.file.YamlConfiguration;
import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.io.IOException;

public class StackPickerGUIConfig {
    private static final String stackPickerGUIPath = HyphaShop.INSTANCE.getDataFolder() + "/gui/stack-picker.yml";
    private static final YamlConfiguration stackPickerGUIConfig = new YamlConfiguration();

    public static void load() {
        File file = new File(stackPickerGUIPath);

        if (!file.exists()) {
            HyphaShop.INSTANCE.saveResource("gui/stack-picker.yml", false);
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
                mainSection.getString("title", ""),
                TextUtils.parseTimeToTicks(mainSection.getString("title-update-period", "0s")),
                mainSection.getStringList("layout"),
                ConfigUtils.getIconDecorators(mainSection.getConfigurationSection("icons"))
        );
    }

    public static SoundRecord getSoundRecord(@NotNull String soundKey) {
        String soundData = stackPickerGUIConfig.getString("sounds." + soundKey);
        return RecordUtils.fromSoundData(soundData == null ? "" : soundData);
    }
}
