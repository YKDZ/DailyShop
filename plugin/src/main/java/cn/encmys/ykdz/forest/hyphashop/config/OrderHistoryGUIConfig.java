package cn.encmys.ykdz.forest.hyphashop.config;

import cn.encmys.ykdz.forest.hyphashop.api.HyphaShop;
import cn.encmys.ykdz.forest.hyphashop.config.record.gui.HistoryIconRecord;
import cn.encmys.ykdz.forest.hyphashop.config.record.gui.OrderHistoryGUIRecord;
import cn.encmys.ykdz.forest.hyphashop.config.record.misc.SoundRecord;
import cn.encmys.ykdz.forest.hyphashop.utils.ConfigUtils;
import cn.encmys.ykdz.forest.hyphashop.utils.LogUtils;
import cn.encmys.ykdz.forest.hyphashop.utils.RecordUtils;
import cn.encmys.ykdz.forest.hyphashop.utils.TextUtils;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.configuration.file.YamlConfiguration;
import org.jetbrains.annotations.NotNull;
import xyz.xenondevs.invui.gui.Markers;

import java.io.File;
import java.io.IOException;

public class OrderHistoryGUIConfig {
    private static final String orderHistoryGUIPath = HyphaShop.INSTANCE.getDataFolder() + "/gui/order-history.yml";
    private static final YamlConfiguration orderHistoryGUIConfig = new YamlConfiguration();

    public static void load() {
        File file = new File(orderHistoryGUIPath);

        if (!file.exists()) {
            HyphaShop.INSTANCE.saveResource("gui/order-history.yml", false);
        }

        try {
            orderHistoryGUIConfig.load(file);
        } catch (IOException | InvalidConfigurationException error) {
            LogUtils.error(error.getMessage());
        }
    }

    public static YamlConfiguration getConfig() {
        return orderHistoryGUIConfig;
    }

    @NotNull
    public static OrderHistoryGUIRecord getGUIRecord() {
        ConfigurationSection mainSection = orderHistoryGUIConfig.getConfigurationSection("order-history");
        if (mainSection == null) {
            throw new RuntimeException("Attempted to read gui information, but the configuration section is empty.");
        }
        ConfigurationSection historyIconSection = mainSection.getConfigurationSection("history-icon");
        if (historyIconSection == null) {
            throw new RuntimeException("Attempted to read gui information, but the configuration section is empty.");
        }
        return new OrderHistoryGUIRecord(
                mainSection.getString("title", ""),
                TextUtils.parseTimeToTicks(mainSection.getString("title-update-period", "0s")),
                mainSection.contains("scroll-mode") ? mainSection.getString("scroll-mode", "HORIZONTAL").equals("HORIZONTAL") ? Markers.CONTENT_LIST_SLOT_HORIZONTAL : Markers.CONTENT_LIST_SLOT_VERTICAL : null,
                mainSection.contains("page-mode") ? mainSection.getString("page-mode", "HORIZONTAL").equals("HORIZONTAL") ? Markers.CONTENT_LIST_SLOT_HORIZONTAL : Markers.CONTENT_LIST_SLOT_VERTICAL : null,
                mainSection.getStringList("layout"),
                ConfigUtils.getIconDecorators(mainSection.getConfigurationSection("icons")),
                new HistoryIconRecord(
                        historyIconSection.getString("format.name", "<dark_gray>Name: <reset>{name} <dark_gray>x <white>{amount}"),
                        historyIconSection.getStringList("format.lore"),
                        historyIconSection.getString("format.order-content-line", " <dark_gray>- <white>{name} <gray>x <white>{amount}"),
                        historyIconSection.getString("format.invalid-order-content-line", " <dark_gray>- <white>{id} <gray>do not exist"),
                        ConfigUtils.parseDecorator(
                                historyIconSection.getConfigurationSection("misc.placeholder-icon")
                        )
                )
        );
    }

    public static SoundRecord getSoundRecord(@NotNull String soundKey) {
        String soundData = orderHistoryGUIConfig.getString("sounds." + soundKey);
        return RecordUtils.fromSoundData(soundData == null ? "" : soundData);
    }
}
