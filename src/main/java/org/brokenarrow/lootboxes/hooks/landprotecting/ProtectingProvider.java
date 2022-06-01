package org.brokenarrow.lootboxes.hooks.landprotecting;

import org.bukkit.Location;
import org.bukkit.entity.Player;

public interface ProtectingProvider {


	default boolean isAllowedToSpawnContainer(Location location) {
		return false;
	}

	default boolean isProtected(Player player, Location location) {
		return false;
	}

	default boolean isProtectedFromBreak(Player player, Location location) {
		return false;
	}

	default boolean isProtectedFromExplode(Location location) {
		return false;
	}
}
