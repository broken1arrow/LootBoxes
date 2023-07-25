package org.brokenarrow.lootboxes.settings;

import org.bukkit.Location;

public final class YamlUtil {

	public static String serializeLoc(final Location loc) {
		return serializeLoc(loc, false);
	}

	public static String serializeLoc(final Location loc, boolean addPitch) {
		if (loc == null) return null;
		String world;
		if (loc.getWorld() == null)
			world = loc.getWorld() + "";
		else
			world = loc.getWorld().getName();
		if (!addPitch)
			return world + " " + loc.getBlockX() + " " + loc.getBlockY() + " " + loc.getBlockZ();

		return world + " " + loc.getX() + " " + loc.getY() + " " + loc.getZ() + (loc.getPitch() != 0F || loc.getYaw() != 0F ? " " + loc.getYaw() + " " + loc.getPitch() : "");
	}
}