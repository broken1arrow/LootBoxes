package org.brokenarrow.lootboxes.untlity;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.util.NumberConversions;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.LinkedHashMap;
import java.util.Map;

public class LocationWrapper {

	private final double xLocation;
	private final double yLocation;
	private final double zLocation;
	private float yaw;
	private float pitch;
	private final String world;
	private Location location;
	private final boolean setYawPitch;

	public LocationWrapper(Location location, boolean setYawPitch) {
		this.setYawPitch = setYawPitch;
		this.xLocation = location.getX();
		this.yLocation = location.getY();
		this.zLocation = location.getZ();
		if (setYawPitch) {
			this.yaw = location.getYaw();
			this.pitch = location.getPitch();
		}
		if (location.getWorld() != null)
			this.world = location.getWorld().getName();
		else this.world = null;
	}

	public LocationWrapper(String path, Map<String, Object> map, boolean setYawPitch) {
		this.setYawPitch = setYawPitch;
	 	Object object = map.get(path);
		 if (object instanceof Map){
			 Map<String, Object> objectMap = (Map<String, Object>) object;
			 this.xLocation = NumberConversions.toDouble(objectMap.get("x"));
			 this.yLocation = NumberConversions.toDouble(objectMap.get( "y"));
			 this.zLocation= NumberConversions.toDouble(objectMap.get( "z"));
			 if (setYawPitch) {
				 this.yaw = NumberConversions.toFloat(objectMap.get( "yaw"));
				 this.pitch = NumberConversions.toFloat(objectMap.get( "pitch"));
			 }
			 this.world = (String) objectMap.get("world");
		} else {
			 path = path == null || path.isEmpty() ? "" : path + ".";
			 this.xLocation = NumberConversions.toDouble(map.get(path + "x"));
			 this.yLocation = NumberConversions.toDouble(map.get(path + "y"));
			 this.zLocation = NumberConversions.toDouble(map.get(path + "z"));
			 if (setYawPitch) {
				 this.yaw = NumberConversions.toFloat(map.get(path + "yaw"));
				 this.pitch = NumberConversions.toFloat(map.get(path + "pitch"));
			 }
			 this.world = (String) map.get(path + "world");
		 }
	}

	@Nullable
	public Location getLocation() {
		World world = Bukkit.getWorld(this.world);
		if (world != null)
			if (this.setYawPitch)
				location = new Location(world, this.xLocation, this.yLocation, this.zLocation, this.yaw, this.pitch);
			else
				location = new Location(world, this.xLocation, this.yLocation, this.zLocation);
		return location;
	}

	/**
	 * Creates a Map representation of this class.
	 * <p>
	 *
	 * @return Map containing the current state of this class
	 */
	@NotNull
	public Map<String, Object> serialize() {
		final Map<String, Object> keysData = new LinkedHashMap<>();
		keysData.put("world", this.world);
		keysData.put("x", this.xLocation);
		keysData.put("y", this.yLocation);
		keysData.put("z", this.zLocation);
		if (this.setYawPitch) {
			keysData.put("yaw", this.yaw);
			keysData.put("pitch", this.pitch);
		}
		return keysData;
	}

	@Override
	public String toString() {
		return world
				+ " " + (int)xLocation
				+ " " + (int)yLocation
				+ " " + (int)zLocation +
		(this.setYawPitch ? " " + yaw + " " + pitch: "");
	}
}
