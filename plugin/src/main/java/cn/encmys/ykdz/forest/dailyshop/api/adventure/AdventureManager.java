package cn.encmys.ykdz.forest.dailyshop.api.adventure;

import cn.encmys.ykdz.forest.dailyshop.api.DailyShop;
import net.kyori.adventure.key.Key;
import net.kyori.adventure.platform.bukkit.BukkitAudiences;
import net.kyori.adventure.sound.Sound;
import net.kyori.adventure.text.Component;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public abstract class AdventureManager {
    protected final BukkitAudiences adventure;

    public AdventureManager(DailyShop plugin) {
        this.adventure = BukkitAudiences.create(plugin);
    }

    public abstract void close();

    public abstract List<Component> getComponentFromMiniMessage(List<String> texts);

    public abstract Component getComponentFromMiniMessage(String text);

    public abstract void sendMessage(CommandSender sender, String s);

    public abstract void sendMessageWithPrefix(CommandSender sender, String s);

    public abstract void sendPlayerMessage(Player player, String s);

    public abstract void sendConsoleMessage(String s);

    public abstract void sendSound(Player player, Sound.Source source, Key key, float volume, float pitch);

    public abstract void sendSound(Player player, Sound sound);

    public abstract List<String> componentToLegacy(List<Component> components);

    public abstract String componentToLegacy(Component component);

    public abstract boolean isColorCode(char c);

    public abstract String legacyToMiniMessage(@NotNull String legacy);
}
