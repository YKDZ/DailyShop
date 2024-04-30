package cn.encmys.ykdz.forest.dailyshop.product.factory;

import cn.encmys.ykdz.forest.dailyshop.DailyShop;
import cn.encmys.ykdz.forest.dailyshop.api.product.Product;
import cn.encmys.ykdz.forest.dailyshop.builder.BaseItemDecorator;
import cn.encmys.ykdz.forest.dailyshop.config.ProductConfig;
import cn.encmys.ykdz.forest.dailyshop.config.RarityConfig;
import cn.encmys.ykdz.forest.dailyshop.price.Price;
import cn.encmys.ykdz.forest.dailyshop.product.BundleProduct;
import cn.encmys.ykdz.forest.dailyshop.product.CommandProduct;
import cn.encmys.ykdz.forest.dailyshop.product.ItemProduct;
import cn.encmys.ykdz.forest.dailyshop.rarity.Rarity;
import cn.encmys.ykdz.forest.dailyshop.util.ConfigUtils;
import cn.encmys.ykdz.forest.dailyshop.util.LogUtils;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.YamlConfiguration;

import java.util.HashMap;
import java.util.Map;

public class ProductFactory {
    private static final HashMap<String, Product> allProducts = new HashMap<>();

    public ProductFactory() {
        for (String configId : ProductConfig.getAllPacksId()) {
            YamlConfiguration config = ProductConfig.getConfig(configId);
            ConfigurationSection products = config.getConfigurationSection("products");
            ConfigurationSection defaultSettings = config.getConfigurationSection("default-settings");

            // Empty product pack is allowed
            if (products == null) {
                continue;
            }

            for (String productId : products.getKeys(false)) {
                ConfigurationSection productSection = products.getConfigurationSection(productId);
                buildProduct(productId, productSection, defaultSettings);
            }
        }
    }

    public void buildProduct(String id, ConfigurationSection productSection, ConfigurationSection defaultSettings) {
        if (containsProduct(id)) {
            LogUtils.warn("Product ID is duplicated: " + id + ". Ignore this product.");
            return;
        }

        ConfigurationSection itemSection = productSection.getConfigurationSection("item");
        ConfigurationSection iconSection = productSection.getConfigurationSection("icon");

        // Price (can default)
        Price buyPrice = new Price(
                ConfigUtils.inheritPriceSection(productSection.getConfigurationSection("buy-price"), defaultSettings.getConfigurationSection("buy-price"))
        );
        Price sellPrice = new Price(
                ConfigUtils.inheritPriceSection(productSection.getConfigurationSection("sell-price"), defaultSettings.getConfigurationSection("sell-price"))
        );

        // Rarity (can default)
        Rarity rarity = DailyShop.RARITY_FACTORY.getRarity(productSection.getString( "rarity", defaultSettings.getString("rarity", RarityConfig.getAllId().get(0))));

        // Cacheable (can default)
        boolean isCacheable = productSection.getBoolean("cacheable", defaultSettings.getBoolean("cacheable", true));

        // Item (Only ItemProduct need it)
        BaseItemDecorator itemBuilder = null;
        if (productSection.contains("item")) {
            itemBuilder = BaseItemDecorator.get(itemSection.getString("base", "DIRT"), false);

            if (itemBuilder == null) {
                LogUtils.warn("Product " + id + " has invalid base setting. Please check it.");
                return;
            } else {
                itemBuilder
                        .setName(itemSection.getString("name"))
                        .setLore(itemSection.getStringList("lore"))
                        .setAmount(itemSection.getInt("amount", defaultSettings.getInt("item.amount", 1)))
                        .setItemFlags(itemSection.getStringList("item-flags"))
                        .setCustomModelData((Integer) itemSection.get("custom-model-data"))
                        .setPatternsData(itemSection.getStringList("banner-patterns"))
                        .setFireworkEffectData(itemSection.getStringList("firework-effects"));
            }
        }

        // Icon (use item section as fall back)
        if (iconSection == null) {
            if (itemSection != null) {
                iconSection = itemSection;
            } else {
                LogUtils.warn("Product " + id + " has invalid config.");
                return;
            }
        }

        // inherit
        if (itemSection != null) {
            // base
            if (!iconSection.contains("base")) {
                if (itemSection.isString("base")) {
                    iconSection.set("base", itemSection.getString("base"));
                } else {
                    LogUtils.warn("Can not find base item for product " + id + ".");
                    return;
                }
            }
            // name
            if (!iconSection.contains("name")) {
                if (itemSection.isString("name")) {
                    iconSection.set("name", itemSection.getString("name"));
                }
            }
            // amount
            if (!iconSection.contains("amount")) {
                if (itemSection.isInt("amount")) {
                    iconSection.set("amount", itemSection.getInt("amount"));
                } else if (defaultSettings.isInt("item.amount")) {
                    iconSection.set("amount", defaultSettings.getInt("item.amount"));
                }
            }
            // custom-model-data
            if (!iconSection.contains("custom-model-data")) {
                if (itemSection.isInt("custom-model-data")) {
                    iconSection.set("custom-model-data", itemSection.getInt("custom-model-data"));
                }
            }
            // banner-patterns
            if (!iconSection.contains("banner-patterns")) {
                if (itemSection.isList("banner-patterns")) {
                    iconSection.set("banner-patterns", itemSection.getStringList("banner-patterns"));
                }
            }
            // banner-patterns
            if (!iconSection.contains("firework-effects")) {
                if (itemSection.isList("firework-effects")) {
                    iconSection.set("firework-effects", itemSection.getStringList("firework-effects"));
                }
            }
        }

        BaseItemDecorator iconBuilder = BaseItemDecorator.get(iconSection.getString("base", "DIRT"), true);

        if (iconBuilder == null) {
            LogUtils.warn("Product " + id + " has invalid base setting. Please check it.");
            return;
        } else {
            iconBuilder
                    .setAmount(iconSection.getInt("amount", 1))
                    .setLore(iconSection.getStringList("lore").isEmpty() ? null : iconSection.getStringList("lore"))
                    .setName(iconSection.getString("name"))
                    .setItemFlags(iconSection.getStringList("item-flags"))
                    .setCustomModelData((Integer) iconSection.get("custom-model-data"))
                    .setPatternsData(iconSection.getStringList("banner-patterns"))
                    .setFireworkEffectData(iconSection.getStringList("firework-effects"));
        }

        if (productSection.contains("buy-commands") || productSection.contains("sell-commands")) {
            getAllProducts().put(id,
                    new CommandProduct(id, buyPrice, sellPrice, rarity, iconBuilder,
                            productSection.getStringList("buy-commands"),
                            productSection.getStringList("sell-commands")));
        } else if (productSection.contains("bundle-contents")) {
            Map<String, Integer> bundleContents = new HashMap<>();
            for (String contentData : productSection.getStringList("bundle-contents")) {
                String[] parsedContentData = contentData.split(":");
                if (parsedContentData.length == 1) {
                    bundleContents.put(parsedContentData[0], 1);
                } else if (parsedContentData.length == 2) {
                    bundleContents.put(parsedContentData[0], Integer.parseInt(parsedContentData[1]));
                } else {
                    LogUtils.warn("Product " + id + " has invalid bundle-contents. The invalid line is: " + contentData + ".");
                }
            }
            getAllProducts().put(id,
                    new BundleProduct(id, buyPrice, sellPrice, rarity, iconBuilder, bundleContents));
        } else {
            getAllProducts().put(id,
                    new ItemProduct(id, buyPrice, sellPrice, rarity, iconBuilder, itemBuilder, isCacheable));
        }
    }

    public static HashMap<String, Product> getAllProducts() {
        return allProducts;
    }

    public Product getProduct(String id) {
        return allProducts.get(id);
    }

    public boolean containsProduct(String id) {
        return allProducts.containsKey(id);
    }

    public void unload() {
        allProducts.clear();
    }
}