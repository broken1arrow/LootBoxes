package org.brokenarrow.lootboxes.hooks.landprotecting;


import org.bukkit.Location;
import org.bukkit.plugin.Plugin;

public class LandsProtection implements ProtectingProvider {
	//private  LandsIntegration lands;

	public LandsProtection(Plugin plugin) {
/*		lands = new LandsIntegration(plugin);
		try {
			lands.registerFlag(new LandFlag(plugin, "Lootboxesspawn"));
		} catch (FlagConflictException exception) {
			exception.printStackTrace();
		}*/
	}

	@SuppressWarnings("ConstantConditions")
	@Override
	public boolean isAllowedToSpawnContainer(Location location) {
	/*	Area landsArea = lands.getAreaByLoc(location);
		Flag flag = Flags.get("Lootboxesspawn");
		if (landsArea == null) {
			if (!lands.isClaimed(location) && flag != null)
				return false;//lands.getLandWorld(location.getWorld()).hasFlag(location, (LandFlag) flag);
			return true;
		}*/
		return  false;
		//return flag == null || false;//landsArea.hasFlag((LandFlag) flag);

	}
}
