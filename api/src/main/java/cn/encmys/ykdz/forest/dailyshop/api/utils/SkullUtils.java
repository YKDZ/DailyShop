package cn.encmys.ykdz.forest.dailyshop.api.utils;

import com.mojang.authlib.GameProfile;
import com.mojang.authlib.properties.Property;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.SkullMeta;

import java.lang.reflect.Field;
import java.util.Base64;
import java.util.UUID;

public class SkullUtils {
    public static ItemStack generateSkullFromURLTexture(String url) {
        ItemStack skull = new ItemStack(Material.PLAYER_HEAD);
        SkullMeta skullMeta = (SkullMeta) skull.getItemMeta();

        GameProfile profile = new GameProfile(UUID.randomUUID(), null);
        String encodedData = Base64.getEncoder().encodeToString(("{\"textures\":{\"SKIN\":{\"url\":\"" + url + "\"}}}").getBytes());
        profile.getProperties().put("textures", new Property("textures", encodedData));

        try {
            Field profileField = null;
            if (skullMeta != null) {
                profileField = skullMeta.getClass().getDeclaredField("profile");
            }
            if (profileField != null) {
                profileField.setAccessible(true);
            }
            if (profileField != null) {
                profileField.set(skullMeta, profile);
            }
        } catch (NoSuchFieldException | IllegalAccessException e) {
            e.printStackTrace();
        }

        skull.setItemMeta(skullMeta);
        return skull;
    }
}
