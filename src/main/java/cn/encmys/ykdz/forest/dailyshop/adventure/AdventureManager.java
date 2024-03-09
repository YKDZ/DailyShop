package cn.encmys.ykdz.forest.dailyshop.adventure;

import cn.encmys.ykdz.forest.dailyshop.DailyShop;
import cn.encmys.ykdz.forest.dailyshop.config.MessageConfig;
import net.kyori.adventure.audience.Audience;
import net.kyori.adventure.key.Key;
import net.kyori.adventure.platform.bukkit.BukkitAudiences;
import net.kyori.adventure.sound.Sound;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.MiniMessage;
import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;

public class AdventureManager {
    private final BukkitAudiences adventure;

    public AdventureManager(DailyShop plugin) {
        this.adventure = BukkitAudiences.create(plugin);
    }

    public void close() {
        if (adventure != null)
            adventure.close();
    }

    public List<Component> getComponentFromMiniMessage(List<String> texts) {
        List<Component> result = new ArrayList<>();
        for (String text : texts) {
            result.add(getComponentFromMiniMessage(text));
        }
        return result;
    }

    public Component getComponentFromMiniMessage(String text) {
        if (text == null) {
            return Component.empty();
        }
        return MiniMessage.miniMessage().deserialize(text);
    }

    public void sendMessage(CommandSender sender, String s) {
        if (s == null) return;
        if (sender instanceof Player player) sendPlayerMessage(player, s);
        else if (sender instanceof ConsoleCommandSender) sendConsoleMessage(s);
    }

    public void sendMessageWithPrefix(CommandSender sender, String s) {
        sendMessage(sender, MessageConfig.messages_prefix + s);
    }

    public void sendPlayerMessage(Player player, String s) {
        if (s == null) return;
        Audience au = adventure.player(player);
        au.sendMessage(getComponentFromMiniMessage(s));
    }

    public void sendConsoleMessage(String s) {
        if (s == null) return;
        Audience au = adventure.sender(Bukkit.getConsoleSender());
        au.sendMessage(getComponentFromMiniMessage(s));
    }

    public void sendSound(Player player, Sound.Source source, Key key, float volume, float pitch) {
        Sound sound = Sound.sound(key, source, volume, pitch);
        Audience au = adventure.player(player);
        au.playSound(sound);
    }

    public void sendSound(Player player, Sound sound) {
        Audience au = adventure.player(player);
        au.playSound(sound);
    }

    public List<String> componentToLegacy(List<Component> components) {
        List<String> result = new ArrayList<>();
        for (Component component : components) {
            result.add(componentToLegacy(component));
        }
        return result;
    }

    public String componentToLegacy(Component component) {
        return LegacyComponentSerializer.legacySection().serialize(component);
    }
}
