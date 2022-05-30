package org.brokenarrow.lootboxes.untlity;

import com.google.common.base.Enums;
import org.bukkit.Sound;
import org.bukkit.entity.Player;

public class PlaySound {

	public static void playSound(Player player, String stringSound) {
		if (stringSound == null) return;
		Sound sound = Enums.getIfPresent(Sound.class, stringSound).orNull();

		player.playSound(player.getLocation(), sound != null ? sound : Sound.BLOCK_CHEST_LOCKED, 0.5F, 1.0F);
	}

	public static void playSound(Player player, String stringSound, float volume) {
		if (stringSound == null) return;
		Sound sound = Enums.getIfPresent(Sound.class, stringSound).orNull();
		if (sound != null) {
			player.playSound(player.getLocation(), sound, volume, 1.0F);
		}
	}
}
