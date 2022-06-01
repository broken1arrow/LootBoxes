package org.brokenarrow.lootboxes.untlity;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Player;

public class TeleportPlayer {

	private final Player player;
	private final Location locTo;
	private Location finalTeleportLoc;

	public TeleportPlayer(Player player, Location locTo) {
		this.player = player;
		this.locTo = locTo;
	}

	public boolean teleportPlayer() {
		Location location = checkLocation();
		if (location != null) {
			this.player.teleport(location);
			return true;
		}
		return false;
	}

	public Location checkLocation() {
		Location locationclone = this.locTo.clone();
		Location yDirection = checkInYderection(locationclone);
		if (yDirection != null) {
			this.finalTeleportLoc = yDirection;
			return yDirection;
		}
		Location xDirection = checkInXderection(locationclone);
		if (xDirection != null) {
			this.finalTeleportLoc = xDirection;
			return xDirection;
		}
		Location zDirection = checkInZderection(locationclone);
		if (zDirection != null) {
			this.finalTeleportLoc = zDirection;
			return zDirection;
		} else {
			this.finalTeleportLoc = this.locTo;
			return locationclone.clone().add(0.5, 0.0, 0.5);
		}
	}

	public Location getFinalTeleportLoc() {
		return finalTeleportLoc;
	}

	public Location checkInZderection(Location location) {
		Location locationclone = location.clone().add(0, 0, 1);
		boolean containsAir = true;
		for (int y = 0; y < 2; y++)
			if (locationclone.add(0, y, 0).getBlock().getType() != Material.AIR) {
				containsAir = false;
				break;
			}
		if (containsAir) {
			locationclone = locationclone.add(0.5, 0.0, 0.5);
			locationclone.setYaw(-180);
			locationclone.setPitch(20);
			return locationclone;
		}
		containsAir = true;
		locationclone = location.clone().add(0, 0, -1);
		for (int y = 0; y < 2; y++)
			if (locationclone.add(0, y, 0).getBlock().getType() != Material.AIR) {
				containsAir = false;
				break;
			}
		if (containsAir) {
			locationclone = locationclone.add(0.5, 0.0, 0.5);
			locationclone.setYaw(0);
			locationclone.setPitch(20);
			return locationclone;
		}
		return null;
	}

	public Location checkInXderection(Location location) {
		Location locationclone = location.clone().add(1, 0, 0);
		boolean containsAir = true;
		for (int y = 0; y < 2; y++) {
			if (locationclone.add(0, y, 0).getBlock().getType() != Material.AIR) {
				containsAir = false;
				break;
			}
		}
		if (containsAir) {
			locationclone = locationclone.add(0.5, 0.0, 0.5);
			locationclone.setYaw(90);
			locationclone.setPitch(20);
			return locationclone;
		}
		containsAir = true;
		locationclone = location.clone().add(-1, 0, 0);
		for (int y = 0; y < 2; y++)
			if (locationclone.add(0, y, 0).getBlock().getType() != Material.AIR) {
				containsAir = false;
				break;
			}
		if (containsAir) {
			locationclone = locationclone.add(0.5, 0.0, 0.5);
			locationclone.setYaw(-90);
			locationclone.setPitch(20);
			return locationclone;
		}
		return null;
	}

	public Location checkInYderection(Location location) {
		Location locationclone = location.clone().add(0, 1, 0);
		boolean containsAir = true;
		for (int y = 1; y < 2; y++) {
			if (locationclone.add(0, y, 0).getBlock().getType() != Material.AIR) {
				containsAir = false;
				break;
			}
		}
		if (containsAir) {
			locationclone = locationclone.add(0.5, 0.0, 0.5);
			locationclone.setYaw(-90);
			locationclone.setPitch(20);
			return locationclone;
		}
		return null;
	}
}
