package org.brokenarrow.lootboxes.untlity;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;

public class DeSerialize {

	public static Location isLocation(Object raw) {
		String[] parts;
		if (!raw.toString().contains(" "))
			return null;
		else {
			int length = (parts = raw.toString().split(" ")).length;
			if (length == 4) {
				final String world = parts[0];
				final World bukkitWorld = Bukkit.getWorld(world);
				if (bukkitWorld == null)
					return null;
				if (!parts[1].matches("[-+]?\\d+") && !parts[2].matches("[-+]?\\d+") && !parts[3].matches("[-+]?\\d+"))
					return null;
				else {
					int x = Integer.parseInt(parts[1]), y = Integer.parseInt(parts[2]), z = Integer.parseInt(parts[3]);
					return new Location(bukkitWorld, x, y, z);
				}
			}
		}
		return null;
	}
}
