package cn.encmys.ykdz.forest.dailyshop.api.config;

import cn.encmys.ykdz.forest.dailyshop.api.DailyShop;
import cn.encmys.ykdz.forest.dailyshop.api.config.record.gui.CartGUIRecord;
import cn.encmys.ykdz.forest.dailyshop.api.config.record.misc.SoundRecord;
import cn.encmys.ykdz.forest.dailyshop.api.config.record.shop.CartProductIconRecord;
import cn.encmys.ykdz.forest.dailyshop.api.utils.ConfigUtils;
import cn.encmys.ykdz.forest.dailyshop.api.utils.EnumUtils;
import cn.encmys.ykdz.forest.dailyshop.api.utils.RecordUtils;
import cn.encmys.ykdz.forest.dailyshop.api.utils.TextUtils;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.event.inventory.ClickType;
import org.jetbrains.annotations.NotNull;
import xyz.xenondevs.invui.gui.structure.Markers;

import java.io.File;
import java.io.IOException;

public class CartGUIConfig {
    private static final String path = DailyShop.INSTANCE.getDataFolder() + "/gui";
    private static final String cartGUIPath = path + "/cart.yml";
    private static final YamlConfiguration cartGUIConfig = new YamlConfiguration();

    public static void load() {
        File file = new File(cartGUIPath);

        if (!file.exists()) {
            DailyShop.INSTANCE.saveResource("gui/cart.yml", false);
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
    public static CartGUIRecord getGUIRecord() {
        ConfigurationSection mainSection = cartGUIConfig.getConfigurationSection("cart");
        if (mainSection == null) {
            throw new RuntimeException("Attempted to read gui information, but the configuration section is empty.");
        }
        ConfigurationSection cartIconSection = mainSection.getConfigurationSection("cart-product-icon");
        if (cartIconSection == null) {
            throw new RuntimeException("Attempted to read gui information, but the configuration section is empty.");
        }
        return new CartGUIRecord(
                mainSection.getString("title", "Cart for player {player-name}"),
                mainSection.getString("scroll-mode", "HORIZONTAL").equals("HORIZONTAL") ? Markers.CONTENT_LIST_SLOT_HORIZONTAL : Markers.CONTENT_LIST_SLOT_VERTICAL,
                mainSection.getStringList("layout"),
                ConfigUtils.getIconRecords(mainSection.getConfigurationSection("icons")),
                new CartProductIconRecord(
                        cartIconSection.getString("format.name", "<dark_gray>Name: <reset>{name} <dark_gray>x <white>{amount}"),
                        cartIconSection.getStringList("format.lore"),
                        TextUtils.parseTimeToTicks(cartIconSection.getString("update-period", "1s")),
                        EnumUtils.getEnumFromName(ClickType.class, cartIconSection.getString("features.add-1-stack")),
                        EnumUtils.getEnumFromName(ClickType.class, cartIconSection.getString("features.remove-1-stack")),
                        EnumUtils.getEnumFromName(ClickType.class, cartIconSection.getString("features.remove-all")),
                        EnumUtils.getEnumFromName(ClickType.class, cartIconSection.getString("features.input-in-anvil"))
                )
        );
    }

    public static SoundRecord getSoundRecord(String soundKey) {
        String soundData = cartGUIConfig.getString("sounds." + soundKey);
        return RecordUtils.fromSoundData(soundData);
    }
}
