package cn.encmys.ykdz.forest.dailyshop.api.utils;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.SkullMeta;
import org.bukkit.profile.PlayerProfile;
import org.bukkit.profile.PlayerTextures;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.UUID;

public class SkullUtils {
    public static ItemStack generateSkullFromURLTexture(String url) {
        try {
            ItemStack item = new ItemStack(Material.PLAYER_HEAD);
            SkullMeta meta = (SkullMeta) item.getItemMeta();
            meta.setOwningPlayer(Bukkit.getOfflinePlayer(UUID.fromString(url)));
            item.setItemMeta(meta);
            return item;
        } catch (IllegalArgumentException ignored) {
            if (url.length() >= 16) {
                url = url.toLowerCase();
                url = url.replace("http://textures.minecraft.net/texture/", "");
                url = url.replace("https://textures.minecraft.net/texture/", "");

                ItemStack item = new ItemStack(Material.PLAYER_HEAD);
                ItemMeta meta = item.getItemMeta();
                SkullMeta skullMeta = (SkullMeta) meta;
                PlayerProfile pp = Bukkit.createPlayerProfile(UUID.fromString("4fbecd49-c7d4-4c18-8410-adf7a7348728"));
                PlayerTextures pt = pp.getTextures();

                try {
                    pt.setSkin(new URL("http://textures.minecraft.net/texture/" + url));
                } catch (MalformedURLException e) {
                    e.printStackTrace();
                }

                pp.setTextures(pt);
                skullMeta.setOwnerProfile(pp);
                meta = skullMeta;
                item.setItemMeta(meta);

                return item;
            } else {
                ItemStack item = new ItemStack(Material.PLAYER_HEAD);
                SkullMeta meta = (SkullMeta) item.getItemMeta();
                meta.setOwningPlayer(Bukkit.getOfflinePlayer(Bukkit.getPlayer(url).getUniqueId()));
                item.setItemMeta(meta);
                return item;
            }
        }
    }
}
