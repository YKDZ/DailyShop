package cn.encmys.ykdz.forest.hyphashop.config;

import cn.encmys.ykdz.forest.hyphashop.api.HyphaShop;
import cn.encmys.ykdz.forest.hyphashop.api.shop.cashier.record.MerchantRecord;
import cn.encmys.ykdz.forest.hyphashop.config.record.gui.ProductIconRecord;
import cn.encmys.ykdz.forest.hyphashop.config.record.gui.ShopGUIRecord;
import cn.encmys.ykdz.forest.hyphashop.config.record.misc.SoundRecord;
import cn.encmys.ykdz.forest.hyphashop.config.record.shop.ShopSettingsRecord;
import cn.encmys.ykdz.forest.hyphashop.utils.*;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.event.inventory.ClickType;
import org.jetbrains.annotations.NotNull;
import xyz.xenondevs.invui.gui.Markers;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class ShopConfig {
    private static final String path = HyphaShop.INSTANCE.getDataFolder() + "/shop";
    private static final HashMap<String, YamlConfiguration> configs = new HashMap<>();

    public static void load() {
        File directory = new File(path);

        if (!directory.exists() || !directory.isDirectory()) {
            directory.getParentFile().mkdirs();
        }

        File[] files = directory.listFiles();
        if (files != null) {
            for (File file : files) {
                if (file.isFile() && file.getName().endsWith(".yml")) {
                    YamlConfiguration config = new YamlConfiguration();
                    try {
                        config.load(file);
                        configs.put(file.getName().replace(".yml", ""), config);
                    } catch (IOException | InvalidConfigurationException error) {
                        LogUtils.error(error.getMessage());
                    }
                }
            }
        }
    }

    public static YamlConfiguration getConfig(String shopId) {
        return configs.get(shopId);
    }

    @NotNull
    public static List<String> getAllId() {
        return new ArrayList<>(configs.keySet());
    }

    public static ShopSettingsRecord getShopSettingsRecord(String shopId) {
        return new ShopSettingsRecord(
                getConfig(shopId).getInt("settings.size", 16),
                getConfig(shopId).getString("settings.name", "<red>Shop name not found!"),
                getConfig(shopId).getBoolean("settings.auto-restock.enabled"),
                TextUtils.parseTimeToTicks(getConfig(shopId).getString("settings.auto-restock.period")),
                getMerchantRecord(shopId),
                getConfig(shopId).getString("settings.context", "")
        );
    }

    @NotNull
    public static List<String> getAllProductsId(String shopId) {
        return getConfig(shopId).getStringList("products");
    }

    @NotNull
    public static MerchantRecord getMerchantRecord(@NotNull String shopId) {
        return new MerchantRecord(
                getConfig(shopId).getDouble("settings.merchant.balance", -1d),
                getConfig(shopId).getBoolean("settings.merchant.supply", false),
                getConfig(shopId).getBoolean("settings.merchant.overflow", false),
                getConfig(shopId).getBoolean("settings.merchant.inherit", false)
        );
    }

    @NotNull
    public static ShopGUIRecord getShopGUIRecord(@NotNull String shopId) {
        ConfigurationSection mainSection = getConfig(shopId).getConfigurationSection("shop-gui");
        if (mainSection == null) {
            throw new RuntimeException("Attempted to read gui information, but the configuration section is empty.");
        }
        ConfigurationSection productIconSection = mainSection.getConfigurationSection("product-icon");
        if (productIconSection == null) {
            throw new RuntimeException("Attempted to read gui information, but the configuration section is empty.");
        }
        return new ShopGUIRecord(
                mainSection.getString("title", ""),
                TextUtils.parseTimeToTicks(mainSection.getString("title-update-period", "0s")),
                mainSection.contains("scroll-mode") ? mainSection.getString("scroll-mode", "HORIZONTAL").equals("HORIZONTAL") ? Markers.CONTENT_LIST_SLOT_HORIZONTAL : Markers.CONTENT_LIST_SLOT_VERTICAL : null,
                mainSection.contains("page-mode") ? mainSection.getString("page-mode", "HORIZONTAL").equals("HORIZONTAL") ? Markers.CONTENT_LIST_SLOT_HORIZONTAL : Markers.CONTENT_LIST_SLOT_VERTICAL : null,
                mainSection.getStringList("layout"),
                ConfigUtils.getIconDecorators(mainSection.getConfigurationSection("icons")),
                new ProductIconRecord(
                        productIconSection.getString("format.name", "<dark_gray>Name: <reset>{name} <dark_gray>x <white>{amount}"),
                        productIconSection.getStringList("format.lore"),
                        productIconSection.getString("format.bundle-contents-line", " <dark_gray>- <white>{name} <gray>x <white>{amount}"),
                        productIconSection.getString("misc.disabled-price", "<red>✘"),
                        TextUtils.parseTimeToTicks(productIconSection.getString("update-period", "3s")),
                        EnumUtils.getEnumFromName(ClickType.class, productIconSection.getString("features.sell-to")),
                        EnumUtils.getEnumFromName(ClickType.class, productIconSection.getString("features.buy-from")),
                        EnumUtils.getEnumFromName(ClickType.class, productIconSection.getString("features.buy-all-from")),
                        EnumUtils.getEnumFromName(ClickType.class, productIconSection.getString("features.add-1-to-cart")),
                        EnumUtils.getEnumFromName(ClickType.class, productIconSection.getString("features.remove-1-from-cart")),
                        EnumUtils.getEnumFromName(ClickType.class, productIconSection.getString("features.remove-all-from-cart"))
                )
        );
    }

    /**
     * @param shopId   Shop to get sound config from
     * @param soundKey Format like "sell-to.success" or "buy-all-from.failure"
     * @return SoundRecord
     */
    @NotNull
    public static SoundRecord getSoundRecord(@NotNull String shopId, @NotNull String soundKey) {
        String soundData = getConfig(shopId).getString("sounds." + soundKey);
        return RecordUtils.fromSoundData(soundData == null ? "" : soundData);
    }
}
