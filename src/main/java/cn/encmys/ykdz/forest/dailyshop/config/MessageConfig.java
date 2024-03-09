package cn.encmys.ykdz.forest.dailyshop.config;

import cn.encmys.ykdz.forest.dailyshop.DailyShop;
import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.io.IOException;

public class MessageConfig {
    private static final DailyShop plugin = DailyShop.getInstance();
    private static YamlConfiguration config;

    public static String messages_prefix;
    public static String messages_command_reload;
    public static String messages_command_restock;
    public static String messages_command_save;
    public static int version;

    public static void load() {
        File file = new File(plugin.getDataFolder(), "lang/" + Config.language + ".yml");
        config = new YamlConfiguration();

        try {
            config.load(file);
            setUp();
        } catch (IOException | InvalidConfigurationException error) {
            error.printStackTrace();
        }
    }

    private static void setUp() {
        messages_prefix = config.getString("messages.prefix", "<gold>DailyShop <gray>-");
        messages_command_reload = config.getString("messages.command.reload", "<lime>Successfully reload the plugin!");
        messages_command_restock = config.getString("messages.command.restock", "<lime>Successfully restock shop {shop} <lime>manually!");
        messages_command_save = config.getString("messages.command.save", "<lime>Successfully save all shop data manually!");
        version = config.getInt("version");
    }

    public static YamlConfiguration getConfig() {
        return config;
    }
}
