package org.brokenarrow.lootboxes.untlity;

import com.google.common.base.Enums;
import org.brokenarrow.lootboxes.Lootboxes;
import org.bukkit.Sound;
import org.bukkit.entity.Player;

import java.util.logging.Level;

public class PlaySound {

    public static void playSound(Player player, String stringSound) {
        if (stringSound == null) return;
        try {
            Sound sound = Enums.getIfPresent(Sound.class, stringSound).orNull();
            player.playSound(player.getLocation(), sound != null ? sound : Sound.BLOCK_CHEST_LOCKED, 0.5F, 1.0F);
        } catch (ClassCastException ignore) {
            try {
                player.playSound(player.getLocation(), Sound.valueOf(stringSound.toUpperCase()), 0.5F, 1.0F);
            } catch (NoSuchMethodError | IllegalArgumentException e) {
                Lootboxes.getInstance().getLogger().log(Level.INFO, "Did not found the sound: `" + stringSound + "' you set in the config.");
            }
        }
    }

    public static void playSound(Player player, String stringSound, float volume) {
        if (stringSound == null) return;
        try {
            Sound sound = Enums.getIfPresent(Sound.class, stringSound).orNull();
            if (sound != null) {
                player.playSound(player.getLocation(), sound, volume, 1.0F);
            }
        } catch (ClassCastException ignore) {
        }
    }
}
