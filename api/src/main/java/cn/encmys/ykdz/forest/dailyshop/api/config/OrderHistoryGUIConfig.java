package cn.encmys.ykdz.forest.dailyshop.api.config;

import cn.encmys.ykdz.forest.dailyshop.api.DailyShop;
import cn.encmys.ykdz.forest.dailyshop.api.config.record.gui.HistoryIconRecord;
import cn.encmys.ykdz.forest.dailyshop.api.config.record.gui.OrderHistoryGUIRecord;
import cn.encmys.ykdz.forest.dailyshop.api.config.record.misc.SoundRecord;
import cn.encmys.ykdz.forest.dailyshop.api.utils.ConfigUtils;
import cn.encmys.ykdz.forest.dailyshop.api.utils.RecordUtils;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.configuration.file.YamlConfiguration;
import org.jetbrains.annotations.NotNull;
import xyz.xenondevs.invui.gui.structure.Markers;

import java.io.File;
import java.io.IOException;

public class OrderHistoryGUIConfig {
    private static final String cartGUIPath = DailyShop.INSTANCE.getDataFolder() + "/gui/order-history.yml";
    private static final YamlConfiguration cartGUIConfig = new YamlConfiguration();

    public static void load() {
        File file = new File(cartGUIPath);

        if (!file.exists()) {
            DailyShop.INSTANCE.saveResource("gui/order-history.yml", false);
        }

        try {
            cartGUIConfig.load(file);
        } catch (IOException | InvalidConfigurationException error) {
            error.printStackTrace();
        }
    }

    public static YamlConfiguration getConfig() {
        return cartGUIConfig;
    }

    @NotNull
    public static OrderHistoryGUIRecord getGUIRecord() {
        ConfigurationSection mainSection = cartGUIConfig.getConfigurationSection("order-history");
        if (mainSection == null) {
            throw new RuntimeException("Attempted to read gui information, but the configuration section is empty.");
        }
        ConfigurationSection historyIconSection = mainSection.getConfigurationSection("history-icon");
        if (historyIconSection == null) {
            throw new RuntimeException("Attempted to read gui information, but the configuration section is empty.");
        }
        return new OrderHistoryGUIRecord(
                mainSection.getString("title", "Order History for player {player-name}"),
                mainSection.getString("scroll-mode", "HORIZONTAL").equals("HORIZONTAL") ? Markers.CONTENT_LIST_SLOT_HORIZONTAL : Markers.CONTENT_LIST_SLOT_VERTICAL,
                mainSection.getStringList("layout"),
                ConfigUtils.getIconRecords(mainSection.getConfigurationSection("icons")),
                new HistoryIconRecord(
                        historyIconSection.getString("format.name", "<dark_gray>Name: <reset>{name} <dark_gray>x <white>{amount}"),
                        historyIconSection.getStringList("format.lore"),
                        historyIconSection.getString("format.order-contents-line", " <dark_gray>- <white>{name} <gray>x <white>{amount}"),
                        historyIconSection.getInt("misc.date-precision", 5)
                )
        );
    }

    public static SoundRecord getSoundRecord(String soundKey) {
        String soundData = cartGUIConfig.getString("sounds." + soundKey);
        return RecordUtils.fromSoundData(soundData);
    }
}
