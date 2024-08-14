package cn.encmys.ykdz.forest.dailyshop.api.utils;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.SkullMeta;
import org.bukkit.profile.PlayerProfile;
import org.bukkit.profile.PlayerTextures;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.UUID;

public class SkullUtils {
    public static ItemStack getSkullFromURL(String url) {
        try {
            ItemStack item = new ItemStack(Material.PLAYER_HEAD);
            SkullMeta meta = (SkullMeta) item.getItemMeta();

            if (meta == null) {
                return item;
            }

            meta.setOwningPlayer(Bukkit.getOfflinePlayer(UUID.fromString(url)));
            item.setItemMeta(meta);
            return item;
        } catch (IllegalArgumentException ignored) {
            if (url.length() >= 16) {
                url = url.toLowerCase();
                url = url.replace("https://textures.minecraft.net/texture/", "");

                ItemStack item = new ItemStack(Material.PLAYER_HEAD);
                ItemMeta meta = item.getItemMeta();
                SkullMeta skullMeta = (SkullMeta) meta;

                if (meta == null) {
                    return item;
                }

                PlayerProfile pp = Bukkit.createPlayerProfile(UUID.randomUUID());
                PlayerTextures pt = pp.getTextures();

                try {
                    pt.setSkin(new URL("https://textures.minecraft.net/texture/" + url));
                } catch (MalformedURLException e) {
                    LogUtils.error(e.getMessage());
                }

                pp.setTextures(pt);
                skullMeta.setOwnerProfile(pp);
                item.setItemMeta(meta);

                return item;
            } else {
                ItemStack item = new ItemStack(Material.PLAYER_HEAD);
                SkullMeta meta = (SkullMeta) item.getItemMeta();

                if (meta == null) {
                    return item;
                }

                Player player = Bukkit.getPlayer(url);

                if (player == null) {
                    return item;
                }

                meta.setOwningPlayer(Bukkit.getOfflinePlayer(player.getUniqueId()));
                item.setItemMeta(meta);
                return item;
            }
        }
    }
}
