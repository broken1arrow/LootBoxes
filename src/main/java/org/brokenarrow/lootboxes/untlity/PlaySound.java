package org.brokenarrow.lootboxes.untlity;

import com.google.common.base.Enums;
import org.bukkit.Sound;
import org.bukkit.entity.Player;

public class PlaySound {

	public static void playSound(Player player, String stringSound) {
		if (stringSound == null) return;
		Sound sound = Enums.getIfPresent(Sound.class, stringSound).orNull();
		if (sound != null) {
			player.playSound(player, sound, 0.5F, 1.0F);
		}
	}

	public static void playSound(Player player, String stringSound, float volume) {
		if (stringSound == null) return;
		Sound sound = Enums.getIfPresent(Sound.class, stringSound).orNull();
		if (sound != null) {
			player.playSound(player, sound, volume, 1.0F);
		}
	}
}
