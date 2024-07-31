package cn.encmys.ykdz.forest.dailyshop.api.config;

import cn.encmys.ykdz.forest.dailyshop.api.DailyShop;
import cn.encmys.ykdz.forest.dailyshop.api.config.record.shop.*;
import cn.encmys.ykdz.forest.dailyshop.api.shop.cashier.record.MerchantRecord;
import cn.encmys.ykdz.forest.dailyshop.api.utils.TextUtils;
import org.bukkit.Sound;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.configuration.file.YamlConfiguration;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import xyz.xenondevs.invui.gui.structure.Markers;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class ShopConfig {
    private static String path = DailyShop.INSTANCE.getDataFolder() + "/shop";
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
                        error.printStackTrace();
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

    public static int getSize(String shopId) {
        return getConfig(shopId).getInt("settings.size");
    }

    public static String getName(String shopId) {
        return getConfig(shopId).getString("settings.name");
    }

    public static long getRestockPeriod(String shopId) {
        return TextUtils.parseTimeToTicks(getConfig(shopId).getString("settings.restock.period", "10m"));
    }

    public static boolean getRestockEnabled(String shopId) {
        return getConfig(shopId).getBoolean("settings.restock.enabled", false);
    }

    public static String getShopGUITitle(String shopId) {
        return getShopGUISection(shopId).getString("title");
    }

    public static String getHistoryGUITitle(String shopId) {
        return getHistoryGuiSection(shopId).getString("title");
    }

    public static String getProductNameFormat(String shopId) {
        return getShopGUISection(shopId).getString("product-icon.format.name", "{name}");
    }

    public static String getBundleContentsLineFormat(String shopId) {
        return getShopGUISection(shopId).getString("product-icon.format.bundle-contents-line", "<dark_gray>- {name} x {amount}");
    }

    @NotNull
    public static List<String> getProductLoreFormat(String shopId) {
        return getShopGUISection(shopId).getStringList("product-icon.format.lore");
    }

    public static String getDisabledPrice(String shopId) {
        return getShopGUISection(shopId).getString("product-icon.misc.disabled-price");
    }

    public static ConfigurationSection getShopGUISection(String shopId) {
        return getConfig(shopId).getConfigurationSection("shop-gui");
    }

    @NotNull
    public static List<String> getAllProductsId(String shopId) {
        return getConfig(shopId).getStringList("products");
    }

    public static String getRestockNotification(String shopId) {
        return getConfig(shopId).getString("messages.notification");
    }

    public static Sound getBuySound(String shopId) {
        String sound = getConfig(shopId).getString("sounds.buy", "ENTITY_VILLAGER_YES").toUpperCase();
        return Sound.valueOf(sound);
    }

    public static Sound getSellSound(String shopId) {
        String sound = getConfig(shopId).getString("sounds.sell", "ENTITY_VILLAGER_YES").toUpperCase();
        return Sound.valueOf(sound);
    }

    public static ConfigurationSection getHistoryGuiSection(String shopId) {
        return getConfig(shopId).getConfigurationSection("history-gui");
    }

    @NotNull
    public static MerchantRecord getMerchant(@NotNull String shopId) {
        return new MerchantRecord(
                getConfig(shopId).getDouble("settings.merchant.balance", -1d),
                getConfig(shopId).getBoolean("settings.merchant.supply", false),
                getConfig(shopId).getBoolean("settings.merchant.overflow", false),
                getConfig(shopId).getBoolean("settings.merchant.inherit", false)
        );
    }

    @NotNull
    public static HistoryGUIRecord getHistoryGUIRecord(@NotNull String shopId) {
        ConfigurationSection mainSection = getConfig(shopId).getConfigurationSection("history-gui");
        if (mainSection == null) {
            throw new RuntimeException("Attempted to read gui information, but the configuration section is empty.");
        }
        ConfigurationSection productIconSection = mainSection.getConfigurationSection("history-icon");
        if (productIconSection == null) {
            throw new RuntimeException("Attempted to read gui information, but the configuration section is empty.");
        }
        return new HistoryGUIRecord(
                mainSection.getString("title", "{shop}"),
                mainSection.getString("scroll-mode", "HORIZONTAL").equals("HORIZONTAL") ? Markers.CONTENT_LIST_SLOT_HORIZONTAL : Markers.CONTENT_LIST_SLOT_VERTICAL,
                mainSection.getStringList("layout"),
                getIconRecords(mainSection.getConfigurationSection("icons")),
                new HistoryIconRecord(
                        productIconSection.getString("format.name", "{date}"),
                        productIconSection.getStringList("format.lore"),
                        productIconSection.getString("format.order-contents-line", " <dark_gray>- <white>{name} <gray>x <white>{amount}")
                )
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
                mainSection.getString("title", "{shop}"),
                mainSection.getString("scroll-mode", "HORIZONTAL").equals("HORIZONTAL") ? Markers.CONTENT_LIST_SLOT_HORIZONTAL : Markers.CONTENT_LIST_SLOT_VERTICAL,
                mainSection.getStringList("layout"),
                getIconRecords(mainSection.getConfigurationSection("icons")),
                new ProductIconRecord(
                        productIconSection.getString("format.name", "<dark_gray>Name: <reset>{name} <dark_gray>x <white>{amount}"),
                        productIconSection.getStringList("format.lore"),
                        productIconSection.getString("format.bundle-contents-line", " <dark_gray>- <white>{name} <gray>x <white>{amount}"),
                        productIconSection.getString("misc.disabled-price", "<red>✘")
                )
        );
    }

    @NotNull
    public static CartGUIRecord getCartGUIRecord(@NotNull String shopId) {
        ConfigurationSection mainSection = getConfig(shopId).getConfigurationSection("cart-gui");
        if (mainSection == null) {
            throw new RuntimeException("Attempted to read gui information, but the configuration section is empty.");
        }
        ConfigurationSection cartIconSection = mainSection.getConfigurationSection("cart-product-icon");
        if (cartIconSection == null) {
            throw new RuntimeException("Attempted to read gui information, but the configuration section is empty.");
        }
        return new CartGUIRecord(
                mainSection.getString("title", "{shop}"),
                mainSection.getString("scroll-mode", "HORIZONTAL").equals("HORIZONTAL") ? Markers.CONTENT_LIST_SLOT_HORIZONTAL : Markers.CONTENT_LIST_SLOT_VERTICAL,
                mainSection.getStringList("layout"),
                getIconRecords(mainSection.getConfigurationSection("icons")),
                new CartProductIconRecord(
                        cartIconSection.getString("format.name", "<dark_gray>Name: <reset>{name} <dark_gray>x <white>{amount}"),
                        cartIconSection.getStringList("format.lore")
                )
        );
    }

    @Nullable
    public static IconRecord getIconRecord(@NotNull ConfigurationSection iconsSection, char iconKey) {
        ConfigurationSection iconSection = iconsSection.getConfigurationSection("icons." + iconKey);
        if (iconSection == null) {
            return null;
        }
        return getIconRecord(iconKey, iconSection);
    }

    @NotNull
    public static List<IconRecord> getIconRecords(@Nullable ConfigurationSection iconsSection) {
        if (iconsSection == null) {
            throw new RuntimeException("Attempted to read gui information, but the icons configuration section is empty.");
        }
        List<IconRecord> icons = new ArrayList<>();
        for (String key : iconsSection.getKeys(false)) {
            char iconKey = key.charAt(0);
            ConfigurationSection iconSection = iconsSection.getConfigurationSection(key);

            if (iconSection == null) {
                continue;
            }

            icons.add(getIconRecord(iconKey, iconSection));
        }
        return icons;
    }

    @NotNull
    public static IconRecord getIconRecord(char iconKey, ConfigurationSection iconSection) {
        return new IconRecord(
                iconKey,
                iconSection.getString("item", "DIRT"),
                iconSection.getString("name", null),
                iconSection.getStringList("lore"),
                iconSection.getInt("amount", 1),
                TextUtils.parseTimeToTicks(iconSection.getString("update-period", "0s")),
                iconSection.getInt("custom-model-data"),
                iconSection.getConfigurationSection("commands"),
                iconSection.getStringList("item-flags"),
                iconSection.getStringList("banner-patterns"),
                iconSection.getStringList("firework-effects"),
                iconSection.getStringList("potion-effects"),
                iconSection.getConfigurationSection("features")
        );
    }
}
